package com.fundacionmagtel.android.teleasistenciaticplus.lib.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Clase diálogo genérica para poder reutilizarla en distintas actividades.
 *
 * Created by SAMUAN on 15/04/2015.
 * @author Antonio Salvador
 */
public class AppDialog extends DialogFragment {

    /**
     * LLamada para crear el dialogo de manera estatica con los valores de texto y configuración apropiados.
     *
     * @param tipo Indica si el dialogo muestra 1 botón o 2 botones.
     * @param iden Es el identificador del dialogo y permite generar varios dialogos en el mismo activity,
     *             pues se puede variar el comportamiento dependiendo de este indicador.
     * @param titulo El título que debe mostrar el dialogo
     * @param mensaje El mensaje que debe mostrar el dialogo
     * @param textoboton El texto del boton Aceptar, o del botón único.
     * @param textobotonB El texto del botón Cancelar.
     * @return
     */
    public static AppDialog newInstance(tipoDialogo tipo,int iden, String titulo, String mensaje, String textoboton,String textobotonB){

        AppDialog dialogo=new AppDialog();
        Bundle args=new Bundle();

        args.putString("tipo",tipo.name());
        args.putInt("identificador",iden);
        args.putString("titulo",titulo);
        args.putString("mensaje",mensaje);
        args.putString("textoboton",textoboton);
        args.putString("textobotonB",textobotonB);
        dialogo.setArguments(args);

        return dialogo;

    }

    /**
     * Crea el diálogo.
     * Necesita establecer las cadenas de texto sino el dialogo aparece vacio.
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String tipo=getArguments().getString("tipo");
        String titulo=getArguments().getString("titulo");
        String mensaje=getArguments().getString("mensaje");
        String textoBoton=getArguments().getString("textoboton");
        String textoBotonB=getArguments().getString("textobotonB");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if ( tipo.equals(tipoDialogo.SIMPLE.name()) )  {
            builder .setTitle(titulo)
                    .setMessage(mensaje)
                    .setNeutralButton(textoBoton,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AppLog.i("APPDIALOG","pulsado");
                            //envio evento a la activity
                            listenerNeutral.onAccionNeutral(AppDialog.this);
                        }
                    });
        }else{
            builder .setTitle(titulo)
                    .setMessage(mensaje)
                    .setPositiveButton(textoBoton,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listenerDoble.onAccionAceptar(AppDialog.this);
                        }
                    })
                    .setNegativeButton(textoBotonB,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listenerDoble.onAccionCancelar(AppDialog.this);
                        }
                    });
        }

        return builder.create();
    }


    /**** Código para listener en activity ******/

    public enum tipoDialogo {SIMPLE,DOBLE}

    /**
     * Interface del diálogo
     */
    public interface AppDialogDobleListener{

        public void onAccionAceptar(DialogFragment dialog);
        public void onAccionCancelar(DialogFragment dialog);

    }

    /**
     * Metodo a implementar para recibir event callbacks
     */
    public interface AppDialogNeutralListener {

        public void onAccionNeutral(DialogFragment dialog);

    }

    AppDialogDobleListener listenerDoble; //referencia a activity
    AppDialogNeutralListener listenerNeutral;

    @Override
    /**
     * Override the Fragment.onAttach() method to instantiate the AppDialogListener
     */
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {

            String eltipo=this.getArguments().getString("tipo");
            if(eltipo.equals(tipoDialogo.SIMPLE.name())){
                listenerNeutral=(AppDialogNeutralListener) activity;
            }else{
                listenerDoble=(AppDialogDobleListener) activity;
            }
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AppDialogListener");
        }
    }
}

/** Ejemplo de uso **/

/*      1. Interfaz
        public class actMain extends FragmentActivity implements AppDialog.AppDialogDobleListener, AppDialog.AppDialogNeutralListener { */

/*      2. Creación objeto
        AppDialog newFragment = AppDialog.newInstance(AppDialog.tipoDialogo.SIMPLE,1,"titulo","mensaje","botosn","boton2");
        newFragment.show(getFragmentManager(),"dialog");
*/