package com.fundacionmagtel.android.teleasistenciaticplus.presentador;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.sound.PlaySound;


public class actStaff extends Activity {

    TextView creditos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_view);

        creditos = (TextView) findViewById(R.id.article);
        creditos.setText("Director de Orquesta \n" +
                        "Antonio 'nuestro amado líder' Palomino \n\n" +
                        "EQUIPO SOCIAL \n" +
                        "Marisol Mejías\n" +
                        "Marimar Cabello\n\n" +
                        "EQUIPO FORMADOR \n" +
                        "Fernando Gómez \n" +
                        "Mabel Andrada \n\n" +
                        "EQUIPO TÉCNICO \n" +
                        "Juan José 'aún creen en el amor' Ferres \n" +
                        "José Manuel 'Scrum Master' Gálvez \n" +
                        "Antonio 'Maestro Sensei' Salvador \n" +
                        "Germán 'es la primera vez que juego al billar' Moreno \n\n" +
                        "OTROS \n" +
                        "David 'BrownDispatcher' Díaz \n\n\n\n" +
                        "En memoria del Programa Emplea30+ \n" +
                        "y con todo nuestro cariño a Susana 'mi gordi' Díaz."
        );

        Toast.makeText(getApplicationContext(), "¡¡¡ 30 de junio de 2015 !!!", Toast.LENGTH_SHORT).show();
        PlaySound.play(R.raw.error_aviso_no_enviado);
    }
}
