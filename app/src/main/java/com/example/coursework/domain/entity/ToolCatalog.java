package com.example.coursework.domain.entity;

public class ToolCatalog {
    private int id;
    private String name;
    private String category;

    public ToolCatalog( String name,String category) {

        this.name = name;
        this.category = category;
    }
    @Override
    public String toString() {
        return name + " (" + category + ")";
    }
    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }

    public void setId(int id) {
        this.id=id;
    }
}
