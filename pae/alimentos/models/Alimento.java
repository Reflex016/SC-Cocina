package pae.alimentos.models;

import java.util.HashMap;

public class Alimento extends HashMap<String, Object> {

    private int id;
    private String nombre;
    private double cantidad;
    private String descripcion;


    public Alimento(int id, String nombre, double cantidad, String descripcion) {
        this.id = id;
        this.nombre= nombre;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
    }

    public Alimento(String nombre, double cantidad, String descripcion) {
        this.nombre= nombre;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
    }

    public Alimento(){

    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }



    @Override public String toString(){
        return "Nombre: " + getNombre() + " Cantidad: " + getCantidad() + " Descripcion: " + getDescripcion();
    }

}
