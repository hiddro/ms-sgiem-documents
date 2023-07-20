package com.sgiem.ms.documents.controller;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@RestController
@Slf4j
public class DocumentController {

    @Value("${blob.connection-string}")
    String connectionString;

    @Value("${blob.container-name}")
    String containerName;
    @Autowired
    WebClient webClient;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadFile(@RequestPart("file") FilePart filePart) {
        String fileName = "prefix-" + UUID.randomUUID().toString() + "-" + filePart.filename();
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(fileName);

        File tempFile;
        try {
            tempFile = File.createTempFile("temp-", null);
        } catch (IOException e) {
            return Mono.error(e);
        }

        Mono<Void> saveFileMono = filePart.transferTo(tempFile);

        return saveFileMono.then(Mono.fromCallable(() -> {
            blobClient.uploadFromFile(tempFile.getAbsolutePath());
            return blobClient.getBlobUrl();
        })).doFinally(signalType -> {
            try {
                Files.deleteIfExists(tempFile.toPath());
            } catch (IOException e) {
                log.error("Error al crear el archivo temporal", e);
            }
        });
    }

    @GetMapping("/download/{documentName}")
    public Mono<ResponseEntity<byte[]>> downloadDocument(@PathVariable String documentName) {
        return webClient
                .get()
                .uri("https://sgiemstorage.blob.core.windows.net/sgiemcontainer/{documentName}", documentName)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(byte[].class)
                                .map(content -> ResponseEntity.ok()
                                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + documentName)
                                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                        .body(content));
                    } else {
                        return Mono.just(ResponseEntity.status(response.statusCode()).build());
                    }
                });
    }
}
