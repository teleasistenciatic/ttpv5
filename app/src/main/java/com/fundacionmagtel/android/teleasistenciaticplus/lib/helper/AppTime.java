package com.fundacionmagtel.android.teleasistenciaticplus.lib.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by FESEJU on 25/03/2015.
 * Devuelve la fecha en el formato seleccionado
 */
public class AppTime {

    String dateFormat = "dd/MM/yyyy : HH:mm:ss";

    public AppTime(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public AppTime() {
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTimeDate() {
        SimpleDateFormat fechaHora = new SimpleDateFormat(dateFormat);
        return fechaHora.format(new Date());
    }

}