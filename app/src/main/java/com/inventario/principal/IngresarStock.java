package com.inventario.principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.inventario.network.SocketCliente;
import com.inventario.principal.R;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class IngresarStock extends AppCompatActivity {
    private SocketCliente socketCliente;
    private TextView txtDescripcion;
    private TextView txtCodigo;
    private TextView txtStock;
    private Integer idCC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_stock);

        mostrarDatos();
    }

    public void mostrarDatos(){
        Intent intent = getIntent();
        socketCliente = (SocketCliente) intent.getSerializableExtra("SOCKET");
        idCC = (Integer) intent.getIntExtra("IDCC", -1);
        Producto datos = (Producto) intent.getSerializableExtra("DATOS");

        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtCodigo = findViewById(R.id.txtCodigo);
        txtStock = findViewById(R.id.txtStock);

        txtDescripcion.setText(datos.getDescripcion());
        txtCodigo.setText(datos.getCodigo());
        txtStock.setText(datos.getStock().toString());
    }

    public void ingresarStock(View view){
        EditText stockNuevoEditTxt = findViewById(R.id.editTxtNuevoStock);
        Integer stockNuevo = Integer.parseInt(stockNuevoEditTxt.getText().toString());
        try{
            JSONObject envioStock = new JSONObject();
            envioStock.put("codigo",txtCodigo.getText().toString());
            envioStock.put("centrodecosto", idCC);
            envioStock.put("stock", stockNuevo);

            Integer resultado = socketCliente.enviarStockNuevo(envioStock);

            if(resultado == 1){
                Toast.makeText(this, "Stock cargado correctamente", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("STOCKOK", 1);
                intent.putExtra("IDCC", idCC);
                intent.putExtra("SOCKET_MESSAGE", socketCliente);
                startActivity(intent);
            } else{
                Toast.makeText(this, "Ocurrio un error cargando el stock", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}