package com.example.coursework.domain.entity;

public class MaterialStock {
    private String name;
    private String color;
    private String unit;
    private int quantity;
    private int id;
    public MaterialStock(String name, String color, int quantity, String unit) {
        this.name = name;
        this.color = color;
        this.quantity=quantity;
        this.unit=unit;
    }
    @Override
    public String toString() {
        return name + " (" + color + "), "+quantity+" "+unit;
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
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setId(int id) {
        this.id=id;
    }
}
