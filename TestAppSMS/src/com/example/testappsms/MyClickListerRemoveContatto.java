package com.example.testappsms;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * Ascoltatore che viene richiamanto per rimuore un contatto dalla lista di quelli selezionati
 * verso cui inviare il messaggio
 *
 */
public class MyClickListerRemoveContatto implements OnClickListener{

	private ArrayList<Contatto> arrayListContatti;
	private AdapterSelectedContact adapter;
	private int indiceDaRimuovere;
	
	public MyClickListerRemoveContatto(ArrayList<Contatto> arrayListContatti ,AdapterSelectedContact adapter, int indiceDaRimuovere){
		
		this.arrayListContatti=arrayListContatti;
		this.adapter=adapter;
		this.indiceDaRimuovere=indiceDaRimuovere;
		
	}
	
	//metodo che rimuove dalla listView dei contatti destinatari , il contatto di indice
	//indiceDaRimuovere
	@Override
	public void onClick(View v) {
		
		arrayListContatti.remove(indiceDaRimuovere);
		adapter.notifyDataSetChanged();
		
	}

}
