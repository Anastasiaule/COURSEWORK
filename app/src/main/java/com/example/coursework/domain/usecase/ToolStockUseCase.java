package com.example.coursework.domain.usecase;

import com.example.coursework.domain.entity.ToolStock;

import com.example.coursework.domain.port.ToolStockRepository;
import com.example.coursework.domain.exceptions.ToolException;

import java.sql.SQLException;
import java.util.List;

public class ToolStockUseCase {

    private final ToolStockRepository toolRepo;

    public ToolStockUseCase(ToolStockRepository toolRepo) {
        this.toolRepo = toolRepo;
    }

    public ToolStock createNewToolStock(

            String name,String category
    ) throws ToolException, SQLException {
        if (category == null || category.trim().isEmpty()) throw new ToolException("Категория обязательна");
        ToolStock toolStock = new ToolStock(name, category);
        toolRepo.save(toolStock);
        return toolStock;
    }
    public void delete(ToolStock tool) throws SQLException {
        toolRepo.delete(tool);
    }
    public List<ToolStock> getAllTools() {
        return toolRepo.findAll();
    }
}