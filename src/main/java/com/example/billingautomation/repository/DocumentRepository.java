package com.example.billingautomation.repository;

import java.util.Optional;

public interface DocumentRepository {
    <T> Optional<T> findById(String id, Class<T> type);
    boolean existsById(String id);
    void save(String id, Object document);
    void deleteById(String id);
    void backupDocument(String id);
}
