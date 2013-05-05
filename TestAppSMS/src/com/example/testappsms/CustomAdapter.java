package com.example.testappsms;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<Contatto>{

    public CustomAdapter(Context context, int textViewResourceId,List<Contatto> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
             .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.rowcustom, null);
        TextView nome = (TextView)convertView.findViewById(R.id.textViewName);
        TextView numero = (TextView)convertView.findViewById(R.id.textViewNumber);
        Contatto c = getItem(position);
        nome.setText(c.getNome()+" "+c.getCognome());
        numero.setText(c.getTelefono());
        return convertView;
    }

}
