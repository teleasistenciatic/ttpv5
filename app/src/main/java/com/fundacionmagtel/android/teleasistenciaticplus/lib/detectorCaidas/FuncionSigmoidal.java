package com.fundacionmagtel.android.teleasistenciaticplus.lib.detectorCaidas;

/**
 * Ejecuta la función sigmoidal como función de activación. 
 * @author SAMUAN
 */
public class FuncionSigmoidal implements IFuncionActivacion{

    /**
     * Implementación de la función sigmoidal como función de activación.
     * @param val vector de valores z
     * @return
     */
    @Override
    public double[] activar(double[] val) {
        double[] respuesta=new double[val.length];
        for(int i=0;i<val.length;i++){
            respuesta[i]= 1/(1+Math.exp(-val[i]));
        }
        return respuesta;
    }
    
}
