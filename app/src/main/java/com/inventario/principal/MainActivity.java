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
import com.inventario.utilidades.Variables;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<CentroCosto> centrosCostoCargados;
    private List<Producto> productosCargados;
    private SocketCliente socketCliente;
    private Integer idCCSeleccionado;
    private String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DWUtilities.CreateDWProfile(this);
        Variables.IP = leerIP("ip.txt");
        Variables.NOMBRE = leerNombre("nombre.txt");

        if(!Variables.IP.equals("")){
            System.out.println("IP LEIDA: "+Variables.IP);
            socketCliente = new SocketCliente(Variables.IP);
        }
        if(!Variables.NOMBRE.equals("")){
            nombre = Variables.NOMBRE;
            System.out.println("NOMBRE LEIDO: "+nombre);
        }

        //CONSULTO INTENTS DESDE OTRA ACTIVIDAD CUANDO SE CREA ESTA
        consultarIntents(getIntent());
    }

    public String leerIP(String nombreArchivo){
        File directorio = getApplicationContext().getFilesDir();
        File archivoLeido = new File(directorio, nombreArchivo);
        byte[] contenido = new byte[(int) archivoLeido.length()];
        try {
            FileInputStream stream = new FileInputStream(archivoLeido);
            stream.read(contenido);
            return new String(contenido);
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public String leerNombre(String nombreArchivo){
        File directorio = getApplicationContext().getFilesDir();
        File archivoLeido = new File(directorio, nombreArchivo);
        byte[] contenido = new byte[(int) archivoLeido.length()];
        try{
            FileInputStream stream = new FileInputStream(archivoLeido);
            stream.read(contenido);
            return new String(contenido);
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public void consultarIntents(Intent intent){
        centrosCostoCargados = (List<CentroCosto>) intent.getSerializableExtra("CC_MESSAGE");
        if(socketCliente==null){
            socketCliente = (SocketCliente) intent.getSerializableExtra("SOCKET_MESSAGE");
        }
        idCCSeleccionado = intent.getIntExtra("IDCC", -1);

        //SI EL INTENT CONTIENE LOS PRODUCTOS LOS GUARDO EN LA VARIABLE
        if((List<Producto>) intent.getSerializableExtra("PRODCARGADOS") != null){
            productosCargados = (List<Producto>) intent.getSerializableExtra("PRODCARGADOS");
        }
    }

    //BOTON COMENZAR TOMA DE INVENTARIO
    public void comenzar(View view){
        if(socketCliente != null && nombre != null){
            /*//OBTENGO EL NUMERO DE LA TOMA DE INVENTARIO
            Integer numTomaInventario = socketCliente.obtenerNumTomaInventario();

            //CARGO LOS NOMBRES EN CHARSEQUENCE PARA USARLOS EN EL ALERT
            final CharSequence[] nombresCC = new CharSequence[centrosCostoCargados.size()];
            for(int i = 0; i < centrosCostoCargados.size(); i++){
                nombresCC[i] = centrosCostoCargados.get(i).getNombre();
            }

            //CORROBORO QUE TRAJO EL NUMERO DE TOMA DE INVENTARIO CORRECTAMENTE
            if(numTomaInventario > 0){
                //ARMO EL ALERT CON LOS NOMBRES DE LOS CENTROS DE COSTO PARA SELECCIONAR
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("TOMA DE INVENTARIO NUMERO "+numTomaInventario)
                        .setItems(nombresCC, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                guardarCCSeleccionado(i, nombresCC);
                                //ARMO EL INTENT PARA INICIAR LA ACTIVIDAD DE LA TOMA DE INVENTARIO
                                Intent intent = new Intent(MainActivity.this, Inventario.class);
                                intent.putExtra("SOCKET", socketCliente);
                                intent.putExtra("IDCC", idCCSeleccionado);
                                intent.putExtra("PRODCARGADOS", (Serializable) productosCargados);
                                intent.putExtra("CCCARGADOS", (Serializable) centrosCostoCargados);
                                startActivity(intent);
                            }
                        })
                        .show();
            }else{
                Toast.makeText(this, "No se pudo obtener el numero de toma de inventario", Toast.LENGTH_LONG).show();
            }*/

            //LOGIN EN LA APP INVENTARIO
            String rta = socketCliente.login();


            if(rta.equals("ok")){
                //Toast.makeText(this, rta, Toast.LENGTH_LONG).show();
                //ARMO EL INTENT PARA INICIAR LA ACTIVIDAD DE LA TOMA DE INVENTARIO
                Intent intent = new Intent(MainActivity.this, Inventario.class);
                intent.putExtra("SOCKET", socketCliente);
                //intent.putExtra("IDCC", idCCSeleccionado);
                //intent.putExtra("PRODCARGADOS", (Serializable) productosCargados);
                //intent.putExtra("CCCARGADOS", (Serializable) centrosCostoCargados);
                startActivity(intent);
            }else{
                Toast.makeText(this, rta, Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "Primero debe configurar la aplicacion", Toast.LENGTH_LONG).show();
        }
    }

    //GUARDO EL CENTRO DE COSTO SELECCIONADO PARA LA TOMA DE INVENTARIO, REALIZADO EN EL ALERT
    public void guardarCCSeleccionado(int opcSeleccionada, CharSequence[] nombresCC){
        String ccSeleccionado = nombresCC[opcSeleccionada].toString();
        System.out.println("CENTRO DE COSTO SELECCIONADO: "+ccSeleccionado);

        for(int i = 0; i < centrosCostoCargados.size(); i++){
            if(centrosCostoCargados.get(i).getNombre().equals(ccSeleccionado)){
                idCCSeleccionado = centrosCostoCargados.get(i).getId();
            }
        }
    }

    //INICIO LA ACTIVIDAD DE CONFIGURACION DE LA APP
    public void configuracion(View view){
        Intent intent = new Intent(this, Configuracion.class);
        //SI EL SOCKET ESTA CREADO SE LO ENVIO NUEVAMENTE, CASO CONTRARIO NO SE ENVIA NADA Y SE INICIA LA ACTIVIDAD
        if(socketCliente != null){
            intent.putExtra("SOCKET", socketCliente);
            intent.putExtra("CCCARGADOS", (Serializable) centrosCostoCargados);
        }
        startActivity(intent);
    }
}