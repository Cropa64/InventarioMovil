package com.inventario.principal;

import androidx.appcompat.app.AppCompatActivity;

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
        String nombreCC = "";
        for(int i = 0; i < centrosCostoCargados.size(); i++){
            if(Objects.equals(idCCSeleccionado, centrosCostoCargados.get(i).getId())){
                nombreCC = centrosCostoCargados.get(i).getNombre();
            }
        }
        TextView txtCentroCosto = findViewById(R.id.txtMostrarCC);
        txtCentroCosto.setText("Haciendo toma de inventario en centro de costo "+nombreCC);
    }

    public void consultarIntents(Intent intent){
        System.out.println("ENTRE A CONSULTAR INTENTS");
        socketCliente = (SocketCliente) intent.getSerializableExtra("SOCKET");
        idCCSeleccionado = intent.getIntExtra("IDCC", -1);
        centrosCostoCargados = (List<CentroCosto>) intent.getSerializableExtra("CCCARGADOS");

        if((List<Producto>) intent.getSerializableExtra("PRODCARGADOS") != null){
            productosCargados = (List<Producto>) intent.getSerializableExtra("PRODCARGADOS");
            mostrarProductosCargados();
        }
    }

    //MUESTRO LOS PRODUCTOS QUE FUERON CARGANDO
    public void mostrarProductosCargados(){
        final TextView output = findViewById(R.id.txtOutputStock);
        for(int i = 0; i < productosCargados.size(); i++){
            output.setText(output.getText() + "CODIGO: "+productosCargados.get(i).getCodigo() + " DESCRIPCION: "+productosCargados.get(i).getDescripcion() + " STOCK CARGADO: "+productosCargados.get(i).getStock() + "\n");
            output.setText(output.getText() + "----------------------------------------------------------------" + "\n");
        }
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

        //ARMO JSON CON EL CODIGO Y CENTRO DE COSTO PARA MANDARLO AL SOCKET Y ESTE AL SERVER
        JSONObject objetoJson = new JSONObject();
        try{
            objetoJson.put("codigo",scan);
            objetoJson.put("idcentrocosto",idCCSeleccionado);
        }catch(Exception e){
            e.printStackTrace();
        }
        //MANDO AL SERVER Y GUARDO LA RESPUESTA
        String rtaEstado = socketCliente.obtenerCantStock(objetoJson.toString());

        //PROCESO LA RESPUESTA
        if(rtaEstado.equals("ok")){
            Producto rtaCantStock = socketCliente.getProductoConsultado();

            System.out.println("CÓDIGO: "+rtaCantStock.getCodigo());
            System.out.println("DESCRIPCION: "+rtaCantStock.getDescripcion());
            System.out.println("STOCK: "+rtaCantStock.getStock());

            //INTENT PARA INICIAR ACTIVIDAD DE INGRESO DE NUEVO STOCK
            Intent intent = new Intent(this, IngresarStock.class);
            intent.putExtra("DATOS", rtaCantStock);
            intent.putExtra("SOCKET", socketCliente);
            intent.putExtra("IDCC", idCCSeleccionado);
            intent.putExtra("PRODCARGADOS", (Serializable) productosCargados);
            intent.putExtra("CCCARGADOS", (Serializable) centrosCostoCargados);
            startActivity(intent);
        }else{
            Toast.makeText(this, rtaEstado, Toast.LENGTH_LONG).show();
        }
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
        }else if(respuesta.equals("Error desconocido")){
            Toast.makeText(this, "Error al finalizar la toma de inventario", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, respuesta, Toast.LENGTH_LONG).show();
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