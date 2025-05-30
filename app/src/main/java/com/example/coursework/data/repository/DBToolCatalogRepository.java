package com.example.coursework.data.repository;

import com.example.coursework.domain.entity.ToolCatalog;
import com.example.coursework.domain.port.ToolCatalogRepository;
import com.example.coursework.data.storage.RoomStorage;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DBToolCatalogRepository implements ToolCatalogRepository {
    private final RoomStorage roomStorage;

    public DBToolCatalogRepository(RoomStorage roomStorage) {
        this.roomStorage = roomStorage;
    }

    @Override
    public ToolCatalog save(ToolCatalog toolCatalog) throws SQLException {
        roomStorage.addTool(toolCatalog);
        return toolCatalog;
    }
    @Override
    public boolean hasLinkedProjects(int toolId) {
        return roomStorage.getProjectToolDao().isToolLinked(toolId);
    }


    @Override
    public Optional<ToolCatalog> findById(int id) {
        return roomStorage.findToolById(id);
    }

    @Override
    public List<ToolCatalog> findAll() {

        return roomStorage.readTools();
    }

    @Override
    public void delete(ToolCatalog tool) throws SQLException {
        roomStorage.deleteTool(tool.getId());
    }
}
