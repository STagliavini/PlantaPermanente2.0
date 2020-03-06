package com.example.plantapermanente;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class cambiar_contrasenia extends Fragment {
    View view;
    EditText edtNueva;
    EditText edtRepetir;
    TextView errorNueva;
    TextView errorRepetir;
    Button btnActualizar;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_cambiar_contrasenia, container, false);
        edtNueva=(EditText)view.findViewById(R.id.edtNueva);
        edtRepetir=(EditText)view.findViewById(R.id.edtRepetir);
        errorNueva=(TextView)view.findViewById(R.id.errorNueva);
        errorRepetir=(TextView)view.findViewById(R.id.errorRepetir);
        btnActualizar=(Button)view.findViewById(R.id.btnCambiarCla);
        sp=getActivity().getSharedPreferences("Sesion",Context.MODE_PRIVATE);

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
        return view;
    }
    private void verificar(){
        if(edtNueva.getText().toString().length()>30){
            errorNueva.setText("No debe exceder los 30 caracteres");
        }
        else{
            errorNueva.setText("");
        }
        if(edtRepetir.getText().toString().length()>30){
            errorRepetir.setText("No debe exceder los 30 caracteres");
        }
        else{
            errorRepetir.setText("");
        }
        boolean coinciden=false;
        if(edtNueva.getText().toString().equals(edtRepetir.getText().toString())){
            coinciden=true;
        }
        if(errorRepetir.getText().toString().isEmpty()&&errorNueva.getText().toString().isEmpty()&&coinciden){
            actualizar(getResources().getString(R.string.host)+"actualizarContrasenia.php");
        }
        else{
            if(!coinciden){
                Toast.makeText(getContext(),"Las claves deben coincidir",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void actualizar(String URL){
        StringRequest sr=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("Actualizado")){
                    Toast.makeText(getContext(),"Clave Actualizada",Toast.LENGTH_LONG).show();
                    sp=getActivity().getSharedPreferences("Sesion",Context.MODE_PRIVATE);
                    editor=sp.edit();
                    editor.putString("contrasenia",edtNueva.getText().toString());
                    editor.commit();
                }
                else{
                    Toast.makeText(getContext(),"Clave No Actualizada",Toast.LENGTH_LONG).show();
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
                sp=getActivity().getSharedPreferences("Sesion",Context.MODE_PRIVATE);
                parametros.put("nombre_usuario",sp.getString("usuario",""));
                parametros.put("contrasenia_usuario",edtRepetir.getText().toString());
                return parametros;
            }
        };
        RequestQueue rq= Volley.newRequestQueue(getContext());
        rq.add(sr);
    }
}
