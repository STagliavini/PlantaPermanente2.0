package com.example.plantapermanente.organismos;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.plantapermanente.organismos.SQLITE.DBAdapter;
import androidx.fragment.app.Fragment;

import com.example.plantapermanente.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class organismos extends Fragment {
    private DBAdapter dba;
    ListView list;
    View view;
    private List<Map<String, Object>> organismos;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_organismos, container, false);
        llenarLista();
        return view;
    }
    private void llenarLista(){
        list=(ListView)view.findViewById(R.id.listorg);
        String[] datos = {"codigo", "nombre", "telefonoorg", "direccionorg","mailorg"};
        int[] vistas = {R.id.codigo, R.id.nombre,R.id.telefonoorg, R.id.direccionorg, R.id.mailorg};
        organismos=new ArrayList<Map<String, Object>>();
        dba=new DBAdapter(this.getContext());
        dba.abrir();
        Cursor cursor=dba.getOrganismos();
        cursor.moveToFirst();
        Map<String, Object> item;
        for(int i=0;i<cursor.getCount();i++){
            item = new HashMap<String, Object>();
            item.put("codigo", "Codigo: "+cursor.getInt(1));
            item.put("nombre", "Nombre: "+cursor.getString(2));
            item.put("telefonoorg", "Telefono: "+cursor.getString(3));
            item.put("direccionorg", "Direccion: "+cursor.getString(4));
            item.put("mailorg", "Mail: "+cursor.getString(5));
            organismos.add(item);
            cursor.moveToNext();
        }
        dba.cerrar();
        SimpleAdapter adaptador =
                new SimpleAdapter(this.getContext(), organismos,
                        R.layout.item_org, datos, vistas);
        list.setAdapter(adaptador);
    }
}
