package com.fundacionmagtel.android.teleasistenciaticplus.lib.detectorCaidas;

import java.util.LinkedList;

/**
 * Clase para construir la red neuronal completa. Esta compuesta por una lista que contiene en su interior
 * a las distintas capas de las que consta la red.
 * Se trata de un perceptrón multicapa.
 *
 * Esta red está construida sólo para realizar el cálculo de clasificación, pero no el aprendizaje.
 *
 * @author SAMUAN
 */
class Red {

    private LinkedList<Capa> lista=new LinkedList<>() ; //lista con las distintas capas de la red.
    private double[] vectorEn; //vector de entrada de datos que se quiere clasificar.

    /**
     * Realiza el cálculo.
     * Se pasa el vector de entrada a la primera capa de la red y la respuesta
     * se pasa a la siguiente capa de la red hasta terminar la red completa.
     */
    public void calcular() {
        for(Capa capa:lista ){
            capa.setVector_entrada(vectorEn);
            capa.calcular();
            vectorEn=capa.getVector_salida();
        }
    }

    /**
     * Método para construir la red neuronal.
     * Se añaden capas a la lista. Se deben añadir en la dirección
     * desde la entrada hasta la salida.
     *
     * @param sinapsis matriz de pesos que llegan a las neuronas de esta capa
     * @param bias vector con el valor del bias para cada neurona de esta capa
     * @param funcion funcion de activación de las neuronas de esta capa
     */
    public void agregarCapa(double[][] sinapsis, double[] bias, IFuncionActivacion funcion){
        Capa capa=new Capa(sinapsis,bias, funcion);
        lista.add( capa );
    }

    /**
     * Setter del vector de datos para clasificar.
     * @param vectorEn
     */
    public void setVectorEn(double[] vectorEn) {
        this.vectorEn = vectorEn;
    }

    /**
     * Getter del vector de datos para clasificar.
     * @return
     */
    public double[] getVectorEn() {
        return vectorEn;
    }
}


    