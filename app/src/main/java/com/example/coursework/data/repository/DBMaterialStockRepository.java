package com.example.coursework.data.repository;

import com.example.coursework.domain.entity.MaterialStock;
import com.example.coursework.domain.port.MaterialStockRepository;
import com.example.coursework.data.storage.RoomStorage;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DBMaterialStockRepository implements MaterialStockRepository {
    private final RoomStorage roomStorage;

    public DBMaterialStockRepository(RoomStorage roomStorage) {
        this.roomStorage = roomStorage;
    }

    @Override
    public MaterialStock save(MaterialStock materialStock) throws SQLException {
        roomStorage.addMaterialStock(materialStock);
        return materialStock;
    }

    @Override
    public Optional<MaterialStock> findById(int id)  {

        return roomStorage.findMaterialStockById(id);
    }
    @Override
    public List<MaterialStock> findAll() {
        return roomStorage.readMaterialStock();
    }

    @Override
    public void delete(MaterialStock material) throws SQLException {
        roomStorage.deleteMaterialStock(material.getId());
    }
}
