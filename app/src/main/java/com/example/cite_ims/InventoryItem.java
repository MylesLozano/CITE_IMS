package com.example.cite_ims;

public class InventoryItem {
    private final int itemId;
    private String name;
    private int quantity;
    private String imageUri;  // New field for image URI

    public InventoryItem(int itemId, String name, int quantity, String imageUri) {
        this.itemId = itemId;
        this.name = name;
        this.quantity = quantity;
        this.imageUri = imageUri;  // Initialize the new field
    }

    public int getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImageUri() {
        return imageUri;  // Getter for the new field
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
