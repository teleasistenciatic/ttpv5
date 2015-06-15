package com.fundacionmagtel.android.teleasistenciaticplus.lib.sound;

/**
 * Created by GAMO1J on 01/04/2015.
 */

import android.content.Context;
import android.media.MediaPlayer;

import com.fundacionmagtel.android.teleasistenciaticplus.modelo.GlobalData;

/**
 * Reproduce un sonido a través del método estático play, el cual recibe como
 * parámetro el id del recurso a reproducir
 * Ejemplo de uso desde otros métodos:
 *   PlaySound.play(R.raw.bienvenido);
 *   @author Jose Manuel Galvez
 */
public class PlaySound {
    /**
     * Constructor
     */
    public PlaySound() {}

    /**
     * Método estático que reproduce el sonido
     *
     * @param sound: id del recurso (sonido) a reproducir
     *
     * Ejemplo de uso desde otros métodos:
     *   PlaySound.play(R.raw.bienvenido);
     */
    static public void play(int sound){

        MediaPlayer mMediaPlayer;
        Context appContext = GlobalData.getAppContext();

        mMediaPlayer = MediaPlayer.create(appContext,sound);

        mMediaPlayer.start();

        //Cuando finaliza la reproducción del sonido liberamos el recurso y memoria usada
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

    }

}
