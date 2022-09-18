package com.inventario.principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class IngresarIP extends AppCompatActivity {
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
        startActivity(intentConf);
    }
}