package com.example.plantapermanente.login;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.plantapermanente.MainActivity;
import com.example.plantapermanente.MenuDrawer;
import com.example.plantapermanente.R;
import com.example.plantapermanente.organismos.SQLITE.DBAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login extends Fragment {
    View view;
    Button inises;
    Button anonimo;
    EditText usu;
    EditText cont;
    Switch recor;
    TextView error_usuario;
    TextView error_contrasenia;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    DBAdapter dba;
    NotificationCompat.Builder notificacion;
    private static final int idUnica=51623;
    private static final String CHANNEL_ID="Notificacion";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_login, container, false);
        actualizarBase(getResources().getString(R.string.host)+"listarOrganismos.php");
        dba.abrir();
        Cursor cursor=dba.getOrganismos();
        cursor.moveToFirst();
        for(int j=0;j<cursor.getCount();j++){
            cursor.moveToNext();
        }
        dba.cerrar();
        inises=(Button)view.findViewById(R.id.btnLogin);
        usu=(EditText) view.findViewById(R.id.edtUsuario);
        cont=(EditText)view.findViewById(R.id.edtPassword);
        recor=(Switch)view.findViewById(R.id.mantses);
        error_usuario=(TextView)view.findViewById(R.id.errorUsuario);
        error_contrasenia=(TextView)view.findViewById(R.id.errorPassword);
        sp=getActivity().getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        if(sp.getBoolean("recor",false)==true&&!sp.getString("tipo","").equals("anonimo")){
            recor.setChecked(true);
            usu.setText(sp.getString("usuario",""));
            cont.setText(sp.getString("contrasenia",""));
        }
        inises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usu.getText().toString().isEmpty()){
                    error_usuario.setText("Debe ingresar un usuario");
                }
                else{
                    error_usuario.setText("");
                }
                if(cont.getText().toString().isEmpty()){
                    error_contrasenia.setText("Debe ingresar una contrasenia");
                }
                else{
                    error_contrasenia.setText("");
                }
                if(error_usuario.getText().toString().isEmpty()&&error_contrasenia.getText().toString().isEmpty()){
                    verificarUsuario(getResources().getString(R.string.host2)+"entity.usuario/listado_filtrado");
                }
            }
        });
        recor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(recor.isChecked()==false){
                    sp=getActivity().getSharedPreferences("Sesion", Context.MODE_PRIVATE);
                    editor=sp.edit();
                    editor.putBoolean("recor",false);
                    editor.commit();
                }
            }
        });
        return view;
    }
    private void verificarUsuario(String URL){
        StringRequest jsonRequest= new StringRequest(Request.Method.POST,URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("[]")){
                    try{
                        JSONArray jo=new JSONArray(response);
                        sp=getActivity().getSharedPreferences("Sesion", Context.MODE_PRIVATE);
                        editor=sp.edit();
                        editor.putString("usuario",jo.getJSONObject(0).getString("nombreUsuario"));
                            editor.putString("contrasenia",jo.getJSONObject(0).getString("contraseniaUsuario"));
                            editor.putString("tipo",jo.getJSONObject(0).getString("tipoUsuario"));
                        if(recor.isChecked()){
                            editor.putBoolean("recor",true);
                        }
                        else{
                            editor.putBoolean("recor",false);
                        }
                        editor.commit();
                        if(sp.getString("tipo","").equals("Admin")){
                            Intent intencion=new Intent(getContext(),MenuDrawer.class);
                            startActivity(intencion);
                            getActivity().finish();
                        }
                    }
                    catch (JSONException e){
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getContext(),"Usuario o contrasenia incorrectos",Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros=new HashMap<>();
                parametros.put("nombre_usuario",usu.getText().toString());
                parametros.put("contrasenia_usuario",cont.getText().toString());
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
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        requestQueue.add(jsonRequest);
    }
    private void actualizarBase(String URL){
        dba=new DBAdapter(getContext());
        StringRequest jsonRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    JSONArray ja=null;
                    try{
                        ja=new JSONArray(response);
                        dba.abrir();
                        Cursor cursor=dba.getOrganismos();
                        JSONObject jo=null;
                        boolean insertado=false;
                        boolean actualizado=false;
                        for(int i=0;i<ja.length();i++){
                            boolean existe=false;
                            jo=ja.getJSONObject(i);
                            cursor.moveToFirst();
                            for(int j=0;j<cursor.getCount()&&!existe;j++){
                                if(cursor.getInt(0)==jo.getInt("id_organismo")){
                                    existe=true;
                                }
                                cursor.moveToNext();
                            }
                            if(!existe){
                                boolean estado_organismo=false;
                                float lat_punto;
                                float long_punto;
                                if(jo.getInt("estado_organismo")==1){
                                    estado_organismo=true;
                                }
                                else{
                                    estado_organismo=false;
                                }
                                try{
                                    lat_punto=Float.parseFloat(jo.getString("lat_punto"));
                                    long_punto=Float.parseFloat(jo.getString("long_punto"));
                                }
                                catch(NumberFormatException e){
                                    lat_punto=0;
                                    long_punto=0;
                                }
                                dba.insertarOrganismo(jo.getInt("id_organismo"),jo.getInt("codigo_organismo"),jo.getString("nombre_organismo"),
                                        jo.getString("telefono_organismo"),jo.getString("direccion_organismo"),jo.getString("mail_organismo"),estado_organismo,lat_punto,long_punto);
                                insertado=true;
                            }
                            else{
                                boolean estado_organismo=false;
                                float lat_punto;
                                float long_punto;
                                if(jo.getInt("estado_organismo")==1){
                                    estado_organismo=true;
                                }
                                else{
                                    estado_organismo=false;
                                }
                                try{
                                    lat_punto=Float.parseFloat(jo.getString("lat_punto"));
                                    long_punto=Float.parseFloat(jo.getString("long_punto"));
                                }
                                catch(NumberFormatException e){
                                    lat_punto=0;
                                    long_punto=0;
                                }
                                cursor.moveToPrevious();
                                if(jo.getInt("codigo_organismo")!=cursor.getInt(1)||
                                        !jo.getString("nombre_organismo").equals(cursor.getString(2))||
                                        !jo.getString("telefono_organismo").equals(cursor.getString(3))||
                                        !jo.getString("direccion_organismo").equals(cursor.getString(4))||
                                        !jo.getString("mail_organismo").equals(cursor.getString(5))||
                                        jo.getInt("estado_organismo")!=cursor.getInt(6)||
                                        lat_punto!=cursor.getFloat(7)||
                                        long_punto!=cursor.getFloat(8)){
                                    dba.actualizarOrganismo(jo.getInt("id_organismo"),jo.getInt("codigo_organismo"),jo.getString("nombre_organismo"),
                                            jo.getString("telefono_organismo"),jo.getString("direccion_organismo"),jo.getString("mail_organismo"),estado_organismo,lat_punto,long_punto);

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
                        dba.cerrar();
                    }catch (Exception e){
                        Toast.makeText(getContext(),e.toString(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        requestQueue.add(jsonRequest);
    }
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name="canal 1";
            String description="canal 1";
            int importance= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);
            NotificationManager nm=(NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(channel);
        }
    }
    private void mostrarNotificacion(String text){
        notificacion=new NotificationCompat.Builder(getContext(),CHANNEL_ID);
        notificacion.setSmallIcon(R.drawable.ic_business_center_black_24dp);
        notificacion.setTicker("Base de Datos Actualizada");
        notificacion.setContentTitle("Base de Datos Actualizada");
        notificacion.setContentText(text);
        notificacion.setDefaults(NotificationCompat.DEFAULT_SOUND);
        NotificationManagerCompat nm=NotificationManagerCompat.from(getContext());
        nm.notify(idUnica,notificacion.build());
    }

}
