package com.fundacionmagtel.android.teleasistenciaticplus.act.debug;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fundacionmagtel.android.teleasistenciaticplus.modelo.GlobalData;
import com.fundacionmagtel.android.teleasistenciaticplus.R;

/**
 * Actividad de depuracion del modo ducha
 * Created by GAMO1J on 09/04/2015.
 */
public class actDebugChronometer extends Activity {

    private CountDownTimer isTheFinalCountDown; //clase para la cuenta atrás

    //párametros para la clase
    private int futureTime;
    private int interval;

    //Token para evitar llamadas adicionales
    private boolean activedCountDown;
    //TODO: sustituir por deshabilitar el botón Start


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_debug_ducha);

        //El intervalo de refresco se estable a segundos
        interval = 1000;

        activedCountDown = false;

    }

    public void startCountDown(View v) {

        if (!activedCountDown) {

            activedCountDown = true;

            //Capturamos el tiempo (en minutos) introducido por el usuario
            final TextView mTextField = (TextView) findViewById(R.id.mTextField);

            EditText text = (EditText) findViewById(R.id.etMinutos);

            String mins = text.getEditableText().toString();

            futureTime = Integer.parseInt(mins) * 60000;

            //futureTime = 15000;

            isTheFinalCountDown = new CountDownTimer(futureTime, interval) {

                final Notification noti = new Notification.Builder(getApplicationContext())
                        .setSound(Uri.parse("android.resource://" + GlobalData.getAppContext().getPackageName() + "/" + R.raw.beep_07))
                        .build();
                //noti.sound = Uri.parse("android.resource://" + GlobalData.getAppContext().getPackageName() + "/" + R.raw.modo_ducha_activado)

                final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                @Override
                public void onTick(long millisUntilFinished) {

                    if (millisUntilFinished  < 60000) {
                        mTextField.setText("00:" + millisUntilFinished / 1000);

                        if (millisUntilFinished  < 10000) {
                            mTextField.setText("00:0" + millisUntilFinished / 1000);
                        }

                        notificationManager.notify(0, noti);


                    } else {

                        int minutos = (int) (millisUntilFinished / 60000);
                        int segundos = (int) ( ( (millisUntilFinished / 1000) - (minutos * 60)) );
                        if(millisUntilFinished > 600000) {
                            mTextField.setText("" + minutos + ":" + segundos);

                            if (segundos  < 10) {
                                mTextField.setText("" + minutos +":0" + segundos);
                            }


                        }else {
                            mTextField.setText("0" + minutos + ":" + segundos);
                            if (segundos  < 10) {
                                mTextField.setText("0" + minutos +":0" + segundos);
                            }
                        }

                    }

                }

                @Override
                public void onFinish() {
                    //TODO: launch SMS
                    mTextField.setText("Send SMS now");
                    activedCountDown = false;
                }
            }.start();
        }
    }

    public void cancelCountDown(View v) {

        activedCountDown = false;
        isTheFinalCountDown.cancel();

        TextView mTextField = (TextView) findViewById(R.id.mTextField);
        mTextField.setText("00:00");
    }

    /**
     * Salida de la aplicación al pulsar el botón de salida del layout
     *
     * @param view vista
     */
    public void exit_button(View view) {
        finish();
    }
}



 /*
        final Chronometer cronoDucha = (Chronometer) findViewById(R.id.chronometer);

        cronoDucha.setBase(System.currentTimeMillis());

        cronoDucha.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

            @Override
            public void onChronometerTick(Chronometer chronometer) {
                AppLog.i("actDebugChronometer","Base       : " + chronometer.getBase()  );
                AppLog.i("actDebugChronometer", "TimeMillis : " + System.currentTimeMillis());
                CharSequence text = chronometer.getText();
                if (text.length()  == 5) {
                    chronometer.setText("00:"+text);
                } else if (text.length() == 7) {
                    chronometer.setText("0"+text);
                }

            }
        });
        cronoDucha.setBase(SystemClock.elapsedRealtime());
        cronoDucha.start();
        */
