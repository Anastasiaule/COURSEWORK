package com.example.coursework.data.storage;

import androidx.room.*;
import com.example.coursework.data.dto.ProjectToolLinkDTO;
import java.util.List;

@Dao
public interface ProjectToolDao {
    @Insert
    void insert(ProjectToolCrossRef ref);

    @Query("SELECT p.id AS projectId, " +
            "p.name AS projectName, " +
            "t.id AS toolId, " +
            "t.name AS toolName, " +
            "t.category AS category " +
            "FROM project_tools x " +
            "JOIN projects p ON x.projectId = p.id " +
            "JOIN toolCatalog t ON x.toolId = t.id")
    List<ProjectToolLinkDTO> getAllLinks();


    @Query("SELECT * FROM project_tools WHERE projectId = :projectId")
    List<ProjectToolCrossRef> getByProjectId(int projectId);


    @Query("DELETE FROM project_tools WHERE projectId = :projectId AND toolId = :toolId")
    int deleteLink(int projectId, int toolId);

}
