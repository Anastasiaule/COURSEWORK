package com.example.coursework.data.repository;

import com.example.coursework.domain.entity.ToolStock;
import com.example.coursework.domain.port.ToolStockRepository;
import com.example.coursework.data.storage.RoomStorage;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DBToolStockRepository implements ToolStockRepository {
    private final RoomStorage roomStorage;

    public DBToolStockRepository(RoomStorage roomStorage) {
        this.roomStorage = roomStorage;
    }

    @Override
    public ToolStock save(ToolStock toolStock) throws SQLException {
        roomStorage.addToolStock(toolStock);
        return toolStock;
    }

    @Override
    public Optional<ToolStock> findById(int id) {
        return roomStorage.findToolStockById(id);
    }

    @Override
    public List<ToolStock> findAll() {

        return roomStorage.readToolStock();
    }

    @Override
    public void delete(ToolStock tool) throws SQLException {
        roomStorage.deleteToolStock(tool.getId());
    }
}
