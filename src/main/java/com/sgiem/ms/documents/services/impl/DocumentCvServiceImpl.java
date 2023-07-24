package com.sgiem.ms.documents.services.impl;

import com.sgiem.ms.documents.models.File;
import com.sgiem.ms.documents.repository.DocumentCvRepositories;
import com.sgiem.ms.documents.repository.GenericRepositories;
import com.sgiem.ms.documents.services.DocumentCvService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DocumentCvServiceImpl extends CrudServiceImpl<File, String> implements DocumentCvService {

    @Autowired
    private DocumentCvRepositories documentCvRepositories;

    @Override
    protected GenericRepositories<File, String> getRepo() {
        return documentCvRepositories;
    }

}
