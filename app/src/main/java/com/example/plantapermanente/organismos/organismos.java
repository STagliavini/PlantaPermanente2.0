package com.example.plantapermanente.organismos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.plantapermanente.empleados.empleado_contacto;
import com.example.plantapermanente.empleados.empleado_enviar_mail;
import com.example.plantapermanente.empleados.puntos_empleados;
import com.example.plantapermanente.organismos.SQLITE.DBAdapter;
import androidx.fragment.app.Fragment;

import com.example.plantapermanente.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class organismos extends Fragment {
    EditText codigo;
    EditText nombre;
    private DBAdapter dba;
    ListView list;
    View view;
    Map<String,Object> itempas;
    private List<Map<String, Object>> organismos;
    SharedPreferences sp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_organismos, container, false);
        sp= getActivity().getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        codigo=view.findViewById(R.id.edtCodigo);
        nombre=view.findViewById(R.id.edtNombre);
        codigo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                llenarLista();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        nombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                llenarLista();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        int cod=-1;
        if(codigo.getText().toString().isEmpty()){
            cod=-1;
        }
        else{
            try {
                cod=Integer.parseInt(codigo.getText().toString());
            }catch(NumberFormatException e){
                Toast.makeText(this.getContext(),"Numero demasiado Largo", Toast.LENGTH_LONG).show();
            }
        }
        Cursor cursor=dba.getFiltroOrganismos(cod,nombre.getText().toString());
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
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itempas = organismos.get(position);
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                LayoutInflater inflater=getLayoutInflater();
                View vieww=inflater.inflate(R.layout.cuadro_accion_organismo,null);
                builder.setView(vieww);
                AlertDialog dialog=builder.create();
                dialog.show();
                Button btnContact=vieww.findViewById(R.id.btnContact);
                Button btnMail=vieww.findViewById(R.id.btnMail);
                Button btnMapa=vieww.findViewById(R.id.btnMapaemp);
                if((itempas.get("telefonoorg").toString().substring(10)).isEmpty()){
                    btnContact.setEnabled(false);
                }
                if((itempas.get("mailorg").toString().substring(6)).isEmpty()){
                    btnMail.setEnabled(false);
                }
                btnContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tel=itempas.get("telefonoorg").toString();
                        String org=itempas.get("nombre").toString();
                        tel=tel.substring(10);
                        org=org.substring(7);
                        Intent intencion=new Intent(getContext(), organismo_contacto.class);
                        Bundle pam= new Bundle();
                        pam.putString("tel",tel);
                        pam.putString("org",org);
                        intencion.putExtras(pam);
                        startActivity(intencion);
                    }
                });
                btnMail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mail=itempas.get("mailorg").toString();
                        mail=mail.substring(6);
                        Intent intencion=new Intent(getContext(), empleado_enviar_mail.class);
                        Bundle pam= new Bundle();
                        pam.putString("mail",mail);
                        intencion.putExtras(pam);
                        startActivity(intencion);
                    }
                });
                btnMapa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String codigo=itempas.get("codigo").toString();
                        Intent intencion=new Intent(getContext(), puntos_organismos.class);
                        Bundle pam= new Bundle();
                        pam.putString("codigo",codigo);
                        intencion.putExtras(pam);
                        startActivity(intencion);
                    }
                });
            }
        });
    }
}
