package com.sgiem.ms.documents.repository;

import com.sgiem.ms.documents.models.File;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentCvRepositories extends GenericRepositories<File, String> {
}
