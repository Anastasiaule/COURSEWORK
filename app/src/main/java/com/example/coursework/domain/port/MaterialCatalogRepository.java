package com.example.coursework.domain.port;

import com.example.coursework.domain.entity.MaterialCatalog;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MaterialCatalogRepository {
    MaterialCatalog save(MaterialCatalog material) throws SQLException;
    Optional<MaterialCatalog> findById(int id) throws SQLException;
    List<MaterialCatalog> findAll();
    void delete(MaterialCatalog material) throws SQLException;
}