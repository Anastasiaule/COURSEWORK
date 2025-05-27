package com.example.coursework.domain.usecase;

import com.example.coursework.domain.entity.MaterialCatalog;
import com.example.coursework.domain.port.MaterialCatalogRepository;
import com.example.coursework.domain.exceptions.MaterialException;


import java.sql.SQLException;
import java.util.List;


public class MaterialCatalogUseCase {

    private final MaterialCatalogRepository repo;


    public MaterialCatalogUseCase(MaterialCatalogRepository repo) {
        this.repo = repo;

    }
    public void delete(MaterialCatalog material) throws SQLException {
        repo.delete(material);
    }
    public MaterialCatalog createNewMaterialCatalog(String name, String color, String unit) throws MaterialException, SQLException {
        if (color == null || color.trim().isEmpty()) throw new MaterialException("Цвет обязателен");

        MaterialCatalog materialCatalog = new MaterialCatalog(name, color,unit);
        repo.save(materialCatalog);
        return materialCatalog;
    }
    public List<MaterialCatalog> getAllMaterials() {
        return repo.findAll();
    }
    public MaterialCatalog getMaterialById(int id) {
        for (MaterialCatalog m : getAllMaterials()) {
            if (m.getId() == id) return m;
        }
        return null;
    }
}