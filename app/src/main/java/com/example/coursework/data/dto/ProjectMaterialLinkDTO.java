package com.example.coursework.data.dto;

// ProjectMaterialLinkDTO.java
public class ProjectMaterialLinkDTO {
    private int projectId;
    private String projectName;
    private String materialName;
    private int quantity;
    private String color;
    private String unit;

    private int materialId;

    public ProjectMaterialLinkDTO(int projectId, String projectName, int materialId, String materialName, String color, int quantity, String unit) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.materialId = materialId;
        this.materialName = materialName;
        this.color = color;
        this.quantity = quantity;
        this.unit = unit;
    }
    @Override
    public String toString() {
        return materialName + " (" + color + "), "+quantity+" "+unit;
    }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    // Геттеры
    public String getProjectName() { return projectName; }
    public String getMaterialName() { return materialName; }
    public int getQuantity() { return quantity; }

    public Object getColor() { return color;
    }
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public int getMaterialId() {
    return materialId;
    }

    public void setMaterialId(int id) {
        this.materialId=materialId;
    }
}