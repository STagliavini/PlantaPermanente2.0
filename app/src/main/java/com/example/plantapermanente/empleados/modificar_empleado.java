package com.example.plantapermanente.empleados;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.plantapermanente.MenuDrawer;
import com.example.plantapermanente.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class modificar_empleado extends Fragment {
    View view;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_modificar_empleado, container, false);
        edtTelefono=(EditText)view.findViewById(R.id.edtTelefono);
    edtMail=(EditText)view.findViewById(R.id.edtMail);
    edtDireccion=(EditText)view.findViewById(R.id.edtDireccion);
    edtNacimiento=(EditText)view.findViewById(R.id.edtNacimiento);
    btnModificar=(Button) view.findViewById(R.id.btnModificar);
    btnCancelar=(Button) view.findViewById(R.id.btnCancelar);
    error_telefono=(TextView)view.findViewById(R.id.errorTelefono);
    error_mail=(TextView)view.findViewById(R.id.errorMail);
    error_direccion=(TextView)view.findViewById(R.id.errorDireccion);
    error_nacimiento=(TextView)view.findViewById(R.id.errorNacimiento);
    fm=getFragmentManager();
    traerDatos("https://tagliavinilab6.000webhostapp.com/traerEmpleado.php");
    btnCancelar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ft=fm.beginTransaction();
            ft.replace(R.id.nav_host_fragment,new empleados());
            ft.addToBackStack(null);
            ft.commit();
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
                actualizar("https://tagliavinilab6.000webhostapp.com/actualizarEmpleado.php");
                empleados emp=new empleados();
                ft=fm.beginTransaction();
                ft.replace(R.id.nav_host_fragment,emp);
                ft.addToBackStack(null);
                ft.commit();
            }
        }
    });
    edtNacimiento.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dpd=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    edtNacimiento.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    nacimiento=year+"-"+(month+1)+"-"+dayOfMonth;
                }
            },anio,mes-1,dia);
            dpd.show();
        }
    });
        return view;
}
private void traerDatos(String URL){
    StringRequest sr=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try{
                JSONObject jo=new JSONObject(response);
                edtTelefono.setText(jo.getString("telefono_empleado"));
                edtMail.setText(jo.getString("mail_empleado"));
                edtDireccion.setText(jo.getString("direccion_empleado"));
                SimpleDateFormat df4=new SimpleDateFormat("yyyy-MM-dd");
                Date form=df4.parse(jo.getString("nacimiento_empleado"));
                Calendar c=Calendar.getInstance();
                c.setTime(form);
                dia=c.get(Calendar.DAY_OF_MONTH);
                mes=c.get(Calendar.MONTH)+1;
                anio=c.get(Calendar.YEAR);
                edtNacimiento.setText(dia+"/"+mes+"/"+anio);
                nacimiento=anio+"-"+mes+"-"+dia;
            }catch (JSONException e){
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }catch (ParseException e){
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
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
            parametros.put("dni_empleado",getArguments().getString("dni",""));
            return parametros;
        }
    };
    RequestQueue rq= Volley.newRequestQueue(this.getContext());
    rq.add(sr);
}
private void actualizar(String URL){
    StringRequest sr=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            if(response.contains("Actualizado")){
                Toast.makeText(getContext(),"Empleado Actualizado",Toast.LENGTH_LONG).show();
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
            parametros.put("dni_empleado",getArguments().getString("dni",""));
            parametros.put("telefono_empleado",edtTelefono.getText().toString());
            parametros.put("mail_empleado",edtMail.getText().toString());
            parametros.put("direccion_empleado",edtDireccion.getText().toString());
            parametros.put("nacimiento_empleado",nacimiento);
            return parametros;
        }
    };
    RequestQueue rq= Volley.newRequestQueue(this.getContext());
    rq.add(sr);
}

}
