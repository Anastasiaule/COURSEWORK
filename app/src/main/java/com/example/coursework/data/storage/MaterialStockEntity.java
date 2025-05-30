package com.example.coursework.data.storage;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "material_stock")
public class MaterialStockEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String color;
    public int quantity;
    public String unit;


}

