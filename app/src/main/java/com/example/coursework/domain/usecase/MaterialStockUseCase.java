package com.example.coursework.domain.usecase;

import com.example.coursework.domain.entity.MaterialStock;
import com.example.coursework.domain.port.MaterialStockRepository;
import com.example.coursework.domain.exceptions.MaterialException;


import java.sql.SQLException;
import java.util.List;


public class MaterialStockUseCase {

    private final MaterialStockRepository repo;


    public MaterialStockUseCase(MaterialStockRepository repo) {
        this.repo = repo;

    }
    public void delete(MaterialStock material) throws SQLException {
        repo.delete(material);
    }
    public MaterialStock createNewMaterialStock(String name, String color, int quantity, String unit) throws MaterialException, SQLException {
        if (color == null || color.trim().isEmpty()) throw new MaterialException("Цвет обязателен");

        MaterialStock materialStock = new MaterialStock(name, color,quantity, unit);
        repo.save(materialStock);
        return materialStock;
    }
    public List<MaterialStock> getAllMaterials() {
        return repo.findAll();
    }

}