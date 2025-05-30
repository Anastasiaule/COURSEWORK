package com.example.coursework.data.storage;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ToolStockDao {

    @Insert
    long insert(ToolStockEntity toolStock);

    @Update
    void update(ToolStockEntity toolStock);

    @Delete
    void delete(ToolStockEntity toolStock);

    @Query("SELECT * FROM tool_stock")
    List<ToolStockEntity> getAll();

    @Query("SELECT * FROM tool_stock WHERE id = :id")
    ToolStockEntity getById(int id);
}

