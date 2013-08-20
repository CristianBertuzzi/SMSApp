package com.example.testappsms;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Adapter usato per inserire i dati di un contatto (che viene selezionato)
 * nella riga della lista di contatti aggiunti il cui layout è stabilito 
 * nel file row.xml
 *
 */

public class AdapterSelectedContact extends ArrayAdapter<Contatto>{
 
	//pulsante per rimuover il contatto dalla lista
	private ImageButton removeButton;
	
	//ascoltatore che viene aggiunto per rimuovere il contatto dalla lista
	private MyClickListerRemoveContatto listenerRemoveContatto;
	
	//lista dei contatti destinatari del messaggio
	private ArrayList<Contatto> arrayListContatti;
	
    public AdapterSelectedContact(Context context, int textViewResourceId,List<Contatto> objects) {
        super(context, textViewResourceId, objects);
        this.arrayListContatti = (ArrayList<Contatto>) objects;
    }
    

    /**
     * Metodo che viene richiamato in automatico quando si scrolla la listView
     * o quando viene aggiunto un contatto alla lista dei destinatari del messaggio
     * 
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
             .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        convertView = inflater.inflate(R.layout.row, null);
        
        removeButton = (ImageButton) convertView.findViewById(R.id.removeContattoButton);
        
        //aggiungo l'ascoltatore al pulsate per rimuovere il contatto nella posizione position
        listenerRemoveContatto = new MyClickListerRemoveContatto(arrayListContatti, this, position); 
        removeButton.setOnClickListener( listenerRemoveContatto );
        
        TextView nome = (TextView)convertView.findViewById(R.id.nomeEnumeroContatto);
        
        //TextView numero = (TextView)convertView.findViewById(R.id.textViewNumber);
        
        
        Contatto c = getItem(position);
        nome.setText(c.getNome()+" - "+c.getTelefono() );

        return convertView;
    }

}
