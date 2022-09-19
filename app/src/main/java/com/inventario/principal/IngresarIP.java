package com.inventario.principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class IngresarIP extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_ip);
    }

    //AL DARLE AL BOTON INGRESAR IP SE LA ENVIO A LA CLASE CONFIGURACION
    public void ingresarIP(View view){
        Intent intentConf = new Intent(this, Configuracion.class);

        EditText editTextIngresoIP = (EditText) findViewById(R.id.editTextIngresoIP);
        String IP = editTextIngresoIP.getText().toString();

        if(!IP.equals("")){
            intentConf.putExtra("IP_MESSAGE_INGRESARIP", IP);
            startActivity(intentConf);
        }else{
            Toast.makeText(this, "Debe ingresar una IP", Toast.LENGTH_SHORT).show();
        }
    }
}