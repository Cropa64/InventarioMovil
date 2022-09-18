package com.inventario.principal;

import java.io.Serializable;

public class Producto implements Serializable {
    private String codigo;
    private String descripcion;
    private Float stock;

    public Producto(String codigo, String descripcion, Float stock){
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.stock = stock;
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

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setStock(Float stock) {
        this.stock = stock;
    }
}
