package com.example.testappsms;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private boolean contattiSelezionati=false;
	private final int SELECT_CONTACT=1;
	
	private OnClickListener inviaSMSClickListener = new OnClickListener() {
		
		//alla pressione del tasto "invia" verifico di aver selezionato
		//dei contatti prima di poter inviare un messaggio
		@Override
		public void onClick(View v) {
			if(contattiSelezionati){
				Toast toast=Toast.makeText(getApplicationContext(),"Messaggio inviato!",Toast.LENGTH_SHORT);
				toast.show();
			}else{
				Toast toast=Toast.makeText(getApplicationContext(),"Seleziona quanlche ",Toast.LENGTH_SHORT);
				toast.show();
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


    }
    
    //public void onStart(){
    		
    		//inviaMsgButton.setText("CASA");	
    //}

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
            Cursor c =  managedQuery(contactData, null, null, null, null);
            if (c.moveToFirst()) {
              String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
              // TODO Whatever you want to do with the selected contact name.
            }
          }
          break;
      }
    }
    
}
