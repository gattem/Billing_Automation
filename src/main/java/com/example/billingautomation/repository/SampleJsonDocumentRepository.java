package com.example.billingautomation.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class SampleJsonDocumentRepository implements DocumentRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path sampleRoot;

    public SampleJsonDocumentRepository() {
        this.sampleRoot = Paths.get(System.getProperty("sample.docs.path", "sample_couchbase_docs"));
    }

    @Override
    public <T> Optional<T> findById(String id, Class<T> type) {
        try {
            return Files.list(sampleRoot)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .map(this::readNode)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(node -> id.equals(node.path("id").asText()))
                    .map(node -> objectMapper.convertValue(node, type))
                    .findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsById(String id) {
        try {
            return Files.list(sampleRoot)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .map(this::readNode)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .anyMatch(node -> id.equals(node.path("id").asText()));
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void save(String id, Object document) {
        throw new UnsupportedOperationException("Save operation is not supported by sample repository yet.");
    }

    @Override
    public void deleteById(String id) {
        throw new UnsupportedOperationException("Delete operation is not supported by sample repository yet.");
    }

    @Override
    public void backupDocument(String id) {
        throw new UnsupportedOperationException("Backup operation is not supported by sample repository yet.");
    }

    private Optional<JsonNode> readNode(Path path) {
        try {
            String content = Files.readString(path);
            return Optional.of(objectMapper.readTree(content));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
