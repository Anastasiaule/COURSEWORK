package com.example.coursework.repository.storage;

import androidx.room.Entity;

@Entity(primaryKeys = {"projectId","toolId"}, tableName = "project_tools")
public class ProjectToolCrossRef {
    public int projectId;
    public int toolId;
}
