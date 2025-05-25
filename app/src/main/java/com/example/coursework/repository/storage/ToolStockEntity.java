package com.example.coursework.repository.storage;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "tool_stock")
public class ToolStockEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String category;


}

