package com.example.coursework.repository.storage;

import androidx.room.*;
import java.util.List;

@Dao
public interface MaterialDao {
    @Insert
    long insert(MaterialEntity m);

    @Delete
    void delete(MaterialEntity m);

    @Query("SELECT * FROM materialCatalog")
    List<MaterialEntity> getAll();
    @Query("SELECT * FROM materialCatalog WHERE id = :id")
    MaterialEntity findById(int id);
    @Query("SELECT COUNT(*) FROM materialCatalog")
    int count();
}
