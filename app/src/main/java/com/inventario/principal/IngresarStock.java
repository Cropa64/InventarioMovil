package com.inventario.principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.inventario.principal.R;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class IngresarStock extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_stock);

        mostrarDatos();
    }

    public void mostrarDatos(){
        Intent intent = getIntent();
        String[] datos = (String[]) intent.getStringArrayExtra("DATOS");
        System.out.println(datos[0] + datos[1] + datos[2]);

        TextView txtDescripcion = findViewById(R.id.txtDescripcion);
        TextView txtCodigo = findViewById(R.id.txtCodigo);
        TextView txtStock = findViewById(R.id.txtStock);

        txtDescripcion.setText(datos[1]);
        txtCodigo.setText(datos[0]);
        txtStock.setText(datos[2]);
    }
}