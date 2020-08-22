package com.example.plantapermanente.organismos;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.plantapermanente.R;
import com.example.plantapermanente.organismos.SQLITE.DBAdapter;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ViewListener;

import java.util.List;
import java.util.Map;

public class organismos_presentacion extends Fragment {
    private DBAdapter dba;
    ListView list;
    View view;
    Map<String,Object> itempas;
    private List<Map<String, Object>> organismos;
    SharedPreferences sp;
    CarouselView carouselView;
    int[] sampleImages = {R.drawable.educacion,R.drawable.turismo,R.drawable.municipalidad,R.drawable.desconocido,R.drawable.unse,R.drawable.desconocido,R.drawable.ministerio2};
    String[]textCarousel;
    int[]cods;
    FragmentTransaction ft;
    FragmentManager fm;
    ViewListener viewListener=new ViewListener() {
        @Override
        public View setViewForPosition(int position) {
            View customView = getLayoutInflater().inflate(R.layout.activity_custom_carousel, null);
            fm=getFragmentManager();
            ImageView imagen=customView.findViewById(R.id.imagen);
            TextView nombre=customView.findViewById(R.id.nombre_carousel_organismo);
            TextView codigo=customView.findViewById(R.id.codOrga);
            if(sampleImages.length>position){
                imagen.setImageResource(sampleImages[position]);
            }
            else{
                imagen.setImageResource(R.drawable.desconocido);
            }
            nombre.setText(textCarousel[position]);
            nombre.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            codigo.setText(String.valueOf(cods[position]));
            return customView;
        }
    };
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_organismos_presentacion, container, false);
        Object[] cadenas=llenarLista();
        cods=(int[])cadenas[0];
        textCarousel=(String[])cadenas[1];
        sp= getActivity().getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        carouselView = (CarouselView) view.findViewById(R.id.carouselView);
        carouselView.setPageCount(textCarousel.length);
        carouselView.setViewListener(viewListener);
        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Bundle extras=new Bundle();
                extras.putInt("codigoOrganismo",cods[position]);
                if(sampleImages.length>position){
                    extras.putInt("imagenOrganismo",sampleImages[position]);
                }
                else{
                    extras.putInt("imagenOrganismo",R.drawable.desconocido);
                }
                organismo_detalle od=new organismo_detalle();
                od.setArguments(extras);
                ft=fm.beginTransaction();
                ft.replace(R.id.nav_host_fragment,od);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return view;
    }
    private Object[] llenarLista(){
        String[]nombres;
        int[]cods;
        dba=new DBAdapter(this.getContext());
        dba.abrir();
        int cod=-1;
        Cursor cursor=dba.getFiltroOrganismos(cod,"");
        cursor.moveToFirst();
        nombres=new String[cursor.getCount()];
        cods=new int[cursor.getCount()];
        for(int i=0;i<cursor.getCount();i++){
            cods[i]=cursor.getInt(1);
            nombres[i]=cursor.getString(2);
            cursor.moveToNext();
        }
        Object[] cadenas=new Object[2];
        cadenas[0]=cods;
        cadenas[1]=nombres;
        dba.cerrar();
        return cadenas;
    }
}
