package com.example.plantapermanente;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.textclassifier.ConversationActions;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.plantapermanente.organismos.SQLITE.DBAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button inises;
    Button anonimo;
    EditText usu;
    EditText cont;
    Switch recor;
    TextView error_usuario;
    TextView error_contrasenia;
    JSONObject jos;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    DBAdapter dba;
    DBAdapter back;
    NotificationCompat.Builder notificacion;
    Cursor cursorr;
    int metodo;
    private static final int idUnica=51623;
    private static final String CHANNEL_ID="Notificacion";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp=getSharedPreferences("Sesion",MODE_PRIVATE);
        editor=sp.edit();
        editor.putString("usuario","anonimo");
        editor.putString("contrasenia","anonimo");
        editor.putString("tipo","anonimo");
        editor.commit();
        actualizarBase(getResources().getString(R.string.host2) + "entity.organismo/listado_filtrado");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intencion=new Intent(MainActivity.this,MenuDrawerAnonimo.class);
                startActivity(intencion);
                finish();
            };
        },3000);
    }
    private void actualizarBase(String URL){
        dba=new DBAdapter(this);
        StringRequest jsonRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
        @Override
            public void onResponse(String response) {
                if(!response.equals("[]")){
                    JSONArray ja=null;
                    try{
                        ja=new JSONArray(response);
                        dba.abrir();
                        Cursor cursor=dba.getOrganismos();
                        Toast.makeText(getApplicationContext(),cursor.getCount(),Toast.LENGTH_LONG).show();
                        JSONObject jo=null;
                        boolean insertado=false;
                        boolean actualizado=false;
                        for(int i=0;i<ja.length();i++){
                            boolean existe=false;
                            jo=ja.getJSONObject(i);
                            cursor.moveToFirst();
                            for(int j=0;j<cursor.getCount()&&!existe;j++){
                                if(cursor.getInt(0)==jo.getInt("idOrganismo")){
                                    existe=true;
                                }
                                cursor.moveToNext();
                            }
                            jos=jo;
                            cursorr=cursor;
                            back=dba;
                            if(!existe){
                                metodo=0;
                                cotejarPuntos(getResources().getString(R.string.host2) + "entity.puntosorganismo/listado");
                            }
                            else{
                                metodo=1;
                                cotejarPuntos(getResources().getString(R.string.host2) + "entity.puntosorganismo/listado");
                            }
                        }
                        dba.cerrar();
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonRequest);
    }
    private void cotejarPuntos(String URL){
        StringRequest jsonRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("[]")){
                    JSONArray ja=null;
                    try{
                        ja=new JSONArray(response);
                        Toast.makeText(getApplicationContext(),cursorr.getCount(),Toast.LENGTH_LONG).show();
                        JSONObject js=null;
                        boolean cotejado=false;
                        boolean insertado=false;
                        boolean actualizado=false;
                        int i=0;
                        while(i<ja.length()&&!cotejado){
                            js=ja.getJSONObject(i);
                            if(jos.getInt("codigoOrganismo")==js.getInt("codigoOrganismop")){
                                cotejado=true;
                            }
                            else{
                                i++;
                            }
                        }
                        float lat_punto;
                        float long_punto;
                        boolean estado_organismo=false;
                        estado_organismo=jos.getBoolean("estadoOrganismo");
                        try{
                            if(cotejado){
                                lat_punto=Float.parseFloat(js.getString("latPunto"));
                                long_punto=Float.parseFloat(js.getString("longPunto"));
                            }
                            else{
                                lat_punto=0;
                                long_punto=0;
                            }
                        }
                        catch(NumberFormatException e){
                            lat_punto=0;
                            long_punto=0;
                        }
                        if (metodo == 0) {
                            back.insertarOrganismo(jos.getInt("idOrganismo"),jos.getInt("codigoOrganismo"),jos.getString("nombreOrganismo"),
                                    jos.getString("telefonoOrganismo"),jos.getString("direccionOrganismo"),jos.getString("mailOrganismo"),estado_organismo,lat_punto,long_punto);
                            insertado=true;
                        }
                        else{
                            if(metodo==1){
                                cursorr.moveToPrevious();
                                if(jos.getInt("codigoOrganismo")!=cursorr.getInt(1)||
                                        !jos.getString("nombreOrganismo").equals(cursorr.getString(2))||
                                        !jos.getString("telefonoOrganismo").equals(cursorr.getString(3))||
                                        !jos.getString("direccionOrganismo").equals(cursorr.getString(4))||
                                        !jos.getString("mailOrganismo").equals(cursorr.getString(5))||
                                        jos.getInt("estadoOrganismo")!=cursorr.getInt(6)||
                                        lat_punto!=cursorr.getFloat(7)||
                                        long_punto!=cursorr.getFloat(8)){
                                    back.actualizarOrganismo(jos.getInt("idOrganismo"),jos.getInt("codigoOrganismo"),jos.getString("nombreOrganismo"),
                                            jos.getString("telefonoOrganismo"),jos.getString("direccionOrganismo"),jos.getString("mailOrganismo"),estado_organismo,lat_punto,long_punto);

                                    actualizado=true;
                                }
                            }
                        }
                        if(insertado){
                            createNotificationChannel();
                            mostrarNotificacion("Nuevos registros fueron insertados en su base de datos local");
                        }
                        if(actualizado){
                            createNotificationChannel();
                            mostrarNotificacion("Anteriores registros fueron actualizados en su base de datos local");
                        }
                    }catch(JSONException e){
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonRequest);
    }
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name="canal 1";
            String description="canal 1";
            int importance=NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);
            NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(channel);
        }
    }
    private void mostrarNotificacion(String text){
        notificacion=new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
        notificacion.setSmallIcon(R.drawable.ic_business_center_black_24dp);
        notificacion.setTicker("Base de Datos Actualizada");
        notificacion.setContentTitle("Base de Datos Actualizada");
        notificacion.setContentText(text);
        notificacion.setDefaults(NotificationCompat.DEFAULT_SOUND);
        NotificationManagerCompat nm=NotificationManagerCompat.from(getApplicationContext());
        nm.notify(idUnica,notificacion.build());
    }
}
