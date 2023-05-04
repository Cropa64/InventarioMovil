package com.inventario.principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.inventario.network.SocketCliente;
import com.inventario.utilidades.Variables;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Configuracion extends AppCompatActivity {
    private SocketCliente socketCliente;
    private List<CentroCosto> centrosCostoCargados = new ArrayList<>();
    private Button botonCC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        //botonCC = findViewById(R.id.btnObtenerCentrosCosto);

        /*if(!Variables.IP.equals("") && centrosCostoCargados.isEmpty()){
            botonCC.setEnabled(true);
        }else{
            botonCC.setEnabled(false);
        }*/

        consultarIntents(getIntent());
        mostrarDatos();
    }

    private void consultarIntents(Intent intent){
        if(intent != null){
            if(intent.getStringExtra("IP_MESSAGE_INGRESARIP") != null){
                //botonCC.setEnabled(true);
                String IP;
                IP = intent.getStringExtra("IP_MESSAGE_INGRESARIP");
                socketCliente = new SocketCliente(IP);
            }
            if(intent.getSerializableExtra("SOCKET") != null){
                socketCliente = (SocketCliente) intent.getSerializableExtra("SOCKET");
            }
            if(intent.getSerializableExtra("CCCARGADOS") != null){
                centrosCostoCargados = (List<CentroCosto>) intent.getSerializableExtra("CCCARGADOS");
            }
        }
    }

    private void mostrarDatos(){
        mostrarIP();
        mostrarNombre();
        //mostrarCentrosCosto();
    }

    private void mostrarIP(){
        final TextView output = findViewById(R.id.txtIPOutput);
        if(!Variables.IP.equals("")){
            output.setText(Variables.IP);
        }
    }

    private void mostrarNombre(){
        final TextView textNombre = findViewById(R.id.txtNombreCargado);
        if(!Variables.NOMBRE.equals("")){
            textNombre.setText(Variables.NOMBRE);
        }
    }
    /*private void mostrarCentrosCosto(){
        final TextView outputCC = findViewById(R.id.txtOutputCentros);
        if(centrosCostoCargados != null){
            for(int i = 0; i < centrosCostoCargados.size(); i++){
                System.out.println("ENTRE A MOSTRAR CENTRO COSTO "+i);
                outputCC.setText(outputCC.getText() + centrosCostoCargados.get(i).getId().toString()+") "+centrosCostoCargados.get(i).getNombre() + "\n");
            }
        }
    }*/

    public void ingresarIP(View view){
        Intent intent = new Intent(this, IngresarIP.class);
        startActivity(intent);
    }

    public void ingresarNombre(View view){
        EditText editNombre = findViewById(R.id.editTextNombre);
        String nombre = editNombre.getText().toString();

        if(nombre != null){
            System.out.println("Nombre ingresado: "+ nombre);
            TextView nombreCargado = findViewById(R.id.txtNombreCargado);
            nombreCargado.setText(nombre);
            Variables.NOMBRE = nombre;
            writeToFile("nombre.txt",nombre);
            editNombre.setText("");
        }else{
            Toast.makeText(this, "Debe ingresar un nombre", Toast.LENGTH_LONG).show();
        }
    }

    /*public void obtenerCentrosCosto(View view){
        centrosCostoCargados = socketCliente.obtenerCentrosCosto();
        if(centrosCostoCargados != null){
            mostrarCentrosCosto();
            botonCC.setEnabled(false);
        }else{
            Toast.makeText(this, "No se pudieron obtener los centros de costo", Toast.LENGTH_LONG).show();
        }
    }*/

    public void volverInicio(View view){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("CC_MESSAGE",(Serializable) centrosCostoCargados);
        intent.putExtra("SOCKET_MESSAGE", socketCliente);
        startActivity(intent);
    }

    public void writeToFile(String nombreArchivo, String contenido){
        File directorio = getApplicationContext().getFilesDir();
        try {
            FileOutputStream escritor = new FileOutputStream(new File(directorio, nombreArchivo));
            escritor.write(contenido.getBytes());
            escritor.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}