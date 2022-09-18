package com.inventario.network;

import android.content.Intent;
import android.os.StrictMode;
import android.widget.Toast;

import com.inventario.principal.CentroCosto;
import com.inventario.principal.IngresarStock;
import com.inventario.principal.MainActivity;
import com.inventario.principal.Producto;
import com.inventario.utilidades.Utiles;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SocketCliente implements Serializable {
    private String ip_sola = "";
    private String ip_server = "";
    private final int puerto_server = 2022;

    public SocketCliente(String ip){
        ip_server = "http://"+ip+":"+puerto_server;
        ip_sola = ip;
        System.out.println(ip_server);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public Integer enviarStockNuevo(JSONObject envio){
        System.out.println("ENVIO NUEVO DE STOCK: " + envio);

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
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                    resultado = response.toString();
                }
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public Producto obtenerCantStock(String jsonConsulta){
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

            System.out.println("CODIGO RESPUESTA: "+conexion.getResponseCode());

            JSONObject rtaJson = null;
            String resultado = "";
            if(conexion.getResponseCode() == 200){
                try(BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                    resultado = response.toString();
                }
                rtaJson = new JSONObject(resultado);
            }

            Producto producto = new Producto(rtaJson.getString("codigo"), rtaJson.getString("descripcion"), Float.parseFloat(rtaJson.getString("stock")));

            os.close();
            conexion.disconnect();

            return producto;
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

            BufferedReader fromServer = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            if(conexion.getResponseCode() == 200){
                String jsonRtaString = Utiles.obtenerLineaString(fromServer);
                JSONObject jsonRta = new JSONObject(jsonRtaString);

                String status = jsonRta.getString("status");
                if(status.equals("ok")){
                    return "ok";
                }else{
                    String problema = jsonRta.getString("descripcion");
                    return problema;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return "Error desconocido";
        }
        return null;
    }

    public Integer obtenerNumTomaInventario(){
        try{
            URL url = new URL(ip_server+"/start");
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            int status = conexion.getResponseCode();
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(conexion.getInputStream()));

            String jsonEntero = Utiles.obtenerLineaString(fromServer);

            Integer numero = 0;
            try{
                JSONObject num = new JSONObject(jsonEntero);
                numero = Integer.parseInt(num.getString("numero"));
            }catch(Exception e){
                e.printStackTrace();
            }

            return numero;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public List<CentroCosto> obtenerCentrosCosto(){
        try{
            URL url = new URL(ip_server+"/centrocosto");
            System.out.println(url);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            int status = conexion.getResponseCode();
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(conexion.getInputStream()));

            String jsonEntero = Utiles.obtenerLineaString(fromServer);

            JSONObject objeto = null;
            List<CentroCosto> centrosCostoCargados = new ArrayList<>();
            try{
                objeto = new JSONObject(jsonEntero);
                System.out.println("OBJETO JSON: " + objeto);

                //PROCESAR JSON
                JSONArray resultado = objeto.getJSONArray("centrosdecosto");
                System.out.println(resultado);

                for(int i = 0; i < resultado.length(); i++){
                    JSONObject centrocosto = resultado.getJSONObject(i);
                    String id = centrocosto.getString("id");
                    String nombre = centrocosto.getString("nombre");
                    System.out.println("CENTRO COSTO "+ (i+1)+ ":" +nombre);

                    CentroCosto centroCosto = new CentroCosto(Integer.parseInt(id), nombre);
                    centrosCostoCargados.add(centroCosto);
                }

            }catch(Exception e){
                e.printStackTrace();
            }
            return centrosCostoCargados;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String getIp_server() {
        return ip_server;
    }

    public String getIp_sola() {
        return ip_sola;
    }
}
