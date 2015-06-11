package com.fundacionmagtel.android.teleasistenciaticplus.lib.detectorCaidas;

/**
 * Interfaz que deben implementar todas las funciones de activación.
 * Se sigue el patrón estrategia.
 * @author SAMUAN
 */
public interface IFuncionActivacion {    

    /**
     * Ejecuta la función de activación. 
     * @param val vector de valores z
     * @return vector de valores a
     */
    double[] activar(double[] val);
}
