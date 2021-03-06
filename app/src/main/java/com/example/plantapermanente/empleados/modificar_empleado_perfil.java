package com.example.plantapermanente.empleados;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.plantapermanente.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class modificar_empleado_perfil extends AppCompatActivity {
    EditText edtTelefono;
    EditText edtMail;
    EditText edtDireccion;
    EditText edtNacimiento;
    Button btnModificar;
    Button btnCancelar;
    TextView error_telefono;
    TextView error_mail;
    TextView error_direccion;
    TextView error_nacimiento;
    String nacimiento;
    DatePickerDialog dpd;
    Calendar c;
    int dia,mes,anio;
    FragmentTransaction ft;
    FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_empleado_perfil);
        edtTelefono=(EditText)findViewById(R.id.edtTelefono);
        edtMail=(EditText)findViewById(R.id.edtMail);
        edtDireccion=(EditText)findViewById(R.id.edtDireccion);
        edtNacimiento=(EditText)findViewById(R.id.edtNacimiento);
        btnModificar=(Button) findViewById(R.id.btnModificar);
        btnCancelar=(Button) findViewById(R.id.btnCancelar);
        error_telefono=(TextView)findViewById(R.id.errorTelefono);
        error_mail=(TextView)findViewById(R.id.errorMail);
        error_direccion=(TextView)findViewById(R.id.errorDireccion);
        error_nacimiento=(TextView)findViewById(R.id.errorNacimiento);
        traerDatos(getResources().getString(R.string.host2)+"entity.empleado/listado_filtrado");
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtTelefono.getText().toString().length()>20){
                    error_telefono.setText("No debe exceder los 20 caracteres");
                }
                else{
                    error_telefono.setText("");
                }
                if(edtMail.getText().toString().length()>30){
                    error_mail.setText("No debe exceder los 30 caracteres");
                }
                else{
                    error_mail.setText("");
                }
                if(edtDireccion.getText().toString().isEmpty()){
                    error_direccion.setText("Debe ingresar una direccion");
                }
                else{
                    if(edtDireccion.getText().toString().length()>100){
                        error_direccion.setText("No debe exceder los 100 caracteres");
                    }
                    else{
                        error_direccion.setText("");
                    }
                }
                if(edtNacimiento.getText().toString().isEmpty()){
                    error_nacimiento.setText("Debe ingresar una fecha de nacimiento");
                }
                else{
                    error_nacimiento.setText("");
                }
                if(error_telefono.getText().toString().isEmpty()&&error_mail.getText().toString().isEmpty()
                        &&error_direccion.getText().toString().isEmpty()&&error_nacimiento.getText().toString().isEmpty()){
                    actualizar(getResources().getString(R.string.host2)+"entity.empleado/modificar");
                }
            }
        });
        edtNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd=new DatePickerDialog(modificar_empleado_perfil.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        edtNacimiento.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                        nacimiento=year+"-"+(month+1)+"-"+dayOfMonth;
                    }
                },anio,mes-1,dia);
                dpd.show();
            }
        });
    }
    private void traerDatos(String URL){
        StringRequest sr=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray jo=new JSONArray(response);
                    edtTelefono.setText(jo.getJSONArray(0).getJSONObject(4).getString("telefonoEmpleado"));
                    edtMail.setText(jo.getJSONArray(0).getJSONObject(4).getString("mailEmpleado"));
                    edtDireccion.setText(jo.getJSONArray(0).getJSONObject(4).getString("direccionEmpleado"));
                    Date form=new Date(Long.parseLong(jo.getJSONArray(0).getJSONObject(4).getString("nacimientoEmpleado")));
                    Calendar c=Calendar.getInstance();
                    c.setTime(form);
                    dia=c.get(Calendar.DAY_OF_MONTH);
                    mes=c.get(Calendar.MONTH)+1;
                    anio=c.get(Calendar.YEAR);
                    edtNacimiento.setText(dia+"/"+mes+"/"+anio);
                    nacimiento=anio+"-"+mes+"-"+dia;
                }catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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
                parametros.put("dni_empleado",getIntent().getExtras().getString("dni",""));
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
    private void actualizar(String URL){
        StringRequest sr=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("[]")){
                    Toast.makeText(getApplicationContext(),"Empleado Actualizado",Toast.LENGTH_LONG).show();
                    finish();
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
                parametros.put("dni_empleado",getIntent().getStringExtra("dni"));
                parametros.put("telefono_empleado",edtTelefono.getText().toString());
                parametros.put("mail_empleado",edtMail.getText().toString());
                parametros.put("direccion_empleado",edtDireccion.getText().toString());
                parametros.put("nacimiento_empleado",nacimiento);
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
        RequestQueue rq= Volley.newRequestQueue(this.getApplicationContext());
        rq.add(sr);
    }
}
