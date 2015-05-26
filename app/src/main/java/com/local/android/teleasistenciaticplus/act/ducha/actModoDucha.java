package com.local.android.teleasistenciaticplus.act.ducha;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.local.android.teleasistenciaticplus.R;
import com.local.android.teleasistenciaticplus.lib.helper.AppLog;
import com.local.android.teleasistenciaticplus.lib.sound.PlaySound;
import com.local.android.teleasistenciaticplus.modelo.Constants;

public class actModoDucha extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_modo_ducha);

        //TODO Comprobar antes de nada que haya contactos.
        // Si no hay mostrar diálogo para acceder a la zona de configuración.
        //Se queda pendiente hasta que Antonio incluya los diálogos

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_act_modo_ducha, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void activarDucha(View v){
        int tiempo_minutos = 0;

        switch (v.getId()) {
            case R.id.btn_ducha_15_minutos:
                AppLog.i("Modo Ducha --> ", "15 - " + v.getId());
                tiempo_minutos = 15;
                break;

            case R.id.btn_ducha_30_minutos:
                AppLog.i("Modo Ducha --> ", "30 - " + v.getId());
                tiempo_minutos = 30;
                break;

            case R.id.btn_ducha_45_minutos:
                AppLog.i("Modo Ducha --> ", "45 - " + v.getId());
                tiempo_minutos = 45;
                break;
        }

        if (Constants.PLAY_SOUNDS) {
            PlaySound.play(R.raw.modo_ducha_activado);
        }

        Intent intent = new Intent(this, actDuchaCuentaAtras.class);
        intent.putExtra("minutos",tiempo_minutos);

        startActivity(intent);

        if( Constants.SHOW_ANIMATION ) {

            overridePendingTransition(R.anim.animation2, R.anim.animation1);

        }
        finish();
    }
}
