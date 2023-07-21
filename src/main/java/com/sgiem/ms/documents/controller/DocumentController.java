package com.sgiem.ms.documents.controller;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.sgiem.ms.documents.api.v1.DocumentApi;
import com.sgiem.ms.documents.dto.FileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
public class DocumentController implements DocumentApi {

    @Value("${blob.connection-string}")
    String connectionString;

    @Value("${blob.container-name}")
    String containerName;
    @Autowired
    WebClient webClient;

    @Override
    public Mono<ResponseEntity<FileResponse>> uploadFile(Flux<Part> file, ServerWebExchange exchange) {
        return file
                .ofType(FilePart.class)
                .flatMap(filePart -> {
                    String fileName = "sgiemcv-" + UUID.randomUUID().toString() + "-" + filePart.filename();
                    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
                    BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
                    BlobClient blobClient = containerClient.getBlobClient(fileName);

                    File tempFile;
                    try {
                        tempFile = File.createTempFile("temp-", null);
                    } catch (IOException e) {
                        return Mono.error(e);
                    }

                    return filePart.transferTo(tempFile)
                            .then(Mono.fromCallable(() -> {
                                blobClient.uploadFromFile(tempFile.getAbsolutePath());
                                return blobClient.getBlobUrl();
                            }))
                            .doFinally(signalType -> {
                                try {
                                    Files.deleteIfExists(tempFile.toPath());
                                } catch (IOException e) {
                                    log.error("Error al crear el archivo temporal", e);
                                }
                            });
                })
                .next()
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(FileResponse.builder().url(e).build())
                );
    }

    @Override
    public Mono<ResponseEntity<Resource>> downloadFile(String documentName, ServerWebExchange exchange) {
        return webClient
                .get()
                .uri("https://sgiemstorage.blob.core.windows.net/sgiemcontainer/{documentName}", documentName)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(byte[].class)
                                .map(content -> {
                                    Resource resource = new ByteArrayResource(content);
                                    return ResponseEntity.ok()
                                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + documentName)
                                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                            .body(resource);
                                });
                    } else {
                        return Mono.just(ResponseEntity.status(response.statusCode()).build());
                    }
                });
    }
}
