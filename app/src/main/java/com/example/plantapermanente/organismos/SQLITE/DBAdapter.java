package com.example.plantapermanente.organismos.SQLITE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    static final String CLAVE_ID = "id_organismo";
    static final String CLAVE_CODIGO_ORGANISMO ="codigo_organismo";
    static final String CLAVE_NOMBRE_ORGANISMO ="nombre_organismo";
    static final String CLAVE_TELEFONO_ORGANISMO ="telefono_organismo";
    static final String CLAVE_DIRECCION_ORGANISMO ="direccion_organismo";
    static final String CLAVE_MAIL_ORGANISMO ="mail_organismo";
    static final String CLAVE_ESTADO_ORGANISMO ="estado_organismo";
    static final String CLAVE_LAT_ORGANISMO ="lat_organismo";
    static final String CLAVE_LONG_ORGANISMO ="long_organismo";
    static final String BASE_DE_DATOS = "Organismos";
    static final String TABLA = "organismo";
    static final int VERSION = 1;
    static final String CREAR_DB =
            "CREATE TABLE organismo (id_organismo INT PRIMARY KEY,"+"codigo_organismo INT UNIQUE,"+
            "nombre_organismo TEXT,"+"telefono_organismo TEXT,"+"direccion_organismo TEXT,"+"mail_organismo TEXT,"+"estado_organismo boolean,"+"lat_organismo float ,"+"long_organismo float);";

    final Context contexto;
    DataBaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context contexto){
        this.contexto = contexto;
        DBHelper = new DataBaseHelper(contexto);
    }

    private static class DataBaseHelper extends SQLiteOpenHelper {
        public DataBaseHelper(Context contexto){
            super(contexto, BASE_DE_DATOS, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            try{
                db.execSQL(CREAR_DB);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,int versionAnterior, int nuevaVersion){
            Log.w("DBAdapter", "Actualizando desde la version "+versionAnterior+
                    " a la version "+nuevaVersion+". Se eliminaran todos los datos");
            db.execSQL("DROP TABLE IF EXISTS organismo;");
            onCreate(db);
            db.execSQL("ALTER TABLE organismo ADD COLUMN bandera BLOB;");
        }

    }

    // Abre la Base de Datos
    public DBAdapter abrir() throws SQLException{
        db = DBHelper.getWritableDatabase();
        return this;//db?
    }
    // Cierra la Base de Datos
    public void cerrar(){
        DBHelper.close();
    }
    // Inserta un pais en la Base de Datos
    public long insertarOrganismo(int id_organismo,int codigo_organismo,String nombre_organismo,String telefono_organismo,String direccion_organismo,String mail_organismo,boolean estado_organismo,float lat_organismo,float long_organismo){
        ContentValues valores = new ContentValues();
        valores.put(CLAVE_ID,id_organismo);
        valores.put(CLAVE_CODIGO_ORGANISMO,codigo_organismo);
        valores.put(CLAVE_NOMBRE_ORGANISMO,nombre_organismo);
        valores.put(CLAVE_TELEFONO_ORGANISMO,telefono_organismo);
        valores.put(CLAVE_DIRECCION_ORGANISMO,direccion_organismo);
        valores.put(CLAVE_MAIL_ORGANISMO,mail_organismo);
        valores.put(CLAVE_ESTADO_ORGANISMO,estado_organismo);
        valores.put(CLAVE_LAT_ORGANISMO,lat_organismo);
        valores.put(CLAVE_LONG_ORGANISMO,long_organismo);
        return db.insert(TABLA, null, valores);
    }
    // Elimina un pais de la Base de Datos
    public boolean eliminarOrganismo(int id){
        return db.delete(TABLA, CLAVE_ID + " = "+id,null) > 0;
    }
    public boolean eliminarTodos(long id){
        return db.delete(TABLA, CLAVE_ID + " != 0",null) > 0;
    }
    // Recupera todos los paises de la Base de Datos
    public Cursor getOrganismos(){
        return db.query(TABLA, new String[] {CLAVE_ID,CLAVE_CODIGO_ORGANISMO,CLAVE_NOMBRE_ORGANISMO,CLAVE_TELEFONO_ORGANISMO,
                        CLAVE_DIRECCION_ORGANISMO,CLAVE_MAIL_ORGANISMO,CLAVE_ESTADO_ORGANISMO,CLAVE_LAT_ORGANISMO,CLAVE_LONG_ORGANISMO},
                null, null, null, null, null);
    }
    // Recupera un pais de la Base de Datos
    public Cursor getAutoridades(long id){
        Cursor cursor =
                db.query(true, TABLA, new String[] {CLAVE_ID, CLAVE_NOMBRE_ORGANISMO},
                CLAVE_ID+"=" + id, null, null, null, null, null);
        if (cursor !=null){
            cursor.moveToFirst();
        }
        return cursor;
    }
    // Actualiza un pais de la Base de Datos
    public boolean actualizarOrganismo(int id_organismo,int codigo_organismo,String nombre_organismo,String telefono_organismo,String direccion_organismo,String mail_organismo,boolean estado_organismo,float lat_organismo,float long_organismo){
        ContentValues valores = new ContentValues();
        valores.put(CLAVE_CODIGO_ORGANISMO,codigo_organismo);
        valores.put(CLAVE_NOMBRE_ORGANISMO,nombre_organismo);
        valores.put(CLAVE_TELEFONO_ORGANISMO,telefono_organismo);
        valores.put(CLAVE_DIRECCION_ORGANISMO,direccion_organismo);
        valores.put(CLAVE_MAIL_ORGANISMO,mail_organismo);
        valores.put(CLAVE_ESTADO_ORGANISMO,estado_organismo);
        valores.put(CLAVE_LAT_ORGANISMO,lat_organismo);
        valores.put(CLAVE_LONG_ORGANISMO,long_organismo);
        return db.update(TABLA, valores, CLAVE_ID+"="+id_organismo, null)>0;
    }
}
