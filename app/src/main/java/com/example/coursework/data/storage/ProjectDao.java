package com.example.coursework.data.storage;

import androidx.room.*;
import java.util.List;

@Dao
public interface ProjectDao {
    @Insert
    long insert(ProjectEntity project);

    @Update
    void update(ProjectEntity project);

    @Delete
    void delete(ProjectEntity project);

    @Query("SELECT * FROM projects")
    List<ProjectEntity> getAll();


    @Query("SELECT * FROM projects WHERE id = :id")
    ProjectEntity findById(int id);
}
