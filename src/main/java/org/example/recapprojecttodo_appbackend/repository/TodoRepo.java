package org.example.recapprojecttodo_appbackend.repository;

import org.example.recapprojecttodo_appbackend.models.Todo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepo extends MongoRepository<Todo, String> {
}
