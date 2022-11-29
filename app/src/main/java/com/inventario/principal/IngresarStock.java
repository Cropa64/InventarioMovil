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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IngresarStock extends AppCompatActivity {
    private SocketCliente socketCliente;
    private TextView txtDescripcion;
    private TextView txtCodigo;
    private TextView txtStock;
    private Integer idCC;
    private Producto producto;
    private List<Producto> productosCargados = new ArrayList<>();
    private List<CentroCosto> centrosCostoCargados;

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
        producto = (Producto) intent.getSerializableExtra("DATOS");
        centrosCostoCargados = (List<CentroCosto>) intent.getSerializableExtra("CCCARGADOS");

        if((List<Producto>) intent.getSerializableExtra("PRODCARGADOS") != null){
            productosCargados = (List<Producto>) intent.getSerializableExtra("PRODCARGADOS");

            for(int i = 0; i < productosCargados.size(); i++){
                System.out.println("PRODS EN INGRESAR STOCK: "+productosCargados.get(i).getDescripcion());
            }
        }

        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtCodigo = findViewById(R.id.txtCodigo);
        txtStock = findViewById(R.id.txtStock);

        txtDescripcion.setText(producto.getDescripcion());
        txtCodigo.setText(producto.getCodigo());
        txtStock.setText(producto.getStock().toString());
    }

    public void ingresarStock(View view){
        EditText stockNuevoEditTxt = findViewById(R.id.editTxtNuevoStock);
        if(!stockNuevoEditTxt.getText().toString().equals("")){
            Float stockNuevo = Float.parseFloat(stockNuevoEditTxt.getText().toString());
            try{
                JSONObject envioStock = new JSONObject();
                envioStock.put("codigo",txtCodigo.getText().toString());
                envioStock.put("idcentrodecosto", idCC);
                envioStock.put("stock", stockNuevo);

                System.out.println("ENVIO: "+envioStock);
                String resultado = socketCliente.enviarStockNuevo(envioStock);
                System.out.println("RESULTADO: "+resultado);
                if(resultado.equals("ok")){
                    Toast.makeText(this, "Stock cargado correctamente", Toast.LENGTH_LONG).show();
                    producto.setStock(stockNuevo);
                    System.out.println("PRODUCTOS NULL?: "+productosCargados);
                    productosCargados.add(producto);
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
}