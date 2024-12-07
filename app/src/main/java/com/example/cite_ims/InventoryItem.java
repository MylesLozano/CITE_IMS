package com.example.cite_ims;

public class InventoryItem {
    private final int id;
    private final String name;
    private final String description;
    private final int quantity;

    public InventoryItem(int id, String name, String description, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }
}