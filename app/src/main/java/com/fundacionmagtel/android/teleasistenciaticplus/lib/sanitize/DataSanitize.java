package com.fundacionmagtel.android.teleasistenciaticplus.lib.sanitize;

/**
 * Created by FESEJU on 21/04/2015.
 * Clase
 * @author Juan Jose Ferres
 */
public class DataSanitize {

    /**
     * Función que elimina los caracteres propios de la lengua española
     *
     * @param input
     * @return
     */
    public String cambiaCaracteresEspanolesPorIngleses(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii =    "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;

        for (int i = 0; i < original.length(); i++) {
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i

        return output;
    }

    /** Devuelve los primeros caracteres de una cadena dado un valor
     *  Se utiliza para limitar cadenas al tener un límite de 160
     *  caracteres por SMS
     * **/

    public String trimStringSize(String cadena, int max) {
        if ( cadena.length() > max) {
            return cadena.substring(0,max);
        } else {
            return cadena;
        }
    }
}
