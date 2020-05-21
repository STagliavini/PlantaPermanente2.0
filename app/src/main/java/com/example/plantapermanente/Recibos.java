package com.example.plantapermanente;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.net.URL;
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
    SharedPreferences sp;
    AlertDialog dialog;
    Map<String,Object> itempas;
    View vieww;
    String dni,nombre;
    String idEmpleado,idOrganismo,idCargo,idCategoria,fechaLiquidacion;
    private static final int PERMISSION_STORAGE_CODE=1000;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_recibos, container, false);
        sp=getActivity().getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        edtDni=view.findViewById(R.id.edtDnii);
        edtFecha_Inicial=view.findViewById(R.id.edtFecha_Inicial);
        edtFecha_Final=view.findViewById(R.id.edtFecha_Final);
        if(sp.getString("tipo","").equals("anonimo")){
            FragmentTransaction ft=getFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment,new no_autorizado()).addToBackStack(null).commit();
        }
        else{
            if(sp.getString("tipo","").equals("Empleado")){
                edtDni.setText(sp.getString("usuario",""));
                edtDni.setEnabled(false);
            }
        }
        edtFecha_Inicial.setText("");
        edtFecha_Final.setText("");
        organismo=(Spinner)view.findViewById(R.id.spinOrganismo);
        cargo=(Spinner)view.findViewById(R.id.spinCargo);
        categoria=(Spinner)view.findViewById(R.id.spinCategoria);
        llenarSpinnerOrganismo(getResources().getString(R.string.host2)+"entity.recibosueldo/listado_organismos");
        llenarSpinnerCargo(getResources().getString(R.string.host2)+"entity.recibosueldo/listado_cargos");
        llenarSpinnerCategoria(getResources().getString(R.string.host2)+"entity.recibosueldo/listado_categorias");
        edtDni.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                traerRecibos(getResources().getString(R.string.host2)+"entity.recibosueldo/listado_filtrado");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        organismo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nombre_organismo=parent.getItemAtPosition(position).toString();
                traerRecibos(getResources().getString(R.string.host2)+"entity.recibosueldo/listado_filtrado");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cargo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nombre_cargo=parent.getItemAtPosition(position).toString();
                traerRecibos(getResources().getString(R.string.host2)+"entity.recibosueldo/listado_filtrado");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                codigo_categoria=parent.getItemAtPosition(position).toString();
                traerRecibos(getResources().getString(R.string.host2)+"entity.recibosueldo/listado_filtrado");
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
                traerRecibos(getResources().getString(R.string.host2)+"entity.recibosueldo/listado_filtrado");
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
                traerRecibos(getResources().getString(R.string.host2)+"entity.recibosueldo/listado_filtrado");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        traerRecibos(getResources().getString(R.string.host2)+"entity.recibosueldo/listado_filtrado");
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
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parametros=new HashMap<>();
                parametros.put("Content-Type","application/x-www-form-urlencoded");
                return parametros;
            }
        };
        RequestQueue rq= Volley.newRequestQueue(getContext());
        rq.add(sr);
    }
    private void llenarLista(JSONArray ja){
        try{
            listrec=(ListView)view.findViewById(R.id.listrec);
            String[] datos = {"dni", "organismo", "cargo", "categoria","sueldo_total","fecha_liquidacion","codigo_empleado"
            ,"id_organismo","id_cargo","id_categoria"};
            int[] vistas = {R.id.dni, R.id.organismo,R.id.cargo, R.id.categoria, R.id.sueldo_total,R.id.fecha_liquidacion,
            R.id.codigo_empleado,R.id.id_organismo,R.id.id_cargo,R.id.id_categoria};
            recibos=new ArrayList<Map<String, Object>>();
            JSONArray jo=null;
            Map<String, Object> item;
            for(int i=0;i<ja.length();i++){
                jo=ja.getJSONArray(i);
                item = new HashMap<String, Object>();
                item.put("dni", "DNI: "+jo.getJSONObject(4).getLong("dniEmpleado"));
                item.put("organismo", "Organismo: "+jo.getJSONObject(1).getString("nombreOrganismo"));
                item.put("cargo", "Cargo: "+jo.getJSONObject(2).getString("nombreCargo"));
                item.put("categoria", "Categoria: "+jo.getJSONObject(3).getString("codigoCategoria"));
                item.put("sueldo_total", "Sueldo Total: "+jo.getJSONObject(0).getString("totalSueldo"));
                item.put("fecha_liquidacion", "Fecha de Liquidacion: "+jo.getJSONObject(0).getString("fechaLiquidacion"));
                item.put("codigo_empleado", jo.getJSONObject(4).getString("codigoEmpleado"));
                item.put("id_organismo", jo.getJSONObject(0).getString("idOrganismo"));
                item.put("id_cargo", jo.getJSONObject(0).getString("idCargo"));
                item.put("id_categoria", jo.getJSONObject(0).getString("idCategoria"));
                recibos.add(item);
            }
            SimpleAdapter adaptador =
                    new SimpleAdapter(this.getContext(), recibos,
                            R.layout.item_rec, datos, vistas);
            listrec.setAdapter(adaptador);
            listrec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    itempas = recibos.get(position);
                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                    LayoutInflater inflater=getLayoutInflater();
                    View vieww=inflater.inflate(R.layout.cuadro_accion_recibo,null);
                    builder.setView(vieww);
                    dialog=builder.create();
                    dialog.show();
                    Button btnImprimir=(Button)vieww.findViewById(R.id.btnImprimir);
                    btnImprimir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            dni=itempas.get("dni").toString().substring(5);
                            nombre=itempas.get("codigo_empleado").toString()+itempas.get("id_organismo").toString()+
                            itempas.get("id_cargo").toString()+itempas.get("id_categoria").toString();
                            idEmpleado=itempas.get("codigo_empleado").toString();
                            idOrganismo=itempas.get("id_organismo").toString();
                            idCargo=itempas.get("id_cargo").toString();
                            idCategoria=itempas.get("id_categoria").toString();
                            fechaLiquidacion=itempas.get("fecha_liquidacion").toString().substring(22);
                            String anio="";
                            String mes="";
                            Date date=null;
                            try{
                                date=new SimpleDateFormat("yyyy-MM-dd").parse(itempas.get("fecha_liquidacion").toString().substring(22));
                                mes=new SimpleDateFormat("MM").format(date);
                                anio=new SimpleDateFormat("yyyy").format(date);
                            }catch(ParseException e){
                                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                            nombre=nombre+mes+"_"+anio;
                            imprimir(getResources().getString(R.string.host2)+"entity.recibosueldo/imprimir");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                                        if(getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                                                PackageManager.PERMISSION_DENIED){
                                            String[] permissions={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                            requestPermissions(permissions,PERMISSION_STORAGE_CODE);
                                        }
                                        else{
                                            iniciarDescarga();                    }
                                    }
                                    else {
                                        iniciarDescarga();
                                    }
                                }
                            },6000);
                        }
                    });
                }
            });
        }catch(JSONException e){

        }
    }
    private void imprimir(String URL){
        StringRequest sr=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.networkResponse.toString()+"error",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros=new HashMap<>();
                parametros.put("codigo_empleado",idEmpleado);
                parametros.put("id_organismo",idOrganismo);
                parametros.put("id_cargo",idCargo);
                parametros.put("id_categoria",idCategoria);
                parametros.put("fecha_liquidacion",fechaLiquidacion);
                return parametros;
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parametros=new HashMap<>();
                parametros.put("Content-Type","application/x-www-form-urlencoded");
                return parametros;
            }
        };
        RequestQueue rq=Volley.newRequestQueue(getContext());
        rq.add(sr);
    }
    private void llenarSpinnerOrganismo(String URL){
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
                            if(jo.getString("nombreOrganismo").equals(organismos.get(j))){
                                existe=true;
                            }
                        }
                        if(!existe){
                            organismos.add(jo.getString("nombreOrganismo"));
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
                            if(jo.getString("nombreCargo").equals(cargos.get(j))){
                                existe=true;
                            }
                        }
                        if(!existe){
                            cargos.add(jo.getString("nombreCargo"));
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
                            if(jo.getString("codigoCategoria").equals(categorias.get(j))){
                                existe=true;
                            }
                        }
                        if(!existe){
                            categorias.add(jo.getString("codigoCategoria"));
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
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parametros=new HashMap<>();
                parametros.put("Content-Type","application/x-www-form-urlencoded");
                return parametros;
            }
        };
        RequestQueue rq= Volley.newRequestQueue(getContext());
        rq.add(sr);
    }
    private void llenarSpinnerCargo(String URL){
        StringRequest sr=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray ja=null;
                try{
                    boolean existe=false;
                    cargos=new ArrayList<>();
                    cargos.add("Seleccionar un Cargo");
                    dataAdapterCar=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,cargos);
                    dataAdapterCar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ja=new JSONArray(response);
                    JSONObject jo=null;
                    for(int i=0;i<ja.length();i++){
                        existe=false;
                        jo=ja.getJSONObject(i);
                        for(int j=0;j<cargos.size();j++){
                            if(jo.getString("nombreCargo").equals(cargos.get(j))){
                                existe=true;
                            }
                        }
                        if(!existe){
                            cargos.add(jo.getString("nombreCargo"));
                        }
                    }
                    cargo.setAdapter(dataAdapterCar);
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
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parametros=new HashMap<>();
                parametros.put("Content-Type","application/x-www-form-urlencoded");
                return parametros;
            }
        };
        RequestQueue rq= Volley.newRequestQueue(getContext());
        rq.add(sr);
    }
    private void llenarSpinnerCategoria(String URL){
        StringRequest sr=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray ja=null;
                try{
                    boolean existe=false;
                    categorias=new ArrayList<>();
                    categorias.add("Seleccionar una Categoria");
                    dataAdapterCat=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,categorias);
                    dataAdapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ja=new JSONArray(response);
                    JSONObject jo=null;
                    for(int i=0;i<ja.length();i++){
                        existe=false;
                        jo=ja.getJSONObject(i);
                        for(int j=0;j<categorias.size();j++){
                            if(jo.getString("codigoCategoria").equals(categorias.get(j))){
                                existe=true;
                            }
                        }
                        if(!existe){
                            categorias.add(jo.getString("codigoCategoria"));
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
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parametros=new HashMap<>();
                parametros.put("Content-Type","application/x-www-form-urlencoded");
                return parametros;
            }
        };
        RequestQueue rq= Volley.newRequestQueue(getContext());
        rq.add(sr);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case PERMISSION_STORAGE_CODE:{
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    iniciarDescarga();
                }
                else{
                    Toast.makeText(getContext(),"Permiso Denegado...!!!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void iniciarDescarga(){
        System.out.println(nombre);
        System.out.println(dni);
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(getResources().getString(R.string.host3)+nombre+".pdf"));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Recibo de Sueldo");
        request.setDescription("Descargando Documento...");
        request.setAllowedOverMetered(true);
        request.setAllowedOverRoaming(true);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,nombre+".pdf");
        DownloadManager dm=(DownloadManager)getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        dm.enqueue(request);
    }
}
