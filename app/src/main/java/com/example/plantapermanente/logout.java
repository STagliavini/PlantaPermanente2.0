package com.example.plantapermanente;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class logout extends Fragment {
@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    Intent intencion=new Intent(this.getContext(),MenuDrawerAnonimo.class);
    startActivity(intencion);
        getActivity().finish();
    }

}
