package com.example.plantapermanente.organismos.SQLITE;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String BASE_DE_DATOS = "Organismos";
    private static final int VERSION = 1;

    public DataBaseHelper(Context contexto){
        super(contexto, BASE_DE_DATOS, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE organismo (id_organismo INT PRIMARY KEY,"+"codigo_organismo INT UNIQUE,"+
                "nombre_organismo TEXT,"+"telefono_organismo TEXT,"+"direccion_organismo TEXT,"+"mail_organismo TEXT,"+"estado_organismo boolean,"+"lat_organismo float ,"+"long_organismo float);");
    }
    public void insertIni(String nombre,SQLiteDatabase db){
        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        long resultadoInsert = db.insert("autoridad", null, valores);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int versionAnterior, int nuevaVersion){
        db.execSQL("ALTER TABLE autoridad ADD COLUMN bandera BLOB);");
    }
}
