package com.example.plantapermanente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class cambiar_contrasenia extends AppCompatActivity {
    EditText edtNueva;
    EditText edtRepetir;
    TextView errorNueva;
    TextView errorRepetir;
    Button btnActualizar;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasenia);
        edtNueva=(EditText)findViewById(R.id.edtNueva);
        edtRepetir=(EditText)findViewById(R.id.edtRepetir);
        errorNueva=(TextView)findViewById(R.id.errorNueva);
        errorRepetir=(TextView)findViewById(R.id.errorRepetir);
        btnActualizar=(Button)findViewById(R.id.btnCambiarCla);
        sp=getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        if(!sp.getString("tipo","").equals("anonimo")){
            btnActualizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verificar();
                }
            });
        }
        else{
            edtNueva.setEnabled(false);
            edtRepetir.setEnabled(false);
        }
    }
    private void verificar(){
        if(!edtNueva.getText().toString().isEmpty()){
            if(edtNueva.getText().toString().length()>30){
                errorNueva.setText("No debe exceder los 30 caracteres");
            }
            else{
                errorNueva.setText("");
            }
        }
        else{
            errorNueva.setText("Debe ingresar una contrasenia");
        }
        if(!edtRepetir.getText().toString().isEmpty()){
            if(edtRepetir.getText().toString().length()>30){
                errorRepetir.setText("No debe exceder los 30 caracteres");
            }
            else{
                errorRepetir.setText("");
            }
        }
        else{
            errorRepetir.setText("Debe repetir la contrasenia");
        }
        boolean coinciden=false;
        if(edtNueva.getText().toString().equals(edtRepetir.getText().toString())){
            coinciden=true;
        }
        if(errorRepetir.getText().toString().isEmpty()&&errorNueva.getText().toString().isEmpty()&&coinciden){
            actualizar(getResources().getString(R.string.host2)+"entity.usuario/modificar");
        }
        else{
            if(!coinciden){
                Toast.makeText(getApplicationContext(),"Las claves deben coincidir",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void actualizar(String URL){
        StringRequest sr=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("[]")){
                    Toast.makeText(getApplicationContext(),"Clave Actualizada",Toast.LENGTH_LONG).show();
                    sp=getApplicationContext().getSharedPreferences("Sesion",Context.MODE_PRIVATE);
                    editor=sp.edit();
                    editor.putString("contrasenia",edtNueva.getText().toString());
                    editor.commit();
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Clave No Actualizada",Toast.LENGTH_LONG).show();
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
                sp=getApplicationContext().getSharedPreferences("Sesion",Context.MODE_PRIVATE);
                parametros.put("nombre_usuario",sp.getString("usuario",""));
                parametros.put("contrasenia_usuario",edtRepetir.getText().toString());
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
        RequestQueue rq= Volley.newRequestQueue(getApplicationContext());
        rq.add(sr);
    }
}
