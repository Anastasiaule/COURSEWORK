package com.example.coursework.repository.indatabase;

import com.example.coursework.domain.entity.Project;
import com.example.coursework.domain.port.ProjectRepository;
import com.example.coursework.repository.dto.ProjectMaterialLinkDTO;
import com.example.coursework.repository.dto.ProjectToolLinkDTO;
import com.example.coursework.repository.storage.RoomStorage;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DBProjectRepository implements ProjectRepository {
    private final RoomStorage roomStorage;

    public DBProjectRepository(RoomStorage roomStorage) {
        this.roomStorage = roomStorage;
    }

    @Override
    public Project save(Project project) throws SQLException {
        roomStorage.addProject(project);
        return project;
    }

    @Override
    public Optional<Project> findById(int id) {

        return roomStorage.findProjectById(id);
    }

    @Override
    public List<Project> findAll() {
        return roomStorage.readProjectsWithMaterials();
    }


    @Override
    public List<ProjectMaterialLinkDTO> getMaterialLinks(){

        return roomStorage.getMaterialLinks();
    }
    @Override
    public List<ProjectToolLinkDTO> getToolLinks(){

        return roomStorage.getToolLinks();
    }

    @Override
    public void delete(Project project) throws SQLException {
        roomStorage.deleteProject(project.getId());
    }

    @Override
    public boolean unlinkMaterial(int projectId, int materialId) throws SQLException {
       return roomStorage.unlinkMaterialFromProject(projectId, materialId);

    }

    @Override
    public boolean unlinkTool(int projectId, int toolId) throws SQLException {
       return roomStorage.unlinkToolFromProject(projectId, toolId);

    }

    @Override
    public boolean linkMaterial(int projectId, int materialId, int quantity) {
        roomStorage.linkMaterialToProject(projectId, materialId, quantity);
        return true;
    }

    @Override
    public boolean linkTool(int projectId, int toolId) {
        roomStorage.linkToolToProject(projectId, toolId);
        return true;
    }

    @Override
    public void update(Project project) throws SQLException {
        roomStorage.updateProject(project);
    }

}
