package com.example.coursework.domain.port;

import com.example.coursework.domain.entity.ToolCatalog;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ToolCatalogRepository {
    ToolCatalog save(ToolCatalog tool) throws SQLException;
    Optional<ToolCatalog> findById(int id)throws SQLException;
    List<ToolCatalog> findAll();
    void delete(ToolCatalog tool) throws SQLException;
}