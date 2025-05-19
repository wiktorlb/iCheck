package dev.app.iCheck.repository;

import dev.app.iCheck.model.SSRCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SSRCodeRepository extends MongoRepository<SSRCode, String> {
    Optional<SSRCode> findByCode(String code);

    List<SSRCode> findByIsActiveTrue();

    List<SSRCode> findByCategory(String category);

    List<SSRCode> findByCategoryAndIsActiveTrue(String category);

    List<SSRCode> findByCodeContainingIgnoreCase(String code);

    List<SSRCode> findByDescriptionContainingIgnoreCase(String description);

    List<SSRCode> findByCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String code, String description);
}