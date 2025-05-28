package dev.app.iCheck.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import dev.app.iCheck.model.Destination;

public interface DestinationRepository extends MongoRepository<Destination, String> {
}
