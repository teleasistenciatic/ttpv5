package com.fundacionmagtel.android.teleasistenciaticplus.act.ducha;

import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fundacionmagtel.android.teleasistenciaticplus.act.main.actMain;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppDialog;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.sms.SmsLauncher;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.sound.PlaySound;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.stats.StatsFileLogTextGenerator;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.Constants;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.GlobalData;
import com.fundacionmagtel.android.teleasistenciaticplus.modelo.TipoAviso;
import com.fundacionmagtel.android.teleasistenciaticplus.R;

import java.util.Date;

/**
 * Actividad que muestra y gestiona el modo ducha
 *
 * @author Jose Manuel Gálvez Moreno
 */

public class actDuchaCuentaAtras extends FragmentActivity implements AppDialog.AppDialogNeutralListener {

    private PowerManager.WakeLock wakeLock;
    private PowerManager mgr;

    //TAG para depuración
    private final String TAG = getClass().getSimpleName();

    private CountDownTimer TheCountDown; //clase para la cuenta atrás

    //párametros para la clase CountDownTimer
    private int futureTime; //tiempo total de la cuenta atrás
    private int interval;  //intervalo de refresco del minutero

    //Referencia al layout para modificar el color de fondo
    private RelativeLayout rl;
    private boolean changeBgColor;


    /**
     * Método de framework onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_act_ducha_cuenta_atras);

        rl = (RelativeLayout) findViewById(R.id.miRelativeLayout);
        changeBgColor = true;

        //Recuperamos del intent los minutos seleccionados por el usuario
        futureTime = (int) getIntent().getExtras().get("minutos");

        //El intervalo de refresco se estable a segundos
        interval = 1000;

        /////////////////////////////////////////////////////
        StatsFileLogTextGenerator.write("modo ducha", "iniciado con minutos : " + futureTime);
        /////////////////////////////////////////////////////

        //Iniciamos la cuenta atrás
        startCountDown();

    }

    /**
     * Al salir de la aplicación se detiene la cuenta atrás
     */
    @Override
    public void onStop() {
        super.onStop();
        //TheCountDown.cancel();

    }

    /**
     * Al destruirse la aplicación hay que liberar
     * el wakelock que hace que la aplicación no se
     * ponga en modo reposo
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }

    /**
     * Al pulsar el boton de back, se para la cuenta
     * del modo ducha. El modo ducha sólo tiene sentido
     * como aplicación en primer plano mientras realmente
     * se está duchando.
     */
    @Override
    public void onBackPressed() {
        TheCountDown.cancel();
        finish();
    }


    /**
     * Comenzar la cuenta atrás del modo ducha
     */
    void startCountDown() {

        /////////////////////////////////////////////////////////// WAKELOCK
        mgr = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();
        /////////////////////////////////////////////////////////////////////

        //Capturamos el tiempo (en minutos) introducido por el usuario
        final TextView mTextField = (TextView) findViewById(R.id.mTextField);

        futureTime = futureTime * 60000;

        //Generamos una notificación sonora
        final Notification beep_sound = new Notification.Builder(getApplicationContext())
                .setSound(Uri.parse("android.resource://" + GlobalData.getAppContext().getPackageName() + "/" + R.raw.beep_07))
                .build();

        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //futureTime = 65000;
        TheCountDown = new CountDownTimer(futureTime, interval) {

            Boolean alarmaDisparada = false;

            @Override
            public void onTick(long millisUntilFinished) {

                //menos de un minuto
                if (millisUntilFinished < 60000) {
                    mTextField.setText("00:" + millisUntilFinished / 1000);

                    if (millisUntilFinished < 10000) {
                        mTextField.setText("00:0" + millisUntilFinished / 1000);
                    }

                    notificationManager.notify(0, beep_sound);

                    rl.setBackgroundColor((changeBgColor) ? 0xffff0000 : 0xff33b5e5);
                    changeBgColor = !changeBgColor;

                } else {

                    int minutos = (int) (millisUntilFinished / 60000);
                    int segundos = (int) (((millisUntilFinished / 1000) - (minutos * 60)));
                    if (millisUntilFinished > 600000) {
                        mTextField.setText("" + minutos + ":" + segundos);

                        if (segundos < 10) {
                            mTextField.setText("" + minutos + ":0" + segundos);
                        }


                    } else {
                        mTextField.setText("0" + minutos + ":" + segundos);
                        if (segundos < 10) {
                            mTextField.setText("0" + minutos + ":0" + segundos);
                        }
                    }

                }

            }

            /**
             * Envio del SMS de aviso cuando no se ha detenido
             * el modo ducha.
             */
            @Override
            public void onFinish() {

                //enviamos el sms
                SmsLauncher miSmsLauncher = new SmsLauncher(TipoAviso.DUCHANOATENDIDA);
                Boolean hayListaContactos = miSmsLauncher.generateAndSend();

                if (hayListaContactos) {

                    //mostramos diálogo de sms enviado
                    AppDialog newFragment = AppDialog.newInstance(AppDialog.tipoDialogo.SIMPLE, 1,
                            "SMS ENVIADO",
                            "Se ha enviado un SMS a sus contactos por una alerta de ducha",
                            "Cerrar",
                            "sin_uso");
                    newFragment.show(getFragmentManager(), "dialog");

                    //pasamos la fecha de envío del SMS a la actividad principal actMAin
                    actMain.getInstance().actualizarUltimoSMSEnviado(new Date());
                } else {
                    /////////////////////////////////////////////////////
                    StatsFileLogTextGenerator.write("modo ducha", "error al enviar no hay contactos");
                    /////////////////////////////////////////////////////
                }


                if (Constants.SHOW_ANIMATION) {

                    overridePendingTransition(R.anim.animation2, R.anim.animation1);

                }

            }
        }.start();

    }

    /**
     * Cuenta atrás cancelada
     * @param v
     */
    public void cancelCountDown(View v) {

        TheCountDown.cancel();

        if (Constants.PLAY_SOUNDS) PlaySound.play(R.raw.modo_ducha_cancelado);

        finish();

    }

    /**
     * Necesario para mostrar un dialogo personalizado.
     * El dialogo externo llama a este método.
     * @param dialog
     */
    //Implementación del interfaz de diálogo
    public void onAccionNeutral(DialogFragment dialog) {

        finish();

    }

}
