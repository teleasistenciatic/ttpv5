package com.fundacionmagtel.android.teleasistenciaticplus.lib.sound;

import java.util.Locale;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

/**
 * Created by MORUGE on 15/05/2015.
 * @author German Moreno
 */
public class SintetizadorVoz implements OnInitListener
{
    private TextToSpeech sintetizador;
    private boolean listoParaHablar = false;
    private Context context;

    public SintetizadorVoz(Context baseContext)
    {
        this.context = baseContext;
        sintetizador = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status)
    {
        if (status == TextToSpeech.SUCCESS)
        {
            sintetizador.setLanguage(Locale.getDefault());
            listoParaHablar = true;
        }
        else
            installTTS();
    }

    private void installTTS()
    {
        Intent installIntent = new Intent();
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        context.startActivity(installIntent);
    }

    /**
     * Método que convierte a voz el texto recibido.
     * @param text Texto a ser convertido en voz.
     */
    public void hablaPorEsaBoquita(String text)
    {
        if (listoParaHablar)
            sintetizador.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        else
            Log.i("VOZ", "Tas quedao con las ganas de oirme hablar, pero aun no estaba listo");
    }

    public void finaliza()
    {
        sintetizador.stop();
        sintetizador.shutdown();
    }
}
