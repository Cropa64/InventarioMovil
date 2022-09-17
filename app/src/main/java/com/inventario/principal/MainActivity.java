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

import com.inventario.network.SocketCliente;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    public static final String IP_MESSAGE_SERVER = "com.inventario.principal.MESSAGE";
    public static final String CC_MESSAGE_SERVER = "com.inventario.principal.MESSAGE";
    private String IP;
    private List<CentroCosto> centrosCostoCargados;
    private SocketCliente socketCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DWUtilities.CreateDWProfile(this);
        Button btnScan = findViewById(R.id.btnScan);
        btnScan.setOnTouchListener(this);
        //DWUtilities.crearIPPlugin(this, IP);

        consultarIntents(getIntent());
    }

    public void consultarIntents(Intent intent){
        IP = intent.getStringExtra("IP_MESSAGE");
        centrosCostoCargados = (List<CentroCosto>) intent.getSerializableExtra("CC_MESSAGE");
        socketCliente = (SocketCliente) intent.getSerializableExtra("SOCKET_MESSAGE");
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        displayScanResult(intent);
    }

    private void displayScanResult(Intent scanIntent)
    {
        String decodedSource = scanIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source));
        String decodedData = scanIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        String decodedLabelType = scanIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));
        String scan = decodedData;
        final TextView output = findViewById(R.id.txtOutput);

        JSONObject objetoJson = new JSONObject();
        try{
            objetoJson.put("codigo",scan);
            objetoJson.put("centrodecosto",idCCSeleccionado);
        }catch(Exception e){
            e.printStackTrace();
        }
        output.setText(objetoJson.toString() + output.getText());

        //ENVIA AL SERVER JSON
        //socketCliente.enviarMsg(objetoJson.toString());
        String[] rtaCantStock = socketCliente.obtenerCantStock(objetoJson.toString());
        Intent intent = new Intent(this, IngresarStock.class);
        intent.putExtra("DATOS", rtaCantStock);
        startActivity(intent);
    }

    //public void ingresarIP(View view){
    //    Intent intent = new Intent(this, IngresarIP.class);
    //    startActivity(intent);
    //}

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
                    }
                })
                .show();
    }

    Integer idCCSeleccionado;
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
        if(IP != null){
            intent.putExtra("IP_MESSAGE_SERVER",IP);
            intent.putExtra("CC_MESSAGE_SERVER",(Serializable) centrosCostoCargados);
        }
        startActivity(intent);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.btnScan)
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