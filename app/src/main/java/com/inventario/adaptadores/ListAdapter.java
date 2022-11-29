package com.inventario.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inventario.principal.Producto;
import com.inventario.principal.R;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter<Producto> {

    public ListAdapter(Context context, List<Producto> productos){
        super(context, R.layout.list_item, productos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Producto producto = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }

        TextView nombreProducto = convertView.findViewById(R.id.nombre_producto_lista);
        TextView cantStockNueva = convertView.findViewById(R.id.stock_cargado);

        nombreProducto.setText(producto.getDescripcion());
        cantStockNueva.setText("Stock nuevo: "+producto.getStock().toString());

        return convertView;
    }
}
