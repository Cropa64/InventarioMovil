package com.inventario.network;

import android.os.StrictMode;

import androidx.appcompat.app.AppCompatActivity;

import com.inventario.principal.CentroCosto;
import com.inventario.principal.Producto;
import com.inventario.utilidades.Utiles;
import com.inventario.utilidades.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SocketCliente implements Serializable {
    private String ip_sola;
    private String ip_server;
    private final int PUERTO_SERVER = 2022;
    private Producto productoConsultado;

    public SocketCliente(String ip){
        cargarIP(ip);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void cargarIP(String ip){
        ip_server = "http://"+ip+":"+ PUERTO_SERVER;
        ip_sola = ip;
    }

    public String login(){
        try{
            URL url = new URL(ip_server+"/login");
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(3000);

            if(conexion.getResponseCode() != 200){
                return "No hay una toma de inventario iniciada";
            }else{
                return "ok";
            }
        }catch(Exception e){
            e.printStackTrace();
            return "No hay una toma de inventario iniciada";
        }
    }

    public String enviarStockNuevo(JSONObject envio){
        try{
            URL url = new URL(ip_server+"/inventario");
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conexion.setDoOutput(true);
            conexion.setDoInput(true);

            OutputStream os = conexion.getOutputStream();
            os.write(envio.toString().getBytes("UTF-8"));
            os.flush();

            String resultado;
            if(conexion.getResponseCode() == 200){
                try(BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    resultado = response.toString();
                }
                JSONObject rtaJson = new JSONObject(resultado);
                String statusInventario = rtaJson.getString("status");

                if(statusInventario.equals("ok")){
                    return statusInventario;
                }else{
                    return rtaJson.getString("descripcion");
                }
            }else{
                return "Error de comunicacion con el servidor";
            }
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String obtenerCantStock(String jsonConsulta){
        System.out.println("ENVIO JSON OBTENER CANT STOCK: "+jsonConsulta);
        try{
            URL url = new URL(ip_server+"/consulta");
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conexion.setDoOutput(true);
            conexion.setDoInput(true);

            OutputStream os = conexion.getOutputStream();
            os.write(jsonConsulta.getBytes("UTF-8"));
            os.flush();

            JSONObject rtaJson;
            System.out.println(conexion.getResponseCode());
            if(conexion.getResponseCode() == 200){
                try(BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "utf-8"))) {
                    String jsonArmado = Utiles.obtenerLineaString(br);
                    System.out.println("JSON ARMADO: "+jsonArmado);
                    rtaJson = new JSONObject(jsonArmado);
                }
                System.out.println(rtaJson);
                try{
                    String errorDescripcion = "";
                    if(!rtaJson.getString("error").equals("")){
                        errorDescripcion = rtaJson.getString("error");

                        os.close();
                        conexion.disconnect();
                    }
                    return errorDescripcion;
                }catch(JSONException e){
                    productoConsultado = new Producto(rtaJson.getString("codigo"), rtaJson.getString("descripcion"), 0f, rtaJson.getDouble("precio"), 0.0);
                    os.close();
                    conexion.disconnect();
                    return "ok";
                }
            }else{
                os.close();
                conexion.disconnect();

                return "El producto no existe";
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String terminarTomaInventario(){
        try{
            URL url = new URL(ip_server+"/end");
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            if(conexion.getResponseCode() == 200){
                BufferedReader fromServer = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String jsonRtaString = Utiles.obtenerLineaString(fromServer);
                JSONObject jsonRta = new JSONObject(jsonRtaString);

                String status = jsonRta.getString("status");
                if(status.equals("ok")){
                    return "ok";
                }else{
                    String problema = jsonRta.getString("descripcion");
                    return problema;
                }
            }else{
                return "Error de comunicacion con el servidor";
            }
        }catch(Exception e){
            e.printStackTrace();
            return "Error desconocido";
        }
    }

    public Integer obtenerNumTomaInventario(){
        try{
            URL url = new URL(ip_server+"/start");
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            if(conexion.getResponseCode() == 200){
                BufferedReader fromServer = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String jsonEntero = Utiles.obtenerLineaString(fromServer);

                Integer numero;
                JSONObject num = new JSONObject(jsonEntero);
                numero = num.getInt("numero");

                return numero;
            }else{
                return 0;
            }
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /*public List<CentroCosto> obtenerCentrosCosto(){
        try{
            URL url = new URL(ip_server+"/centrocosto");
            System.out.println(url);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            if(conexion.getResponseCode() == 200){
                BufferedReader fromServer = new BufferedReader(new InputStreamReader(conexion.getInputStream()));

                String jsonEntero = Utiles.obtenerLineaString(fromServer);

                JSONObject objeto = null;
                List<CentroCosto> centrosCostoCargados = new ArrayList<>();

                objeto = new JSONObject(jsonEntero);
                System.out.println("OBJETO JSON: " + objeto);

                //PROCESAR JSON
                JSONArray resultado = objeto.getJSONArray("centrosdecosto");
                System.out.println(resultado);

                for(int i = 0; i < resultado.length(); i++){
                    JSONObject centrocosto = resultado.getJSONObject(i);
                    Integer id = centrocosto.getInt("id");
                    String nombre = centrocosto.getString("nombre");
                    System.out.println("CENTRO COSTO "+ (i+1)+ ":" +nombre);

                    CentroCosto centroCosto = new CentroCosto(id, nombre);
                    centrosCostoCargados.add(centroCosto);
                }
                return centrosCostoCargados;
            }else{
                return null;
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }*/

    public Producto getProductoConsultado() {
        return productoConsultado;
    }

    public String getIp_sola() {
        return ip_sola;
    }
}
