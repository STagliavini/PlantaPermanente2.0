package com.example.plantapermanente;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class cambiar_contrasenia extends Fragment {
    View view;
    EditText edtNueva;
    EditText edtRepetir;
    TextView errorNueva;
    TextView errorRepetir;
    Button btnActualizar;
@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_cambiar_contrasenia, container, false);
        return view;
    }
}
