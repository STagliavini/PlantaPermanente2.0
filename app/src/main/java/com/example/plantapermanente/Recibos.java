package com.example.plantapermanente;


import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.plantapermanente.empleados.empleados;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Recibos extends Fragment {
    private List<Map<String, Object>> recibos;
    View view;
    EditText edtDni;
    EditText edtFecha_Inicial;
    EditText edtFecha_Final;
    Spinner organismo;
    Spinner cargo;
    Spinner categoria;
    List<String>organismos;
    ArrayAdapter<String>dataAdapterOrg;
    List<String>cargos;
    ArrayAdapter<String>dataAdapterCar;
    List<String>categorias;
    ArrayAdapter<String>dataAdapterCat;
    ListView listrec;
    String nombre_organismo="Seleccionar un Organismo";
    String nombre_cargo="Seleccionar un Cargo";
    String codigo_categoria="Seleccionar una Categoria";
    String fecha_inicial="";
    String fecha_final="";
    DatePickerDialog dpd;
    Calendar c;
    int dia,mes,anio=2020;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_recibos, container, false);
        edtDni=view.findViewById(R.id.edtDnii);
        edtFecha_Inicial=view.findViewById(R.id.edtFecha_Inicial);
        edtFecha_Final=view.findViewById(R.id.edtFecha_Final);
        edtFecha_Inicial.setText("");
        edtFecha_Final.setText("");
        organismo=(Spinner)view.findViewById(R.id.spinOrganismo);
        cargo=(Spinner)view.findViewById(R.id.spinCargo);
        categoria=(Spinner)view.findViewById(R.id.spinCategoria);
        llenarSpinners(getResources().getString(R.string.host2)+"listarRecibos.php");
        edtDni.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                traerRecibos(getResources().getString(R.string.host2)+"listarRecibos.php");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        organismo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nombre_organismo=parent.getItemAtPosition(position).toString();
                traerRecibos(getResources().getString(R.string.host2)+"listarRecibos.php");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cargo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nombre_cargo=parent.getItemAtPosition(position).toString();
                traerRecibos(getResources().getString(R.string.host2)+"listarRecibos.php");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                codigo_categoria=parent.getItemAtPosition(position).toString();
                traerRecibos(getResources().getString(R.string.host2)+"listarRecibos.php");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        edtFecha_Inicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dia=1;
                dpd=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        edtFecha_Inicial.setText((month+1)+"/"+year);
                        Date date=null;
                        try{
                            date=new SimpleDateFormat("yyyy-MM-dd").parse(year+"-"+(month+1)+"-"+dayOfMonth);
                        }catch(ParseException e){

                        }
                        c=Calendar.getInstance();
                        c.setTime(date);
                        dia=c.getActualMaximum(Calendar.DAY_OF_MONTH);
                        mes=c.get(Calendar.MONTH)+1;
                        anio=c.get(Calendar.YEAR);
                        fecha_inicial=anio+"-"+mes+"-"+dia;
                    }
                },anio,mes-1,dia);
                dpd.show();
            }
        });
        edtFecha_Final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dia=1;
                dpd=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        edtFecha_Final.setText((month+1)+"/"+year);
                        Date date=null;
                        try{
                            date=new SimpleDateFormat("yyyy-MM-dd").parse(year+"-"+(month+1)+"-"+dayOfMonth);
                        }catch(ParseException e){

                        }
                        c=Calendar.getInstance();
                        c.setTime(date);
                        dia=c.getActualMaximum(Calendar.DAY_OF_MONTH);
                        mes=c.get(Calendar.MONTH)+1;
                        anio=c.get(Calendar.YEAR);
                        fecha_final=anio+"-"+mes+"-"+dia;
                    }
                },anio,mes-1,dia);
                dpd.show();
            }
        });
        edtFecha_Inicial.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                traerRecibos(getResources().getString(R.string.host2)+"listarRecibos.php");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtFecha_Final.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                traerRecibos(getResources().getString(R.string.host2)+"listarRecibos.php");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        traerRecibos(getResources().getString(R.string.host2)+"listarRecibos.php");
        return view;
    }
    private void traerRecibos(String URL){
        StringRequest sr=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray ja=null;
                try{
                    ja=new JSONArray(response);
                }catch(JSONException e){
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
                Map<String,String>parametros=new HashMap<>();
                parametros.put("dni_empleado",edtDni.getText().toString());
                parametros.put("nombre_organismo",nombre_organismo);
                parametros.put("nombre_cargo",nombre_cargo);
                parametros.put("codigo_categoria",codigo_categoria);
                parametros.put("fecha_inicial",fecha_inicial);
                parametros.put("fecha_final",fecha_final);
                return parametros;
            }
        };
        RequestQueue rq= Volley.newRequestQueue(getContext());
        rq.add(sr);
    }
    private void llenarLista(JSONArray ja){
        try{
            listrec=(ListView)view.findViewById(R.id.listrec);
            String[] datos = {"dni", "organismo", "cargo", "categoria","sueldo_total","fecha_liquidacion"};
            int[] vistas = {R.id.dni, R.id.organismo,R.id.cargo, R.id.categoria, R.id.sueldo_total,R.id.fecha_liquidacion};
            recibos=new ArrayList<Map<String, Object>>();
            JSONObject jo=null;
            Map<String, Object> item;
            for(int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                item = new HashMap<String, Object>();
                item.put("dni", "DNI: "+jo.getLong("dni_empleado"));
                item.put("organismo", "Organismo: "+jo.getString("nombre_organismo"));
                item.put("cargo", "Cargo: "+jo.getString("nombre_cargo"));
                item.put("categoria", "Categoria: "+jo.getString("codigo_categoria"));
                item.put("sueldo_total", "Sueldo Total: "+jo.getString("total_sueldo"));
                item.put("fecha_liquidacion", "Fecha de Liquidacion: "+jo.getString("fecha_liquidacion"));
                recibos.add(item);
            }
            SimpleAdapter adaptador =
                    new SimpleAdapter(this.getContext(), recibos,
                            R.layout.item_rec, datos, vistas);
            listrec.setAdapter(adaptador);
        }catch(JSONException e){

        }
    }
    private void llenarSpinners(String URL){
        StringRequest sr=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray ja=null;
                try{
                    boolean existe=false;
                    organismos=new ArrayList<>();
                    organismos.add("Seleccionar un Organismo");
                    dataAdapterOrg=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,organismos);
                    dataAdapterOrg.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ja=new JSONArray(response);
                    JSONObject jo=null;
                    for(int i=0;i<ja.length();i++){
                        existe=false;
                        jo=ja.getJSONObject(i);
                        for(int j=0;j<organismos.size();j++){
                            if(jo.getString("nombre_organismo").equals(organismos.get(j))){
                                existe=true;
                            }
                        }
                        if(!existe){
                            organismos.add(jo.getString("nombre_organismo"));
                        }
                    }
                    organismo.setAdapter(dataAdapterOrg);
                    cargos=new ArrayList<>();
                    cargos.add("Seleccionar un Cargo");
                    dataAdapterCar=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,cargos);
                    dataAdapterCar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    for(int i=0;i<ja.length();i++){
                        existe=false;
                        jo=ja.getJSONObject(i);
                        for(int j=0;j<cargos.size();j++){
                            if(jo.getString("nombre_cargo").equals(cargos.get(j))){
                                existe=true;
                            }
                        }
                        if(!existe){
                            cargos.add(jo.getString("nombre_cargo"));
                        }
                    }
                    cargo.setAdapter(dataAdapterCar);
                    categorias=new ArrayList<>();
                    categorias.add("Seleccionar una Categoria");
                    dataAdapterCat=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,categorias);
                    dataAdapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    for(int i=0;i<ja.length();i++){
                        existe=false;
                        jo=ja.getJSONObject(i);
                        for(int j=0;j<categorias.size();j++){
                            if(jo.getString("codigo_categoria").equals(categorias.get(j))){
                                existe=true;
                            }
                        }
                        if(!existe){
                            categorias.add(jo.getString("codigo_categoria"));
                        }
                    }
                    categoria.setAdapter(dataAdapterCat);
                }catch(JSONException e){
                }
             }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>parametros=new HashMap<>();
                return parametros;
            }
        };
        RequestQueue rq= Volley.newRequestQueue(getContext());
        rq.add(sr);
    }
}
