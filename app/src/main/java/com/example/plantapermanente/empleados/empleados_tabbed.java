package com.example.plantapermanente.empleados;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.plantapermanente.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class empleados_tabbed extends Fragment {
    private List<Map<String, Object>> empleados;
    EditText dni;
    EditText ape;
    EditText nom;
    ListView list;
    View view;
    Map<String,Object> itempas;
    SharedPreferences sp;
@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    view=inflater.inflate(R.layout.fragment_empleados_tabbed, container, false);
    sp= getActivity().getSharedPreferences("Sesion", Context.MODE_PRIVATE);
    dni=view.findViewById(R.id.edtDni);
    ape=view.findViewById(R.id.edtApe);
    nom=view.findViewById(R.id.edtNom);
    if(sp.getString("tipo","").equals("Empleado")){
        dni.setEnabled(false);
        dni.setText(sp.getString("usuario",""));
    }
    dni.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            traerEmpleados("https://tagliavinilab6.000webhostapp.com/listarEmpleados.php");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    });
    ape.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            traerEmpleados("https://tagliavinilab6.000webhostapp.com/listarEmpleados.php");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    });
    nom.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            traerEmpleados("https://tagliavinilab6.000webhostapp.com/listarEmpleados.php");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    });
    traerEmpleados("https://tagliavinilab6.000webhostapp.com/listarEmpleados.php");
    return view;
    }
    private void traerEmpleados(String URL) {
        StringRequest jsonArrayRequest=new StringRequest(Request.Method.POST,URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray ja=null;
                try{
                    ja=new JSONArray(response);
                }
                catch (JSONException e){

                }
                llenarLista(ja);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<>();
                if(!dni.getText().toString().isEmpty()){
                    parametros.put("dni_empleado",dni.getText().toString());
                }
                if(!ape.getText().toString().isEmpty()){
                    parametros.put("apellido_empleado",ape.getText().toString());
                }
                if(!nom.getText().toString().isEmpty()){
                    parametros.put("nombre_empleado",nom.getText().toString());
                }
                return parametros;
            }
        };
        RequestQueue rq= Volley.newRequestQueue(this.getContext());
        rq.add(jsonArrayRequest);
    }
    private void llenarLista(JSONArray ja){
        try{
            list=(ListView)view.findViewById(R.id.listemp);
            String[] datos = {"dni", "apnom", "fecnac", "sexo","telefono","direccion","mail"};
            int[] vistas = {R.id.dni, R.id.apnom,R.id.fecnac, R.id.sexo, R.id.telefono,R.id.direccion,R.id.mail};
            empleados=new ArrayList<Map<String, Object>>();
            JSONObject jo=null;
            Map<String, Object> item;
            for(int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                item = new HashMap<String, Object>();
                item.put("dni", "DNI: "+jo.getLong("dni_empleado"));
                item.put("apnom", "Apellido y Nombre: "+jo.getString("apellido_empleado")+" "+jo.getString("nombre_empleado"));
                item.put("fecnac", "Fecha de Nacimiento"+jo.getString("nacimiento_empleado"));
                item.put("sexo", "Sexo: "+jo.getString("sexo_empleado"));
                item.put("telefono", "Telefono: "+jo.getString("telefono_empleado"));
                item.put("direccion", "Direccion: "+jo.getString("direccion_empleado"));
                item.put("mail", "Mail: "+jo.getString("mail_empleado"));
                empleados.add(item);
            }
            SimpleAdapter adaptador =
                    new SimpleAdapter(this.getContext(), empleados,
                            R.layout.item_emp, datos, vistas);
            list.setAdapter(adaptador);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    itempas = empleados.get(position);
                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                    LayoutInflater inflater=getLayoutInflater();
                    View vieww=inflater.inflate(R.layout.cuadro_accion_empleado,null);
                    builder.setView(vieww);
                    AlertDialog dialog=builder.create();
                    dialog.show();
                    Button btnContact=vieww.findViewById(R.id.btnContact);
                    Button btnMail=vieww.findViewById(R.id.btnMail);
                    Button btnMapa=vieww.findViewById(R.id.btnMapaemp);
                    if(sp.getString("tipo","").equals("Empleado")){
                        btnContact.setVisibility(View.GONE);
                        btnMail.setVisibility(View.GONE);
                    }
                    if((itempas.get("telefono").toString().substring(10,itempas.get("telefono").toString().length())).isEmpty()){
                        btnContact.setEnabled(false);
                    }
                    if((itempas.get("mail").toString().substring(6,itempas.get("mail").toString().length())).isEmpty()){
                        btnMail.setEnabled(false);
                    }
                    btnContact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String tel=itempas.get("telefono").toString();
                            String apenom=itempas.get("apnom").toString();
                            tel=tel.substring(10);
                            apenom=apenom.substring(19);
                            Intent intencion=new Intent(getContext(),empleado_contacto.class);
                            Bundle pam= new Bundle();
                            pam.putString("tel",tel);
                            pam.putString("apenom",apenom);
                            intencion.putExtras(pam);
                            startActivity(intencion);
                        }
                    });
                    btnMail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String mail=itempas.get("mail").toString();
                            mail=mail.substring(6,mail.length());
                            Intent intencion=new Intent(getContext(),empleado_enviar_mail.class);
                            Bundle pam= new Bundle();
                            pam.putString("mail",mail);
                            intencion.putExtras(pam);
                            startActivity(intencion);
                        }
                    });
                    btnMapa.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String dni=itempas.get("dni").toString();
                            Intent intencion=new Intent(getContext(),puntos_empleados.class);
                            Bundle pam= new Bundle();
                            pam.putString("dni",dni);
                            intencion.putExtras(pam);
                            startActivity(intencion);
                        }
                    });
                }
            });
        }catch (JSONException e){

        }
    }
}
