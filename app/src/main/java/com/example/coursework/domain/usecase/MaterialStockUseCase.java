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
        if (color == null || color.trim().isEmpty()) {
            throw new MaterialException("Цвет обязателен");
        }

        List<MaterialStock> allStock = repo.findAll();
        for (MaterialStock existing : allStock) {
            if (existing.getName().equalsIgnoreCase(name)
                    && existing.getColor().equalsIgnoreCase(color)
                    && existing.getUnit().equalsIgnoreCase(unit)) {


                int newQuantity =  quantity;

                MaterialStock updated = new MaterialStock(
                        existing.getId(),
                        name,
                        color,
                        newQuantity,
                        unit
                );

                repo.save(updated);
                return updated;
            }
        }

        // Если такой материал не найден — создаём новый
        MaterialStock newMaterial = new MaterialStock(name, color, quantity, unit);
        repo.save(newMaterial);
        return newMaterial;
    }



    public List<MaterialStock> getAllMaterials() {
        return repo.findAll();
    }
    public void decreaseStock(String name, String color, int amount) throws SQLException {
        MaterialStock stock = findMaterialByNameAndColor(name, color);
        if (stock != null) {
            int newQty = stock.getQuantity() - amount;
            stock.setQuantity(newQty);
            repo.save(stock); // save обновит запись
        }
    }
    public void increaseStock(String name, String color, int amount) throws SQLException {
        MaterialStock stock = findMaterialByNameAndColor(name, color);
        if (stock != null) {
            int newQty = stock.getQuantity() + amount;
            stock.setQuantity(newQty);
            repo.save(stock); // save обновит запись
        }
    }

    private MaterialStock findMaterialByNameAndColor(String name, String color) {
        for (MaterialStock stock : repo.findAll()) {
            if (stock.getName().equals(name) && stock.getColor().equals(color)) {
                return stock;
            }
        }
        return null;
    }
}