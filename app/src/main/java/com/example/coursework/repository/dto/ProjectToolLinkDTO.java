package com.example.coursework.repository.dto;

public class ProjectToolLinkDTO {
    private int projectId;
    private String projectName;
    private String toolName;
    private String category;
private int toolId;
    public ProjectToolLinkDTO(int projectId, String projectName, String toolName, String category,int toolId) {
        this.projectId=projectId;
        this.projectName = projectName;
       this.toolName=toolName;
       this.category=category;
       this.toolId=toolId;
    }
    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    // Геттеры
    public String getProjectName() {
        return projectName;
    }

    public String getToolName() {
        return toolName;
    }

    public String getCategory() {
        return category;
    }

    public int getToolId() {
        return toolId;
    }
}
