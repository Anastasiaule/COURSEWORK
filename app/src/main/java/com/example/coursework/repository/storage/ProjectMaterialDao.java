package com.example.coursework.repository.storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.coursework.repository.dto.ProjectMaterialLinkDTO;

import java.util.List;

@Dao
public interface ProjectMaterialDao {
    @Insert
    void insert(ProjectMaterialCrossRef ref);

    @Query("SELECT p.id AS projectId, " +
            "p.name AS projectName, " +
            "m.id AS materialId, " +
            "m.name AS materialName, " +
            "m.color AS color, " +
            "m.unit AS unit, " +
            "x.quantity AS quantity " +
            "FROM project_materials x " +
            "JOIN projects p ON x.projectId = p.id " +
            "JOIN materialCatalog m ON x.materialId = m.id")
    List<ProjectMaterialLinkDTO> getAllLinks();

    @Query("SELECT * FROM project_materials WHERE projectId = :projectId")
    List<ProjectMaterialCrossRef> getByProjectId(int projectId);


        @Query("DELETE FROM project_materials WHERE projectId = :projectId AND materialId = :materialId")
        int deleteLink(int projectId, int materialId); // ← вернёт количество удалённых строк
    }


