package com.example.coursework.data.storage;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "materialCatalog")
public class MaterialEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String color;
    public String unit;
}
