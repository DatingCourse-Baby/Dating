package com.example.datingcourse;

public class Product {
    private String name1;
    private String name2;
    private int price;
    private int imageResId;

    public Product(String name1,String name2, int price, int imageResId) {
        this.name1 = name1;
        this.name2 = name2;
        this.price = price;
        this.imageResId = imageResId;
    }
    public String getName1() {
        return name1;
    }

    public String getName2() {
        return name2;
    }

    public int getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }
}