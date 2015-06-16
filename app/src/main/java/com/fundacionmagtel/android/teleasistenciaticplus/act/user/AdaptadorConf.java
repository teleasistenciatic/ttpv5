package com.fundacionmagtel.android.teleasistenciaticplus.act.user;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fundacionmagtel.android.teleasistenciaticplus.R;

/**
 * Adaptador para crear las lineas de la lista de la actividad de configuración.
 *
 * Created by SAMUAN on 15/06/2015.
 */
public class AdaptadorConf extends ArrayAdapter {


    private final Activity context;
    private final String[] textos;
    private final Integer[] imageId;

    /**
     *
     * @param context
     * @param textos Array con los textos a añadir en la lista.
     * @param imageId Array con los identificadores de las imagenes a añadir en la lista.
     */
    public AdaptadorConf(Activity context, String[] textos, Integer[] imageId) {
        super(context, R.layout.layout_linea_conf, textos);
        this.context = context;
        this.textos = textos;
        this.imageId = imageId;
    }

    /**
     * Método que construye cada una de las líneas de la lista.
     * Añade la imagen y el texto en el layout correspondiente.
     *
     * @param position
     * @param view
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.layout_linea_conf, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(textos[position]);

        imageView.setImageResource(imageId[position]);
        return rowView;
    }

}
