package com.example.t4_peliculas.model;

public class Genero {
    private int id;
    private String name;

    // Constructor
    public Genero(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}

