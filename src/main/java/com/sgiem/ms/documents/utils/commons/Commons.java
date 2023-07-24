package com.sgiem.ms.documents.utils.commons;

import com.sgiem.ms.documents.models.File;

import java.net.MalformedURLException;
import java.net.URL;

public class Commons {

    public static File convertToEntity(String code, String url) {
        int pos = url.lastIndexOf('/');
        String fileName = url.substring(pos + 1);

        return File.builder()
                .titulo(fileName.substring(0, fileName.lastIndexOf('.')))
                .tipoFile(fileName.substring(fileName.lastIndexOf('.') + 1))
                .code(code)
                .tipoDocumento("CV")
                .url(url)
                .build();
    }
}
