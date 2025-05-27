package com.example.coursework.domain.usecase;

import com.example.coursework.domain.entity.MaterialCatalog;
import com.example.coursework.domain.entity.ToolCatalog;

import com.example.coursework.domain.port.ToolCatalogRepository;
import com.example.coursework.domain.exceptions.ToolException;

import java.sql.SQLException;
import java.util.List;

public class ToolCatalogUseCase {

    private final ToolCatalogRepository toolRepo;

    public ToolCatalogUseCase(ToolCatalogRepository toolRepo) {
        this.toolRepo = toolRepo;
    }

    public ToolCatalog createNewToolCatalog(

            String name,String category
    ) throws ToolException, SQLException {
        if (category == null || category.trim().isEmpty()) throw new ToolException("Категория обязательна");
        ToolCatalog toolCatalog = new ToolCatalog(name, category);
        toolRepo.save(toolCatalog);
        return toolCatalog;
    }
    public void delete(ToolCatalog tool) throws SQLException {
        toolRepo.delete(tool);
    }
    public List<ToolCatalog> getAllTools() {
        return toolRepo.findAll();
    }
    public ToolCatalog getToolById(int id) {
        for (ToolCatalog m : getAllTools()) {
            if (m.getId() == id) return m;
        }
        return null;
    }
}