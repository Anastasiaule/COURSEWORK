package com.example.coursework.repository.dto;

// ProjectMaterialLinkDTO.java
public class ProjectMaterialLinkDTO {
    private int projectId;
    private String projectName;
    private String materialName;
    private int quantity;
    private String color;
    private int materialId;

    public ProjectMaterialLinkDTO(int projectId, String projectName, int materialId, String materialName, String color, int quantity) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.materialId = materialId;
        this.materialName = materialName;
        this.color = color;
        this.quantity = quantity;
    }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    // Геттеры
    public String getProjectName() { return projectName; }
    public String getMaterialName() { return materialName; }
    public int getQuantity() { return quantity; }

    public Object getColor() { return color;
    }

    public int getMaterialId() {
    return materialId;
    }
}