package com.sgiem.ms.documents.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenericRepositories<T, ID> extends ReactiveMongoRepository<T, ID> {
}
