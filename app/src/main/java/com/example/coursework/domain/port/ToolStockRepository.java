package com.example.coursework.domain.port;

import com.example.coursework.domain.entity.ToolStock;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ToolStockRepository {
    ToolStock save(ToolStock tool) throws SQLException;
    Optional<ToolStock> findById(int id)throws SQLException;
    List<ToolStock> findAll();
    void delete(ToolStock tool) throws SQLException;
}