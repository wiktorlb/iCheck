package dev.app.iCheck.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import dev.app.iCheck.model.BaggageId;

public interface BaggageIdRepository extends MongoRepository<BaggageId, String> {
}