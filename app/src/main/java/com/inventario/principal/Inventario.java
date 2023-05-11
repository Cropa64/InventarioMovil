package com.inventario.principal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.inventario.adaptadores.ListAdapter;
import com.inventario.network.SocketCliente;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Inventario extends AppCompatActivity implements View.OnTouchListener{
    private List<Producto> productosCargados;
    private SocketCliente socketCliente;
    private Integer idCCSeleccionado;
    private List<CentroCosto> centrosCostoCargados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);
        Button btnScan2 = findViewById(R.id.btnScan2);
        btnScan2.setOnTouchListener(this);

        consultarIntents(getIntent());
        mostrarTextoCC();
    }

    public void mostrarTextoCC(){
        /*String nombreCC = "";
        for(int i = 0; i < centrosCostoCargados.size(); i++){
            if(Objects.equals(idCCSeleccionado, centrosCostoCargados.get(i).getId())){
                nombreCC = centrosCostoCargados.get(i).getNombre();
            }
        }*/
        TextView txtCentroCosto = findViewById(R.id.txtMostrarCC);
        txtCentroCosto.setText("Realizando toma de inventario");
    }

    public void consultarIntents(Intent intent){
        System.out.println("ENTRE A CONSULTAR INTENTS");
        socketCliente = (SocketCliente) intent.getSerializableExtra("SOCKET");
        //idCCSeleccionado = intent.getIntExtra("IDCC", -1);
        //centrosCostoCargados = (List<CentroCosto>) intent.getSerializableExtra("CCCARGADOS");

        if((List<Producto>) intent.getSerializableExtra("PRODCARGADOS") != null){
            productosCargados = (List<Producto>) intent.getSerializableExtra("PRODCARGADOS");
            mostrarProductosCargados();
        }
    }

    //MUESTRO LOS PRODUCTOS QUE FUERON CARGANDO
    public void mostrarProductosCargados(){
        ListAdapter listAdapter = new ListAdapter(this, productosCargados);
        ListView listView = findViewById(R.id.list_view);

        listView.setAdapter(listAdapter);
        listView.setClickable(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(productosCargados.get(i).getDescripcion());
            }
        });
    }

    //INGRESO MANUAL DE CODIGO DE BARRAS
    public void ingresoManual(View view){
        //final String[] codigoIngresado = new String[16];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingrese el código");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Codigo");
        //input.setImeOptions(EditorInfo.IME_ACTION_SEND);

        builder.setView(input);
        input.requestFocus();

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String codigoIngresado = input.getText().toString();
                System.out.println("CODIGO MANUAL INGRESADO: "+codigoIngresado);
                String rtaEstado = enviarCodigoAlServer(codigoIngresado);
                procesarRespuesta(rtaEstado);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }



    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        displayScanResult(intent);
    }

    private void displayScanResult(Intent scanIntent)
    {
        String decodedData = scanIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        String scan = decodedData;
        //Integer prodYaCargado = -1;

        /*if(productosCargados != null){
            for(int i = 0; i < productosCargados.size(); i++){
                if(scan.equals(productosCargados.get(i).getCodigo())){
                    prodYaCargado = i;
                }
            }
        }*/

        String rtaEstado = enviarCodigoAlServer(scan);
        procesarRespuesta(rtaEstado);
    }

    private void procesarRespuesta(String rtaEstado){
        //PROCESO LA RESPUESTA
        if(rtaEstado.equals("ok")){
            Producto rtaCantStock = socketCliente.getProductoConsultado();

            System.out.println("CÓDIGO: "+rtaCantStock.getCodigo());
            System.out.println("DESCRIPCION: "+rtaCantStock.getDescripcion());
            System.out.println("STOCK: "+rtaCantStock.getStock());
            System.out.println("VENTA: "+rtaCantStock.getVenta());
            System.out.println("COSTO: "+rtaCantStock.getCosto());

            //INTENT PARA INICIAR ACTIVIDAD DE INGRESO DE NUEVO STOCK
            Intent intent = new Intent(this, IngresarStock.class);
            intent.putExtra("DATOS", rtaCantStock);
            intent.putExtra("SOCKET", socketCliente);
            //intent.putExtra("IDCC", idCCSeleccionado);
            intent.putExtra("PRODCARGADOS", (Serializable) productosCargados);
            //intent.putExtra("CCCARGADOS", (Serializable) centrosCostoCargados);
            //intent.putExtra("YACARGADO", prodYaCargado);
            startActivity(intent);
        }else{
            Toast.makeText(this, rtaEstado, Toast.LENGTH_LONG).show();
        }
    }

    private String enviarCodigoAlServer(String codigo){
        JSONObject objetoJson = new JSONObject();
        try{
            objetoJson.put("codigo",codigo);
            //objetoJson.put("idcentrocosto",idCCSeleccionado);
        }catch(Exception e){
            e.printStackTrace();
        }
        //MANDO AL SERVER Y GUARDO LA RESPUESTA
        String rtaEstado = socketCliente.obtenerCantStock(objetoJson.toString());

        return rtaEstado;
    }

    //METODO QUE TERMINA LA TOMA DE INVENTARIO
    public void terminar(View view){
        String respuesta = socketCliente.terminarTomaInventario();

        if(respuesta.equals("ok")){
            Toast.makeText(this, "Toma de inventario realizada exitosamente", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("SOCKET_MESSAGE", socketCliente);
            intent.putExtra("CC_MESSAGE", (Serializable) centrosCostoCargados);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("SOCKET_MESSAGE", socketCliente);
            intent.putExtra("CC_MESSAGE", (Serializable) centrosCostoCargados);
            startActivity(intent);
        }
    }

    //METODO SOBRESCRITO QUE MANEJA EL BOTON DE ESCANEO PARA COMENZARLO Y PARARLO
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.btnScan2)
        {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            {
                //  Button pressed, start scan
                Intent dwIntent = new Intent();
                dwIntent.setAction("com.symbol.datawedge.api.ACTION");
                dwIntent.putExtra("com.symbol.datawedge.api.SOFT_SCAN_TRIGGER", "START_SCANNING");
                sendBroadcast(dwIntent);
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
            {
                //  Button released, end scan
                Intent dwIntent = new Intent();
                dwIntent.setAction("com.symbol.datawedge.api.ACTION");
                dwIntent.putExtra("com.symbol.datawedge.api.SOFT_SCAN_TRIGGER", "STOP_SCANNING");
                sendBroadcast(dwIntent);
            }
        }
        return true;
    }
}