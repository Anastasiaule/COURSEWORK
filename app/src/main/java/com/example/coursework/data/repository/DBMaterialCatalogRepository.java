package com.example.coursework.data.repository;

import com.example.coursework.domain.entity.MaterialCatalog;
import com.example.coursework.domain.port.MaterialCatalogRepository;
import com.example.coursework.data.storage.RoomStorage;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DBMaterialCatalogRepository implements MaterialCatalogRepository {
    private final RoomStorage roomStorage;

    public DBMaterialCatalogRepository(RoomStorage roomStorage) {
        this.roomStorage = roomStorage;
    }

    @Override
    public MaterialCatalog save(MaterialCatalog materialCatalog) throws SQLException {
        roomStorage.addMaterial(materialCatalog);
        return materialCatalog;
    }

    @Override
    public Optional<MaterialCatalog> findById(int id)  {

            return roomStorage.findMaterialById(id);
    }
    @Override
    public List<MaterialCatalog> findAll() {
        return roomStorage.readMaterials();
    }

    @Override
    public void delete(MaterialCatalog material) throws SQLException {
        roomStorage.deleteMaterial(material.getId());
    }
}
