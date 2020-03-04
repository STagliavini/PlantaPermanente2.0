package com.example.plantapermanente.organismos;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.plantapermanente.R;

import java.util.ArrayList;

public class organismo_contacto extends AppCompatActivity {
    EditText nomContacto;
    EditText numContacto;
    String nomcontactoo,numcontactoo;
    Button btnAgregar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organismo_contacto);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras=getIntent().getExtras();
        nomcontactoo=extras.getString("org");
        numcontactoo=extras.getString("tel");
        nomContacto=findViewById(R.id.nomContacto);
        numContacto=findViewById(R.id.numContacto);
        btnAgregar=findViewById(R.id.btnagregarContacto);
        nomContacto.setText(nomcontactoo);
        numContacto.setText(numcontactoo);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(getBaseContext().checkSelfPermission(Manifest.permission.WRITE_CONTACTS)==
                            PackageManager.PERMISSION_DENIED){
                        String[] permissions={Manifest.permission.WRITE_CONTACTS};
                        requestPermissions(permissions,1001);
                    }
                    else{
                        insertarContacto();
                    }
                }
                else {
                    insertarContacto();
                }
            }
        });
    }
    private void insertarContacto(){
        String displayName=nomContacto.getText().toString();
        String number=numContacto.getText().toString();
        ArrayList<ContentProviderOperation> ops = new ArrayList <ContentProviderOperation> ();
        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        displayName).build());
        ops.add(ContentProviderOperation.
                newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(getBaseContext(), "CONTACTO INSERTADO CON EXITO!!!!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
