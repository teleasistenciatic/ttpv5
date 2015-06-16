package com.fundacionmagtel.android.teleasistenciaticplus.lib.detectorCaidas;

/**
 * Representa a una capa de la red neuronal.
 * Está constituida por una matriz de pesos, un vector para el bias y la función de activación
 * que se tiene que aplicar.
 *
 * Created by SAMUAN on 12/05/2015.
 */
class Capa {
    private double[] vector_entrada; //entrada a la capa
    private double[] vector_parcial; //calculo de pesos y bias
    private double[] vector_salida; // salida de la capa

    private double[][] sinapsis; //relaciona la capa anterior con esta.
    private double[] bias;
    private IFuncionActivacion funcion; //funcion de activación que se utiliza en esta capa.

    /**
     * Constructor para cálculos.
     * @param sinapsis la matriz de pesos que relaciona la capa anterior con esta.
     * @param bias El bias de esta capa
     * @param funcion La función de activación que se ejecutará en cada neurona de esta capa.
     */
    public Capa(double[][] sinapsis, double[] bias, IFuncionActivacion funcion) {
        this.sinapsis = sinapsis;
        this.funcion = funcion;
        this.bias= bias;

        vector_parcial=new double[sinapsis.length];
        vector_salida=new double[sinapsis.length];
    }

    /**
     * Realiza el cálculo de pesos por entrada y aplica la función de activación.
     */
    public void calcular(){
        calculoSumatorioPesos();
        calculoActivacion();
    }

    /**
     * Realiza la  operación de sumatorio de pesos por valores.
     */
    private void calculoSumatorioPesos(){
        double suma=0;
        for(int i=0;i<sinapsis.length;i++){ //i es cada neurona de esta capa
            suma=0;
            for(int j=0;j<sinapsis[i].length;j++){ //j es el número de entradas que llegan a la neurona.
                suma += vector_entrada[j]*sinapsis[i][j];
            }
            suma=suma+bias[i];
            vector_parcial[i]=suma;
        }
    }

    /**
     * Realiza la operación de la función de activación.
     * Se le pasa el vector con los valores z y me devuelve el vector con los valores a.
     */
    private void calculoActivacion(){
        vector_salida=funcion.activar(vector_parcial);
    }



    /* ***********   GETTER AND SETTER ****************** */

    /**
     * Setter del vector de entrada a la capa.
     * @param vector_entrada
     */
    public void setVector_entrada(double[] vector_entrada) {
        this.vector_entrada = vector_entrada;
    }

    /**
     * Getter del vector de entrada a la capa.
     * @return
     */
    public double[] getVector_salida() {
        return vector_salida;
    }

}
