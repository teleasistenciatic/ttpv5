package com.fundacionmagtel.android.teleasistenciaticplus.act.ducha;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.sound.PlaySound;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;


/**A ctividad que permite seleccionar el tiempo de ducha.
 * Implementa un diálogo con un numberPicker para la opción de tiempo personalizado
 *
 * @author Jose Manuel Gálvez Moreno
 */
public class actModoDucha extends FragmentActivity implements duchaDialog.duchaDialogNeutralListener {

    //Variable que almacena el tiempo elegido por el usuario desde el diálogo.
    private static int MinutosPersonalizado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_modo_ducha);
        MinutosPersonalizado = 60;
    }

    /** Selector del tiempo de ducha predeterminado
     *
     * @param v
     */
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
        intent.putExtra("minutos", tiempo_minutos);

        startActivity(intent);

        if( Constants.SHOW_ANIMATION ) {

            overridePendingTransition(R.anim.animation2, R.anim.animation1);

        }
        finish();
    }

    /**
     * Método asociado al botón de tiempo personalizado.
     * Se encarga de mostrar el cuadro de diálogo con el numberPicker
     * @param v
     */
    public void elegirMinutos(View v){
        duchaDialog newFragment = duchaDialog.newInstance(1,"Aceptar");
        newFragment.show(getFragmentManager(), "dialog");

    }

    /**Implementación del interfaz de diálogo
     *
     * @param dialog
     * @param mins
     */
    public void onAccionNeutral(DialogFragment dialog, int mins){


        AppLog.i("DUCHA DIALOG", "Recibido: " + mins);


        Intent intent = new Intent(this, actDuchaCuentaAtras.class);
        intent.putExtra("minutos", mins);

        startActivity(intent);

        if( Constants.SHOW_ANIMATION ) {

            overridePendingTransition(R.anim.animation2, R.anim.animation1);

        }
        finish();

    }
}
