package com.inventario.principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.inventario.network.SocketCliente;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Configuracion extends AppCompatActivity {
    public static final String IP_MESSAGE = "com.inventario.principal.MESSAGE";
    public static final String CC_MESSAGE = "com.inventario.principal.MESSAGE";
    private SocketCliente socketCliente;
    private String IP;
    private List<CentroCosto> centrosCostoCargados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        consultarIntents(getIntent());
        mostrarIP();
        mostrarCentrosCosto();
    }

    private void consultarIntents(Intent intent){
        System.out.println(3);
        if(intent != null){
            if(intent.getStringExtra("IP_MESSAGE_INGRESARIP") != null){
                System.out.println(4);
                IP = intent.getStringExtra("IP_MESSAGE_INGRESARIP");
                socketCliente = new SocketCliente(IP);
            }
            if(intent.getStringExtra("IP_MESSAGE_SERVER") != null){
                System.out.println(5);
                IP = intent.getStringExtra("IP_MESSAGE_SERVER");
            }
            if(intent.getSerializableExtra("CC_MESSAGE_SERVER") != null){
                System.out.println(6);
                centrosCostoCargados = (List<CentroCosto>) intent.getSerializableExtra("CC_MESSAGE_SERVER");
            }
        }
    }

    private void mostrarIP(){
        final TextView output = findViewById(R.id.txtIPOutput);
        output.setText(IP);
    }

    private void mostrarCentrosCosto(){
        final TextView outputCC = findViewById(R.id.txtOutputCentros);
        if(centrosCostoCargados != null){
            for(int i = 0; i < centrosCostoCargados.size(); i++){
                System.out.println("ENTRE A MOSTRAR CENTRO COSTO "+i);
                outputCC.setText(outputCC.getText() + centrosCostoCargados.get(i).getId().toString()+") "+centrosCostoCargados.get(i).getNombre() + "\n");
            }
        }
    }

    public void ingresarIP(View view){
        Intent intent = new Intent(this, IngresarIP.class);
        System.out.println(1);
        startActivity(intent);
    }

    public void obtenerCentrosCosto(View view){
        centrosCostoCargados = socketCliente.obtenerCentrosCosto();
        mostrarCentrosCosto();
    }

    public void volverInicio(View view){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("IP_MESSAGE",IP);
        intent.putExtra("CC_MESSAGE",(Serializable) centrosCostoCargados);
        intent.putExtra("SOCKET_MESSAGE", socketCliente);
        startActivity(intent);
    }
}