package com.example.testappsms;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;

public class MyClickListerRemoveContatto implements OnClickListener{

	private ArrayList<Contatto> arrayListContatti;
	private CustomAdapter adapter;
	private int indiceDaRimuovere;
	
	public MyClickListerRemoveContatto(ArrayList<Contatto> arrayListContatti ,CustomAdapter adapter, int indiceDaRimuovere){
		
		this.arrayListContatti=arrayListContatti;
		this.adapter=adapter;
		this.indiceDaRimuovere=indiceDaRimuovere;
		
	}
	
	@Override
	public void onClick(View v) {
		
		arrayListContatti.remove(indiceDaRimuovere);
		adapter.notifyDataSetChanged();
		
	}

}
