package com.example.coursework.domain.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Project {
    private int id;
    private String name;
    private String craftType;


    public Project(String name, String craftType) {
        this.name = name;
        this.craftType = craftType;
    }
    @Override
    public String toString() {
        return name + " (" + craftType + ")";
    }
    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCraftType() { return craftType; }


    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCraftType(String craftType) { this.craftType = craftType; }

    public void setMaterials(List<MaterialCatalog> mats) {
    }

    public void setTools(List<ToolCatalog> tls) {
    }
}