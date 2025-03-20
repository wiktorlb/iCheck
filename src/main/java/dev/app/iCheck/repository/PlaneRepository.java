package dev.app.iCheck.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import dev.app.iCheck.model.Plane;

public interface PlaneRepository extends MongoRepository<Plane, String> {
}
