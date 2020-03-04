package com.example.plantapermanente.organismos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.plantapermanente.R;
import com.example.plantapermanente.empleados.puntos_empleados;
import com.example.plantapermanente.organismos.SQLITE.DBAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class puntos_organismos extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    String id;
    LatLng lt;
    Marker mk;
    DBAdapter dba;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntos_organismos);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        dba=new DBAdapter(this.getApplicationContext());
        mMap = googleMap;
        Bundle pam=getIntent().getExtras();
        id=pam.getString("codigo");
        listarPuntos("https://tagliavinilab6.000webhostapp.com/listarPuntosOrganismos.php");
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                lt=latLng;
                AlertDialog.Builder builder=new AlertDialog.Builder(puntos_organismos.this);
                builder.setMessage("Desea agregar un punto?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean encontrado=false;
                                dba.abrir();
                                Cursor cursor=dba.getOrganismos();
                                cursor.moveToFirst();
                                for(int i=0;i<cursor.getCount()&&!encontrado;i++){
                                    if(cursor.getInt(1)==Integer.parseInt(id.substring(8))){
                                       encontrado=true;
                                    }
                                    else{
                                        cursor.moveToNext();
                                    }
                                }
                                if(encontrado){
                                    float lat_punto=Float.parseFloat(Double.toString(lt.latitude));
                                    float long_punto=Float.parseFloat(Double.toString(lt.longitude));
                                    boolean estado=cursor.getInt(6)>0;
                                    if(cursor.getFloat(7)==0&&cursor.getFloat(8)==0){
                                        dba.actualizarOrganismo(cursor.getInt(0),cursor.getInt(1),
                                                cursor.getString(2),cursor.getString(3),cursor.getString(4),
                                                cursor.getString(5),estado,lat_punto,long_punto);
                                    }
                                    else{
                                        Toast.makeText(puntos_organismos.this,"Ya existe un punto",Toast.LENGTH_LONG).show();
                                    }
                                }
                                dba.cerrar();
                                agregarPunto("https://tagliavinilab6.000webhostapp.com/agregarPuntosOrganismos.php");
                            }
                        });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                mk=marker;
                AlertDialog.Builder builder=new AlertDialog.Builder(puntos_organismos.this);
                builder.setMessage("Desea eliminar este punto?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean encontrado=false;
                                dba.abrir();
                                Cursor cursor=dba.getOrganismos();
                                cursor.moveToFirst();
                                for(int i=0;i<cursor.getCount()&&!encontrado;i++){
                                    if(cursor.getInt(1)==Integer.parseInt(id.substring(8))){
                                        encontrado=true;
                                    }
                                    else{
                                        cursor.moveToNext();
                                    }
                                }
                                if(encontrado){
                                    boolean estado=cursor.getInt(6)>0;
                                    if(cursor.getFloat(7)!=0&&cursor.getFloat(8)!=0){
                                        dba.actualizarOrganismo(cursor.getInt(0),cursor.getInt(1),
                                                cursor.getString(2),cursor.getString(3),cursor.getString(4),
                                                cursor.getString(5),estado,0,0);
                                    }
                                }
                                dba.cerrar();
                                eliminarPunto("https://tagliavinilab6.000webhostapp.com/borrarPuntosOrganismos.php");
                            }
                        });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });
    }
    private void agregarPunto(String URL){
        StringRequest sr= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("PuntoExiste")){
                    Toast.makeText(puntos_organismos.this,"Ya existe un punto",Toast.LENGTH_LONG).show();
                }
                else{
                    mMap.addMarker(new MarkerOptions().position(lt).title(id).draggable(true));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<>();
                parametros.put("codigo_organismop",id.substring(8));
                parametros.put("lat_punto",Double.toString(lt.latitude));
                parametros.put("long_punto",Double.toString(lt.longitude));
                return parametros;
            }
        };
        RequestQueue rq= Volley.newRequestQueue(this);
        rq.add(sr);
    }
    private void listarPuntos(String URL){
        dba=new DBAdapter(this.getApplicationContext());
        boolean encontrado=false;
        dba.abrir();
        Cursor cursor=dba.getOrganismos();
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount()&&!encontrado;i++){
            if(cursor.getInt(1)==Integer.parseInt(id.substring(8))){
                encontrado=true;
            }
            else{
                cursor.moveToNext();
            }
        }
        if(encontrado){
            if(cursor.getFloat(7)!=0&&cursor.getFloat(8)!=0){
                Double latitud=Double.parseDouble(Float.toString(cursor.getFloat(7)));
                Double longitud=Double.parseDouble(Float.toString(cursor.getFloat(8)));
                lt=new LatLng(latitud,longitud);
                mMap.addMarker(new MarkerOptions().position(lt).title(id).draggable(true));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lt,16.0f));
            }
        }
        dba.cerrar();
    }
    private void eliminarPunto(String URL){
        StringRequest sr=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mk.remove();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros=new HashMap<>();
                parametros.put("codigo_organismop",id.substring(8));
                return parametros;
            }
        };
        RequestQueue rq= Volley.newRequestQueue(this);
        rq.add(sr);
    }
}
