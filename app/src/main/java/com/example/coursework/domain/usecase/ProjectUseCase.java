package com.example.coursework.domain.usecase;

import com.example.coursework.domain.entity.MaterialCatalog;
import com.example.coursework.domain.entity.Project;
import com.example.coursework.domain.entity.ToolCatalog;
import com.example.coursework.domain.port.MaterialCatalogRepository;
import com.example.coursework.domain.port.ProjectRepository;
import com.example.coursework.domain.exceptions.ProjectException;
import com.example.coursework.domain.port.ToolCatalogRepository;

import java.sql.SQLException;
import java.util.List;

public class ProjectUseCase {
    private final ProjectRepository projectRepo;
    private final MaterialCatalogRepository materialRepo;
    private final ToolCatalogRepository toolRepo;
    //private final ToolRepository toolRepo;

    public ProjectUseCase(
            ProjectRepository projectRepo,
            MaterialCatalogRepository materialRepo,
            ToolCatalogRepository toolRepo)
    {
        this.projectRepo = projectRepo;
        this.materialRepo = materialRepo;
        this.toolRepo = toolRepo;
    }

    public Project createProject(String name, String craftType) throws ProjectException, SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new ProjectException("Название проекта обязательно");
        }
        if (craftType == null) {
            throw new ProjectException("Тип проекта обязателен");
        }

        // Создаём проект
        Project project = new Project(name, craftType);
        projectRepo.save(project);
        return project;
    }

    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }


    public void linkMaterialToProject(int projectId, int materialId, int quantity) throws SQLException, ProjectException {
        // Валидация
        if (quantity <= 0) throw new ProjectException("Количество должно быть больше 0");

        // Получаем объекты из репозиториев
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ProjectException("Проект не найден"));
        MaterialCatalog material = materialRepo.findById(materialId)
                .orElseThrow(() -> new ProjectException("Материал не найден"));


        System.out.println("Linking material to project: Project ID = " + projectId + ", Material ID = " + materialId + ", Quantity = " + quantity);

        // Сохраняем связь
        projectRepo.linkMaterial(project.getId(), material.getId(), quantity);
    }
    public Project getProjectById(int id) throws SQLException {
        return projectRepo.findById(id)
                .orElse(null);
    }

    public void linkToolToProject(int projectId, int toolId) throws SQLException, ProjectException {
        // Получаем объекты из репозиториев
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ProjectException("Проект не найден"));
        ToolCatalog tool = toolRepo.findById(toolId)
                .orElseThrow(() -> new ProjectException("Иструмент не найден"));


        System.out.println("Linking material to project: Project ID = " + projectId + ", Tool ID = " + toolId);

        // Сохраняем связь
        projectRepo.linkTool(project.getId(), tool.getId());
    }
    public List<MaterialCatalog> getAllMaterials() {
        return materialRepo.findAll();
    }
    public List<ToolCatalog> getAllTools() {
        return toolRepo.findAll();
    }


    public void unlinkMaterialFromProject(int projectId, int materialId) throws SQLException {
        projectRepo.unlinkMaterial(projectId, materialId);
    }

    public void unlinkToolFromProject(int projectId, int toolId) throws SQLException {
        projectRepo.unlinkTool(projectId, toolId);
    }
    public void deleteProject(Project project) throws SQLException {
        projectRepo.delete(project);
    }
}



