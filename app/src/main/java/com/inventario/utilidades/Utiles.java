package com.inventario.utilidades;

import java.io.BufferedReader;

public class Utiles {
    public static String obtenerLineaString(BufferedReader buff){
        String line;
        String jsonEntero = "";
        try{
            while((line = buff.readLine()) != null){
                jsonEntero += line;
            }
            return jsonEntero;
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }
}
