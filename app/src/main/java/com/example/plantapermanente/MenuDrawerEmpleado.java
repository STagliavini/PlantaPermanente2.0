package com.example.plantapermanente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

public class MenuDrawerEmpleado extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_drawer_empleado);
        sp= getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_organismos,R.id.nav_recibos)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menuempleado, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_logout:{
                Intent intencion=new Intent(getApplicationContext(),MenuDrawerAnonimo.class);
                startActivity(intencion);
                finish();
            }
            break;
            case R.id.action_change_pass:{
                Intent intencion=new Intent(getApplicationContext(),cambiar_contrasenia.class);
                startActivity(intencion);
            }
            break;
            case R.id.action_modifi_perf:{
                Intent intencion=new Intent(getApplicationContext(),com.example.plantapermanente.empleados.modificar_empleado_perfil.class);
                intencion.putExtra("dni",sp.getString("usuario",""));
                startActivity(intencion);
            }
            break;
            default:{
                return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
