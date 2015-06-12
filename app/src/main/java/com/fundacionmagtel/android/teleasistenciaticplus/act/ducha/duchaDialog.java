package com.fundacionmagtel.android.teleasistenciaticplus.act.ducha;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.fundacionmagtel.android.teleasistenciaticplus.R;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppLog;
import com.fundacionmagtel.android.teleasistenciaticplus.lib.helper.AppSharedPreferences;

/**
 * Created by GAMO1J on 01/06/2015.
 */

/**
 * Fragment que permite mostrar un numberPicker dentro de el
 * Requiere un metodo en la actividad que lo llama (onAccionNeutral)
 */
public class duchaDialog extends DialogFragment {

    NumberPicker npMinutos;
    public int minutos=60;

    public static duchaDialog newInstance(int iden, String textoboton){

        duchaDialog dialogo=new duchaDialog();

        Bundle args=new Bundle();

        args.putInt("identificador", iden);
        args.putString("textoboton",textoboton);

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



        String textoBoton=getArguments().getString("textoboton");
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.layout_modo_ducha_dialog, null);
        npMinutos = (NumberPicker) view.findViewById(R.id.np_minutos);
        npMinutos.setMinValue(5);
        npMinutos.setMaxValue(90);

        //Comprobamos si hay un valor guardado en sharedpreferences
        final AppSharedPreferences duchaPreferences= new AppSharedPreferences();
        if(duchaPreferences.hasPreferenceData("tiempoDucha")){
            minutos = Integer.parseInt(duchaPreferences.getPreferenceData("tiempoDucha"));
        }
        npMinutos.setValue(minutos);
        npMinutos.setWrapSelectorWheel(false);


        npMinutos.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                AppLog.i("PICKER", "M valores " + oldVal + " " + newVal);
                minutos = newVal;
                //duchaPreferences.setPreferenceData("tiempoDucha", String.valueOf(minutos));
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)

                .setNeutralButton(textoBoton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //guardamos el valor seleccionado
                        duchaPreferences.setPreferenceData("tiempoDucha", String.valueOf(minutos));

                        //envio evento a la activity
                        listenerNeutral.onAccionNeutral(duchaDialog.this, minutos);

                    }

                });


        return builder.create();
    }


    /**** Código para listener en activity ******/


    /**
     * Listener para llamar un método en otra actividad
     */
    //Metodo a implementar para recibir event callbacks
    public interface duchaDialogNeutralListener {

        public void onAccionNeutral(DialogFragment dialog, int mins);

    }

    duchaDialogNeutralListener listenerNeutral;

    /**
     * Override the Fragment.onAttach() method to instantiate the AppDialogListener
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {

            listenerNeutral=(duchaDialogNeutralListener) activity;

        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement duchaDialogListener");
        }

    }

}


