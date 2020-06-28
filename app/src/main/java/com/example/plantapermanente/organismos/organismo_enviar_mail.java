package com.example.plantapermanente.organismos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.plantapermanente.R;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class organismo_enviar_mail extends AppCompatActivity {
    EditText correoET;
    EditText contET;
    EditText asuntoET;
    EditText mensajeET;
    Button enviar;
    String correo,cont,asunto,mensaje,correoHacia;
    Session session=null;
    ProgressDialog pdialog = null;
    Context context = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organismo_enviar_mail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bd=getIntent().getExtras();
        context=this;
        correoHacia=bd.getString("mailorg");
        //correoET=findViewById(R.id.mCorreo);
        //contET=findViewById(R.id.mCont);
        asuntoET=findViewById(R.id.mAsunto);
        mensajeET=findViewById(R.id.mMensaje);
        enviar=findViewById(R.id.btnmEnviar);
        Toast.makeText(this,"El mail sera enviado a: "+correoHacia,Toast.LENGTH_LONG).show();
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarEmail();
            }
        });
    }
    public void enviarEmail() {
        pdialog = ProgressDialog.show(context, "", "Abriendo aplicacion de email...",true);

        //Policy configurations
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        asunto = asuntoET.getText().toString();
        //correo = correoET.getText().toString();
        mensaje = mensajeET.getText().toString();
        //cont = contET.getText().toString();
        Intent email=new Intent(Intent.ACTION_SEND);
        email.setData(Uri.parse("mailto:"));
        email.setType("text/plain");
        email.putExtra(Intent.EXTRA_EMAIL,new String[]{correoHacia});
        email.putExtra(Intent.EXTRA_SUBJECT,asunto);
        email.putExtra(Intent.EXTRA_TEXT,mensaje);
        startActivity(Intent.createChooser(email,"Seleccionar Aplicacion"));
        //Connection to GMail's SMTP server
        /*Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.starttls.enable", "true") ;
        prop.put("mail.smtp.auth", "true") ;

        try {
            session = Session.getDefaultInstance(prop,
                    new javax.mail.Authenticator() {
                        //Authenticating the password
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(correo, cont);
                        }
                    });

            if(session != null){
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(correo));
                message.setSubject(asunto);
                //Destination mail
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoHacia));
                message.setContent(mensaje, "text/html; charset= utf-8");
                Transport.send(message);
                Toast.makeText(context,"¡El correo ha sido enviado a: "+correoHacia+"!",Toast.LENGTH_LONG).show();
            }


        } catch (Exception e) {
            Toast.makeText(context,"¡Hubo un fallo en el envio. Revise los datos ingresados!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }*/
        pdialog.hide();
    }
}
