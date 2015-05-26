package com.local.android.teleasistenciaticplus.lib.networking;

import android.os.SystemClock;

import com.local.android.teleasistenciaticplus.modelo.Constants;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by FESEJU on 17/02/2015.
 * <p/>
 * Esta clase maneja distintas operaciones HTTP
 *
 * @input url contiene la ruta completa que intentará leer la clase
 * Por defecto toma el valor de las
 * text devolverá el texto contenido en el fichero leido o null
 */
public class HttpUrlTextRead implements Constants {

    private String url; //input
    private String text; //output

    /**
     * Constructor
     *
     * @param url url a leer
     */
    public HttpUrlTextRead(String url) {

        this.url = url;
        getUrlTextThread();

    }

    /**
     * Getter
     *
     * @return el texto que contiene la url leida o null
     */
    public String getText() {
        SystemClock.sleep(Constants.HTTP_OPERATION_DELAY);
        return this.text;
    }

    /**
     * El método que abre una conexión de lectura a la URL y guarda el contenido en text
     *
     * @return String con el texto leido o null
     */
    public String getUrlText() {
        URLConnection feedUrl;
        try {
            feedUrl = new URL(this.url).openConnection();
            InputStream is = feedUrl.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "");
            }
            is.close();

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * La operación necesita un thread y la llamada la realiza este metodo.
     * Puede ser necesario usar después un delay para que el valor de text
     * se propague realmente al text de esta clase.
     */
    public void getUrlTextThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUrlTextRead.this.text = getUrlText();
            }

        }).start();
    }
}