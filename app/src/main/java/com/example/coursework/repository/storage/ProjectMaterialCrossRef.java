package com.example.coursework.repository.storage;

import androidx.room.Entity;

@Entity(primaryKeys = {"projectId","materialId"}, tableName = "project_materials")
public class ProjectMaterialCrossRef {
    public int projectId;
    public int materialId;
    public int quantity;
}
