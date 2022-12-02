package com.inventario.principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.inventario.network.SocketCliente;
import com.inventario.principal.R;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IngresarStock extends AppCompatActivity {
    private SocketCliente socketCliente;
    private TextView txtDescripcion;
    private TextView txtCodigo;
    private TextView txtStock;
    private TextView txtCosto;
    private TextView txtVenta;
    private Integer idCC;
    private Producto producto;
    private List<Producto> productosCargados = new ArrayList<>();
    private List<CentroCosto> centrosCostoCargados;
    private RadioGroup radioGroup;
    private TextView textProdYaContabilizado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_stock);
        radioGroup = findViewById(R.id.grupo_radio);
        textProdYaContabilizado = findViewById(R.id.textYaContabilizado);
        mostrarDatos();
    }

    public void mostrarDatos(){
        Intent intent = getIntent();
        socketCliente = (SocketCliente) intent.getSerializableExtra("SOCKET");
        idCC = (Integer) intent.getIntExtra("IDCC", -1);
        producto = (Producto) intent.getSerializableExtra("DATOS");
        centrosCostoCargados = (List<CentroCosto>) intent.getSerializableExtra("CCCARGADOS");
        Integer prodYaCargado = (Integer) intent.getIntExtra("YACARGADO",-2);

        if((List<Producto>) intent.getSerializableExtra("PRODCARGADOS") != null){
            productosCargados = (List<Producto>) intent.getSerializableExtra("PRODCARGADOS");
        }

        if(prodYaCargado > -1){
            producto = productosCargados.get(prodYaCargado);
            radioGroup.setVisibility(View.VISIBLE);
            textProdYaContabilizado.setVisibility(View.VISIBLE);
        }else{
            radioGroup.setVisibility(View.GONE);
            textProdYaContabilizado.setVisibility(View.GONE);
        }

        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtCodigo = findViewById(R.id.txtCodigo);
        txtStock = findViewById(R.id.txtStock);
        txtVenta = findViewById(R.id.textVenta);
        txtCosto = findViewById(R.id.textCosto);

        txtDescripcion.setText(producto.getDescripcion());
        txtCodigo.setText(producto.getCodigo());
        txtStock.setText(producto.getStock().toString());
        txtVenta.setText(producto.getVenta().toString());
        txtCosto.setText(producto.getCosto().toString());
    }

    public void ingresarStock(View view){
        RadioButton radioButton;

        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);

        //Toast.makeText(this, "Radio seleccionado: "+radioButton.getText().toString(), Toast.LENGTH_SHORT).show();

        String accion = determinarAccion(radioButton.getText().toString());

        EditText stockNuevoEditTxt = findViewById(R.id.editTxtNuevoStock);
        if(!stockNuevoEditTxt.getText().toString().equals("")){
            Float stockNuevo = Float.parseFloat(stockNuevoEditTxt.getText().toString());
            try{
                JSONObject envioStock = new JSONObject();
                envioStock.put("codigo",txtCodigo.getText().toString());
                envioStock.put("idcentrodecosto", idCC);
                envioStock.put("stock", stockNuevo);
                envioStock.put("accion", accion);

                System.out.println("ENVIO: "+envioStock);
                String resultado = socketCliente.enviarStockNuevo(envioStock);
                System.out.println("RESULTADO: "+resultado);
                if(resultado.equals("ok")){
                    Toast.makeText(this, "Stock cargado correctamente", Toast.LENGTH_LONG).show();

                    if(radioGroup.getVisibility() == RadioGroup.GONE){
                        producto.setStock(stockNuevo);
                        productosCargados.add(producto);
                    }else{
                        if(accion.equals("sumar")){
                            producto.setStock(producto.getStock() + stockNuevo);
                        }else if(accion.equals("reemplazar")){
                            producto.setStock(stockNuevo);
                        }
                    }

                    Intent intent = new Intent(this, Inventario.class);
                    intent.putExtra("STOCKOK", 1);
                    intent.putExtra("IDCC", idCC);
                    intent.putExtra("SOCKET", socketCliente);
                    intent.putExtra("PRODCARGADOS", (Serializable) productosCargados);
                    intent.putExtra("CCCARGADOS", (Serializable) centrosCostoCargados);
                    startActivity(intent);
                } else{
                    Toast.makeText(this, "Error: "+resultado, Toast.LENGTH_SHORT).show();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Debe ingresar una cantidad valida", Toast.LENGTH_SHORT).show();
        }
    }

    private String determinarAccion(String textRadioSelected){
        switch(textRadioSelected){
            case "Sumar":
                return "sumar";
            case "Reemplazar":
                return "reemplazar";
            case "No hacer nada":
                return "";
        }
        return null;
    }
}