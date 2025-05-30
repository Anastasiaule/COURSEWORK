package com.example.coursework.domain.port;

import com.example.coursework.domain.entity.Project;
import com.example.coursework.data.dto.ProjectMaterialLinkDTO;
import com.example.coursework.data.dto.ProjectToolLinkDTO;

import java.sql.SQLException;
import java.util.Optional;
import java.util.List;

public interface ProjectRepository {
    Project save(Project project) throws SQLException;
    Optional<Project> findById(int id);
    List<Project> findAll();

    boolean linkTool(int projectId, int toolId);

    void update(Project project) throws SQLException;
    boolean linkMaterial(int projectId, int materialId, int quantity);

    List<ProjectMaterialLinkDTO> getMaterialLinks();
    List<ProjectToolLinkDTO> getToolLinks();

    void delete(Project project) throws SQLException;
    boolean unlinkMaterial(int projectId, int materialId) throws SQLException;
    boolean unlinkTool(int projectId, int toolId) throws SQLException;
}
