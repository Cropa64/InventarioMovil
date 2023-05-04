package com.inventario.utilidades;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;

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
