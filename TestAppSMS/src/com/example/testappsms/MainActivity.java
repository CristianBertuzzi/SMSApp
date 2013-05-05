package com.example.testappsms;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private boolean contattiSelezionati=false;
	private final int SELECT_CONTACT=1;
	
	//private ArrayList <Contatto>arrayListContatti = new ArrayList <Contatto>();
	private ArrayList <String>arrayListContatti;
    private ListView listaContatti ;
    private ArrayAdapter <String>adapter;
    
    //private CustomAdapter adapter = new CustomAdapter(this, R.layout.rowcustom, arrayListContatti);
	
	private OnClickListener inviaSMSClickListener = new OnClickListener() {
		
		//alla pressione del tasto "invia" verifico di aver selezionato
		//dei contatti prima di poter inviare un messaggio
		@Override
		public void onClick(View v) {
			if(contattiSelezionati){
				showToast("Messagio inviato!");
			}else{
				showToast("Errore!Seleziona prima qualche contatto!");
			}
			
		}
	};

	//alla pressione del tasto "seleziona contatti" lancia un nuovo
	//c
	private OnClickListener selectContattiListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, SELECT_CONTACT);

		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //mi collego agli elementi della grafica che mi servono
        Button inviaMsgButton = (Button)findViewById(R.id.inviaSMSButton);
        inviaMsgButton.setOnClickListener(inviaSMSClickListener);
        
        ImageButton slectContact = (ImageButton)findViewById(R.id.selectContattiButton);  
        slectContact.setOnClickListener	(selectContattiListener);

        
        arrayListContatti = new ArrayList <String>(); 
        
        listaContatti=(ListView)findViewById(R.id.listaContattiSelezionati);

        //listaContatti.setAdapter(adapter);

        adapter=new ArrayAdapter<String>(
        									this,
        									R.layout.row,
        									R.id.textViewList,
        									arrayListContatti);
        listaContatti.setAdapter(adapter);

    }
    
    
    private void showToast(String testo){
		Toast toast=Toast.makeText(getApplicationContext(),testo,Toast.LENGTH_SHORT);
		toast.show();
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    //metodo usato per ricevere il risultato dell'intent per selezionare i contatti
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
    	super.onActivityResult(reqCode, resultCode, data);

    	switch (reqCode) {
    	case (SELECT_CONTACT) :
    		
    		if (resultCode == Activity.RESULT_OK) {
    			Uri contactData = data.getData();
    			Cursor cur =  managedQuery(contactData, null, null, null, null);
    			
    			if (cur.moveToFirst()) {
    				String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
    				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

    				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) 
    				{
    					// Query phone here. Covered next
    					Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null); 
    					while (phones.moveToNext()) { 
    						String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    						Log.i("Number", phoneNumber);
    						showToast("Numero contatto selzionato= " + phoneNumber +
    								"\nnome contatto selezionato= " + name);
    						addContattoToList(  name , phoneNumber);
    					} 
    					phones.close();

    					Log.i("Names", name);
    				}

    			}
    		}
    	break;
    	}
    }


	private void addContattoToList(String name, String phoneNumber) {
        Contatto c = new Contatto(name, "", phoneNumber);
        arrayListContatti.add(name + "    -  " + phoneNumber);
        adapter.notifyDataSetChanged();
	}
}
