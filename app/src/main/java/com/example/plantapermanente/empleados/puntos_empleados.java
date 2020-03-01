package com.example.plantapermanente.empleados;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
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

public class puntos_empleados extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String dni;
    LatLng lt;
    Marker mk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntos_empleados);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Bundle pam=getIntent().getExtras();
        dni=pam.getString("dni");
        listarPuntos("https://tagliavinilab6.000webhostapp.com/listarPuntosEmpleados.php");
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                lt=latLng;
                AlertDialog.Builder builder=new AlertDialog.Builder(puntos_empleados.this);
                builder.setMessage("Desea agregar un punto?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        agregarPunto("https://tagliavinilab6.000webhostapp.com/agregarPuntosEmpleados.php");
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
                AlertDialog.Builder builder=new AlertDialog.Builder(puntos_empleados.this);
                builder.setMessage("Desea eliminar este punto?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eliminarPunto("https://tagliavinilab6.000webhostapp.com/borrarPuntosEmpleados.php");
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
                    Toast.makeText(puntos_empleados.this,"Ya existe un punto",Toast.LENGTH_LONG).show();
                }
                else{
                    mMap.addMarker(new MarkerOptions().position(lt).title(dni).draggable(true));
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
                parametros.put("dni_empleado",dni.substring(5));
                parametros.put("lat_punto",Double.toString(lt.latitude));
                parametros.put("long_punto",Double.toString(lt.longitude));
                return parametros;
            }
        };
        RequestQueue rq= Volley.newRequestQueue(this);
        rq.add(sr);
    }
    private void listarPuntos(String URL){
        StringRequest sr=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo=new JSONObject(response);
                    Double latitud=Double.parseDouble(jo.getString("lat_punto"));
                    Double longitud=Double.parseDouble(jo.getString("long_punto"));
                    lt=new LatLng(latitud,longitud);
                    mMap.addMarker(new MarkerOptions().position(lt).title(dni).draggable(true));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lt,16.0f));
                }
                catch(JSONException e){

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
                parametros.put("dni_empleado",dni.substring(5));
                return parametros;
            }
        };
        RequestQueue rq=Volley.newRequestQueue(this);
        rq.add(sr);
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
                parametros.put("dni_empleado",dni.substring(5));
                return parametros;
            }
        };
        RequestQueue rq= Volley.newRequestQueue(this);
        rq.add(sr);
    }
}
