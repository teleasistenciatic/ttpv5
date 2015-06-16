package com.fundacionmagtel.android.teleasistenciaticplus.lib.detectorCaidas;

/**
 *
 * Clase para guardar datos de cada muestra.
 * Almacena el momento en que se capturan los datos y
 * el módulo del vector aceleración.
 *
 * Created by SAMUAN on 01/04/2015.
 */
class Muestra {

    private long tiempo;
    private double aceleracion;

    /**
     * Crea una muestra con tiempo de captura y valor de la aceleración.
     *
     * @param tiempo momento en el que se captura el dato.
     * @param aceleracion módulo del vector aceleración
     */
    public Muestra(long tiempo, double aceleracion) {
        this.tiempo = tiempo;
        this.aceleracion = aceleracion;
    }

    /**
     * Getter del momento de captura.
     * @return
     */
    public long getTiempo() {
        return tiempo;
    }

    /**
     * Setter del momento de captura.
     * @param tiempo
     */
    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
    }

    /**
     * Getter del valor de la aceleración.
     * @return
     */
    public double getAceleracion() {
        return aceleracion;
    }

    /**
     * Setter del valor de la aceleración.
     * @param aceleracion
     */
    public void setAceleracion(double aceleracion) {
        this.aceleracion = aceleracion;
    }
}
