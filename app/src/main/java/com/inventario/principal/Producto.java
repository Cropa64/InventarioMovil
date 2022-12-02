package com.inventario.principal;

import java.io.Serializable;

public class Producto implements Serializable {
    private String codigo;
    private String descripcion;
    private Float stock;
    private Double venta;
    private Double costo;

    public Producto(String codigo, String descripcion, Float stock, Double venta, Double costo){
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.stock = stock;
        this.venta = venta;
        this.costo = costo;
    }

    public Producto(){
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Float getStock() {
        return stock;
    }

    public Double getVenta(){ return venta; }

    public Double getCosto(){ return costo; }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setStock(Float stock) {
        this.stock = stock;
    }

    public void setVenta(Double venta){ this.venta = venta; }

    public void setCosto(Double costo){ this.costo = costo; }
}
