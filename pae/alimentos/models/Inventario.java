package pae.alimentos.models;

import java.util.HashMap;

public class Inventario extends HashMap<String, Object> {

    private int id_alimento;
    private String accion;
    private double cantidad;
    private String fecha;


    public Inventario(int id_alimento, String accion, double cantidad, String fecha) {
        this.id_alimento = id_alimento;
        this.accion = accion;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public int getId_alimento() {
        return id_alimento;
    }

    public String getAccion() {
        return accion;
    }

    public double getCantidad() {

        return cantidad;
    }

    public String getFecha() {
        return fecha;
    }

    @Override public String toString(){
        return " Alimento: " + getId_alimento() +
                " Accion: " + getAccion() +
                " Cantidad: " + getCantidad() +
                " Fecha: " + getFecha();
    }

}
