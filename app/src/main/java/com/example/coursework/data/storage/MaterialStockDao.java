package com.example.coursework.data.storage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MaterialStockDao {

    @Insert
    long insert(MaterialStockEntity materialStock);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertOrUpdate(MaterialStockEntity e);

    @Update
    void update(MaterialStockEntity materialStock);

    @Delete
    void delete(MaterialStockEntity materialStock);

    @Query("SELECT * FROM material_stock")
    List<MaterialStockEntity> getAll();

    @Query("SELECT * FROM material_stock WHERE id = :id")
    MaterialStockEntity getById(int id);

    @Query("SELECT * FROM material_stock WHERE name = :name AND color = :color")
    MaterialStockEntity findByNameAndColor(String name, String color);

    @Query("SELECT * FROM material_stock WHERE name = :name AND color = :color AND unit = :unit LIMIT 1")
    MaterialStockEntity findByNameColorUnit(String name, String color, String unit);

}
