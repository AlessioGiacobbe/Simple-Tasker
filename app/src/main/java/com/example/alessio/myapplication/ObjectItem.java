package com.example.alessio.myapplication;

/**
 * Created by Alessio on 17/04/2016.
 */
public class ObjectItem {
    public int id;
    public String nome;
    public double latitudine;
    public double longitudine;

    public ObjectItem(int id, String nome, double latitudine, double longitudine){
        this.id = id;
        this.nome = nome;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }
}
