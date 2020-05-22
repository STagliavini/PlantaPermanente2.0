package com.example.plantapermanente.organismos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plantapermanente.R;
import com.example.plantapermanente.empleados.empleado_enviar_mail;
import com.example.plantapermanente.organismos.SQLITE.DBAdapter;

public class organismo_detalle extends Fragment {
    View view;
    private DBAdapter dba;
    Bundle extras;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_organismo_detalle, container, false);
        extras=getArguments();
        ImageView imagenOrgaDet=view.findViewById(R.id.imagenOrgaDetalle);
        final TextView nombreOrgaDet=view.findViewById(R.id.nombreOrgaDet);
        final TextView telOrgDet=view.findViewById(R.id.telefonoOrgDet);
        Button addContact=view.findViewById(R.id.btnContactDetalle);
        TextView dirOrgDet=view.findViewById(R.id.dirOrgDet);
        Button viewMap=view.findViewById(R.id.btnMapaDetalle);
        final TextView mailOrgDet=view.findViewById(R.id.mailOrgDet);
        Button mandarMailDet=view.findViewById(R.id.btnMailDet);
        imagenOrgaDet.setImageResource(extras.getInt("imagenOrganismo"));
        dba=new DBAdapter(this.getContext());
        dba.abrir();
        Cursor cursor=dba.getFiltroOrganismos(extras.getInt("codigoOrganismo"),"");
        cursor.moveToFirst();
        nombreOrgaDet.setText(cursor.getString(2));
        telOrgDet.setText("Telefono: "+cursor.getString(3));
        dirOrgDet.setText("Direccion: "+cursor.getString(4));
        mailOrgDet.setText("Mail: "+cursor.getString(5));
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tel=telOrgDet.getText().toString();
                String org=nombreOrgaDet.getText().toString();
                tel=tel.substring(10);
                Intent intencion=new Intent(getContext(), organismo_contacto.class);
                Bundle pam= new Bundle();
                pam.putString("tel",tel);
                pam.putString("org",org);
                intencion.putExtras(pam);
                startActivity(intencion);
            }
        });
        mandarMailDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail=mailOrgDet.getText().toString();
                mail=mail.substring(6);
                Intent intencion=new Intent(getContext(), empleado_enviar_mail.class);
                Bundle pam= new Bundle();
                pam.putString("mail",mail);
                intencion.putExtras(pam);
                startActivity(intencion);
            }
        });
        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codigo="Codigo: "+extras.getInt("codigoOrganismo");
                Intent intencion=new Intent(getContext(), puntos_organismos.class);
                Bundle pam= new Bundle();
                pam.putString("codigo",codigo);
                intencion.putExtras(pam);
                startActivity(intencion);
            }
        });
        return view;
    }
}
