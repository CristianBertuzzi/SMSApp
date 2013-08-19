package com.example.testappsms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class MyDialog  {
	
	private String title;
	private String text;
	private Context context;
	private int indiceContatto;
	
	public MyDialog(String title , String text , Context context, int indiceContatto){
		this.title=title;
		this.text=text;
		this.context=context;
		this.indiceContatto=indiceContatto;
	}
	
	
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