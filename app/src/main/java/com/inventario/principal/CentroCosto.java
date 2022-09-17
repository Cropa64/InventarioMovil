package com.inventario.principal;

import java.io.Serializable;

public class CentroCosto implements Serializable {
    private Integer id;
    private String nombre;

    public CentroCosto(Integer id, String nombre){
        this.id = id;
        this.nombre = nombre;
    }

    public CentroCosto(){
    }

    public Integer getId(){
        return id;
    }

    public String getNombre(){
        return nombre;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
