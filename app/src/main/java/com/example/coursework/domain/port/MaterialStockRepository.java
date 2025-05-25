package com.example.coursework.domain.port;

import com.example.coursework.domain.entity.MaterialStock;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MaterialStockRepository {
    MaterialStock save(MaterialStock material) throws SQLException;
    Optional<MaterialStock> findById(int id) throws SQLException;
    List<MaterialStock> findAll();
    void delete(MaterialStock material) throws SQLException;
}