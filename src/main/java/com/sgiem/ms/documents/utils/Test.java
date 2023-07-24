package com.sgiem.ms.documents.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class Test {

    public static void main(String[] args) {
        String urlStr = "https://sgiemstorage.blob.core.windows.net/sgiemcontainer/sgiemcv-34d27dd8-eef8-4e27-ab8b-934974202ed3-Cristina_Fuentes_CV.pdf";
        int urlStr2 = urlStr.lastIndexOf('/');


            String fileName = urlStr.substring(urlStr2 + 1);
            String formato = fileName.substring(fileName.lastIndexOf('.') + 1);
            String nombre = fileName.substring(0, fileName.lastIndexOf('.'));
            System.out.println(fileName); // Output: sgiemcv-34d27dd8-eef8-4e27-ab8b-934974202ed3-Cristina_Fuentes_CV.pdf

    }
}
