package com.inventario.principal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.inventario.network.SocketCliente;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String IP;
    private List<CentroCosto> centrosCostoCargados;
    private List<Producto> productosCargados;
    private SocketCliente socketCliente;
    private Integer idCCSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DWUtilities.CreateDWProfile(this);

        consultarIntents(getIntent());
    }

    public void mostrarProductosCargados(){
        final TextView output = findViewById(R.id.txtOutputStock);
        for(int i = 0; i < productosCargados.size(); i++){
            output.setText(output.getText() + "CODIGO: "+productosCargados.get(i).getCodigo() + " DESCRIPCION: "+productosCargados.get(i).getDescripcion() + " STOCK CARGADO: "+productosCargados.get(i).getStock() + "\n");
        }
    }

    public void consultarIntents(Intent intent){
        IP = intent.getStringExtra("IP_MESSAGE");
        centrosCostoCargados = (List<CentroCosto>) intent.getSerializableExtra("CC_MESSAGE");
        socketCliente = (SocketCliente) intent.getSerializableExtra("SOCKET_MESSAGE");
        idCCSeleccionado = intent.getIntExtra("IDCC", -1);

        if((List<Producto>) intent.getSerializableExtra("PRODCARGADOS") != null){
            productosCargados = (List<Producto>) intent.getSerializableExtra("PRODCARGADOS");

            for(int i = 0; i < productosCargados.size(); i++){
                System.out.println("PRODS EN MAIN: "+productosCargados.get(i).getDescripcion());
            }
            mostrarProductosCargados();
        }
    }

    public void comenzar(View view){
        System.out.println("ENTRE A COMENZAR");
        Integer numTomaInventario = socketCliente.obtenerNumTomaInventario();

        final CharSequence[] nombresCC = new CharSequence[centrosCostoCargados.size()];
        for(int i = 0; i < centrosCostoCargados.size(); i++){
            nombresCC[i] = centrosCostoCargados.get(i).getNombre();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("TOMA DE INVENTARIO NUMERO "+numTomaInventario)
                .setItems(nombresCC, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("OPCION SELECCIONADA: "+i);
                        guardarCCSeleccionado(i, nombresCC);
                        Intent intent = new Intent(MainActivity.this, Inventario.class);
                        intent.putExtra("SOCKET", socketCliente);
                        intent.putExtra("IDCC", idCCSeleccionado);
                        intent.putExtra("PRODCARGADOS", (Serializable) productosCargados);
                        intent.putExtra("CCCARGADOS", (Serializable) centrosCostoCargados);
                        startActivity(intent);
                    }
                })
                .show();
    }

    public void guardarCCSeleccionado(int opcSeleccionada, CharSequence[] nombresCC){
        String ccSeleccionado = nombresCC[opcSeleccionada].toString();
        System.out.println("CENTRO DE COSTO SELECCIONADO: "+ccSeleccionado);

        for(int i = 0; i < centrosCostoCargados.size(); i++){
            if(centrosCostoCargados.get(i).getNombre().equals(ccSeleccionado)){
                idCCSeleccionado = centrosCostoCargados.get(i).getId();
            }
        }
    }

    public void configuracion(View view){
        Intent intent = new Intent(this, Configuracion.class);
        if(socketCliente != null){
            intent.putExtra("SOCKET", socketCliente);
            intent.putExtra("CCCARGADOS", (Serializable) centrosCostoCargados);
        }
        startActivity(intent);
    }
}