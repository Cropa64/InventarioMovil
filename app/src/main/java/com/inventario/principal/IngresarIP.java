package com.inventario.principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class IngresarIP extends AppCompatActivity {
    public static final String IP_MESSAGE_INGRESARIP = "com.inventario.principal.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_ip);
    }

    public void ingresarIP(View view){
        Intent intentConf = new Intent(this, Configuracion.class);

        EditText editTextIngresoIP = (EditText) findViewById(R.id.editTextIngresoIP);
        String IP = editTextIngresoIP.getText().toString();
        intentConf.putExtra("IP_MESSAGE_INGRESARIP", IP);
        System.out.println(2);
        startActivity(intentConf);
    }
}