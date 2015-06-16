package com.fundacionmagtel.android.teleasistenciaticplus.lib.detectorCaidas;

/**
 * Clase para normalizar los datos de las caracteristicas sacadas del acelerómetro.
 *
 * @author SAMUAN
 */
class Normalizador {


    private static double[] media= new double[0];
    private static double[] desviacion= new double[0];

    /**
     * Devuelve un valor normalizado a partir del valor de entrada.
     * @param resul vector de características a normalizar.
     * @return vector de características normalizado.
     */
    public double[] normaliza(double[] resul) {
        double[] valor=new double[resul.length];
        for(int carac=0;carac<valor.length;carac++){
            valor[carac] = (resul[carac]-media[carac])/desviacion[carac];
        }
        return valor;      
    }

    /* ************ GETTER AND SETTER ***************** */

    /**
     * Getter el vector de valores medios.
     * @return
     */
    public static double[] getMedia() {
        return media;
    }

    /**
     * Setter del vector de valores medios.
     * @param media
     */
    public static void setMedia(double[] media) {
        Normalizador.media = media;
    }

    /**
     * Getter del vector de desviaciones típicas.
     * @return
     */
    public static double[] getDesviacion() {
        return desviacion;
    }

    /**
     * Setter del vector de desviaciones típicas.
     * @param desviacion
     */
    public static void setDesviacion(double[] desviacion) {
        Normalizador.desviacion = desviacion;
    }  
   
}
