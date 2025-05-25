package com.example.coursework.domain.entity;

public class MaterialCatalog {

    private String name;
    private String color;
    private String unit;

    private int id;
    public MaterialCatalog( String name,String color, String unit) {
        this.name = name;
        this.color = color;
        this.unit=unit;
    }
    @Override
    public String toString() {
        return name + " (" + color + "), "+unit;
    }
    // Геттеры
    public String getName() { return name; }
    public String getColor() { return color; }

    public int getId() { return id;
    }
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setId(int id) {
        this.id=id;
    }
}
