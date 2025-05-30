package com.example.coursework.data.storage;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "toolCatalog")
public class ToolEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String category;
}
