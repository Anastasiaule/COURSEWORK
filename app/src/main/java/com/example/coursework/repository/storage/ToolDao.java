package com.example.coursework.repository.storage;

import androidx.room.*;
import java.util.List;

@Dao
public interface ToolDao {
    @Insert
    long insert(ToolEntity t);

    @Delete
    void delete(ToolEntity t);

    @Query("SELECT * FROM toolCatalog")
    List<ToolEntity> getAll();
    @Query("SELECT * FROM toolCatalog WHERE id = :id")
    ToolEntity findById(int id);

    @Query("SELECT COUNT(*) FROM toolCatalog")
    int count();
}
