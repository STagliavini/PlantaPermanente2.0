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
    JSONArray jaaa;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    DBAdapter dba;
    NotificationCompat.Builder notificacion;
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
        actualizarBase(getResources().getString(R.string.host2)+"entity.organismo/listado_filtrado");
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
                        jaaa=new JSONArray(response);
                        cotejarPuntos(getResources().getString(R.string.host2)+"entity.puntosorganismo/listado");
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
                        dba.abrir();
                        Cursor cursor=dba.getOrganismos();
                        JSONObject jo=null;
                        boolean cotejado;
                        boolean insertado=false;
                        boolean actualizado=false;
                        for(int i=0;i<jaaa.length();i++){
                            boolean existe=false;
                            jo=jaaa.getJSONObject(i);
                            cursor.moveToFirst();
                            for(int j=0;j<cursor.getCount()&&!existe;j++){
                                if(cursor.getInt(0)==jo.getInt("idOrganismo")){
                                    existe=true;
                                }
                                cursor.moveToNext();
                            }
                            cursor.moveToPrevious();
                            if(!existe){
                                metodo=0;
                            }
                            else{
                                metodo=1;
                            }
                            ja=new JSONArray(response);
                            JSONObject js=null;
                            cotejado=false;
                            int z=0;
                            while(z<ja.length()&&!cotejado){
                                js=ja.getJSONObject(z);
                                if(jo.getInt("codigoOrganismo")==js.getInt("codigoOrganismop")){
                                    cotejado=true;
                                }
                                else{
                                    z++;
                                }
                            }
                            float lat_punto;
                            float long_punto;
                            boolean estado_organismo=false;
                            estado_organismo=jo.getBoolean("estadoOrganismo");
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
                                dba.insertarOrganismo(jo.getInt("idOrganismo"),jo.getInt("codigoOrganismo"),jo.getString("nombreOrganismo"),
                                        jo.getString("telefonoOrganismo"),jo.getString("direccionOrganismo"),jo.getString("mailOrganismo"),estado_organismo,lat_punto,long_punto);
                                insertado=true;
                            }
                            else{
                                if(metodo==1){
                                    int valor_estado;
                                    if(jo.getBoolean("estadoOrganismo")){
                                        valor_estado=1;
                                    }
                                    else{
                                        valor_estado=0;
                                    }
                                    if(jo.getInt("codigoOrganismo")!=cursor.getInt(1)||
                                            !jo.getString("nombreOrganismo").equals(cursor.getString(2))||
                                            !jo.getString("telefonoOrganismo").equals(cursor.getString(3))||
                                            !jo.getString("direccionOrganismo").equals(cursor.getString(4))||
                                            !jo.getString("mailOrganismo").equals(cursor.getString(5))||
                                            valor_estado!=cursor.getInt(6)||
                                            lat_punto!=cursor.getFloat(7)||
                                            long_punto!=cursor.getFloat(8)){
                                        dba.actualizarOrganismo(jo.getInt("idOrganismo"),jo.getInt("codigoOrganismo"),jo.getString("nombreOrganismo"),
                                                jo.getString("telefonoOrganismo"),jo.getString("direccionOrganismo"),jo.getString("mailOrganismo"),estado_organismo,lat_punto,long_punto);
                                        actualizado=true;
                                    }
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
                        dba.cerrar();
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
