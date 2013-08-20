package com.example.testappsms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Casella di testo personalizzata per visualizzare a video un avviso con la 
 * possibilità di scegliere si o annulla alla domanda posta.
 * 
 * La richiesta nel mio caso è quella di splittare o meno il messaggio.
 * 
 */
public class MyDialog  {
	
	//titolo del box di dialogo
	private String title;
	
	//testo del messaggio di richiesta input dall'utente
	private String text;
	
	//riferimento all'activity principale, usato per invocare il metodo di
	//ritorno(callback) quando l'utente effettua la sua scelta 
	private Context context;
	
	//indice del contatto ,nella listView ,verso cui si deve inviare il messaggio 
	private int indiceContatto;
	
	public MyDialog(String title , String text , Context context, int indiceContatto){
		this.title=title;
		this.text=text;
		this.context=context;
		this.indiceContatto=indiceContatto;
	}
	
	
	//metodo invocato per far apparire il box di dialogo
    public void showDialog() {

		// 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
       
        builder.setTitle(title);

        // 2. Chain together various setter methods to set the dialog characteristics

        builder.setMessage(text)
        	   .setCancelable(false)
               .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       ((MainActivity)context).setDialogResponse(true, indiceContatto);
                       dialog.dismiss();
                   }
               })
               .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   ((MainActivity)context).setDialogResponse(false , indiceContatto);
                	   dialog.dismiss();
                	   
                   }
               });
        
		// 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}