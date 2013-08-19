package com.example.testappsms;

import java.util.ArrayList;
import java.util.Map;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	
	private final int SELECT_CONTACT=1;
	
	//array di contatti di appoggio per la ListView : è il modello dei dati dove si aggiungono i contatti  
	private ArrayList <Contatto>arrayListContatti;
	
	//ListView di contatti a cui devo INVIARE IL MESSAGGIO
    private ListView listaContatti ;
    
    //adapter usato per personalizzare le righe della ListView listaContatti
    private CustomAdapter adapter;
    
    /*variabili x l'AutoCompleteTextView*/
    
    //lista di contatti associata all'AutoCompleteTextView
    private ArrayList<Map<String, String>> mPeopleList;

    //adapter per personalizzare le ricghe dell'AutoCompleteTextView
    private SimpleAdapter mAdapter;
    
    
    //AutoCompleteTextView usata per inserire un nuovo contatto
    private AutoCompleteTextView mTxtPhoneNo;
    
    private String smsText;
	
    private MainActivity activity = this;
    
    //usato per sapere che bottone è stato premuto nelle finestre di dialogo
    private boolean rispostaDialog = true;
    
    //se il messagio occupa più di 160 caratteri vine diviso e messo qua
    private String[] pezziMsg;
    
	private int numSMSInviati;
	
	private ArrayList<String[]> vetMsgToSend;
	
	private SmsManager smsMan; 
	
    
	private OnClickListener inviaSMSClickListener = new OnClickListener() {
		
		//alla pressione del tasto "invia" verifico di aver selezionato
		//dei contatti prima di poter inviare un messaggio
		@Override
		public void onClick(View v) {
			//invio un messagio a tutti i destinatari
			if(arrayListContatti.size()  > 0){
				
				EditText msgField = (EditText)findViewById(R.id.textBoxSMS);
				String msgToSend = msgField.getText().toString();				
				
				numSMSInviati=0;
				
				if(!msgToSend.isEmpty()){

					smsMan = SmsManager.getDefault();

					boolean parse = smsNeedToParse(msgField);
						
					vetMsgToSend = new ArrayList<String[]>();
					//con questo ciclo faccio il parse e creo i messaggi da inviare a
					//ciascun contatto selezionato
					for(int i=0; i < arrayListContatti.size() ; i++){
						
						if(parse){
							
							msgToSend = parsingAndSobstituteSMS(arrayListContatti.get(i).getNome());
							Log.d("TESTO INVIATO: " , msgToSend );
						}
						//aggiungo il messaggio al vettore : 
						//ogni elemento verrà poi splittato se neccessario
						
						vetMsgToSend.add(new String[]{msgToSend});
						
					}	
					
					for(int i=0; i < vetMsgToSend.size(); i++){
					
						pezziMsg = splitStringNChar( msgToSend , 160 );
						
						vetMsgToSend.set(i,pezziMsg);
						
						//se il messaggio è diviso in più pezzi chiedo all'utente se vuole inviarlo lo stesso
						if(pezziMsg.length > 1){
							
							MyDialog m1 = new MyDialog( "Messaggio per "+arrayListContatti.get(i).getNome()+ " lungo!","Inviare "+ pezziMsg.length +" messaggi?" ,activity,i );

							m1.showDialog();
							
							//MyDialog richiama il metodo setDialogResponse per impstare la risposta e inviare il messagio splittato se l'utente lo vuole
						
						//se il messaggio non è stato splittato lo invio così com'è	
						}else{
							
							smsMan.sendTextMessage(arrayListContatti.get(i).getTelefono(), null, pezziMsg[0], null, null);

						}
					}//fine ciclo per vedere se devo splittare i messaggi
					
				}else{
					showToast("Messaggio vuoto!");	
				}
					
			}else{
				showToast("Nessun contatto selezionato!");
			}
			
		}
	};

	//alla pressione del tasto "seleziona contatti" lancia un nuovo
	//intent per la selezione di un contatto
	private OnClickListener selectContattiListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, SELECT_CONTACT);
		}
	};


	
    //ascoltatore per modificare il conto dei caratteri del testo del messaggio
	TextWatcher listenerCaratteriInseriti = new TextWatcher(){
    	
		public void  afterTextChanged (Editable s){ 
    		
    		Log.d("seachScreen", "afterTextChanged");
    		
    		TextView txtNumCar = (TextView)findViewById(R.id.numCaratteriInseriti);

			txtNumCar.setText(s.length() + "/160");
    	} 
    	public void  beforeTextChanged  (CharSequence s, int start, int count, int after){} 
    	
    	public void  onTextChanged  (CharSequence s, int start, int before, int count){ }
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
        
        EditText msgField = (EditText)findViewById(R.id.textBoxSMS);
        
        msgField.addTextChangedListener(listenerCaratteriInseriti);
        
        this.smsText=null;
        
        //per l'autocompletamento nell'inserimento del contatto
        mPeopleList = new ArrayList<Map<String, String>>();
        

        
        mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        //disabilito la ricerca di contatti finchè non ho caricato tutti i contatti
        mTxtPhoneNo.setFocusable(false);
        
        mAdapter = new SimpleAdapter(this, mPeopleList, R.layout.autocomplete_contact,
                new String[] { "Nome", "Telefono", "Tipo" }, 
                new int[] { R.id.ccontName, R.id.ccontNo, R.id.ccontType });
        
 
        mTxtPhoneNo.setOnItemClickListener( new OnItemClickListener() {

        	@Override
        	public void onItemClick(AdapterView<?> av, View arg1, int index, long arg3) {
        		@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) av.getItemAtPosition(index);

        		String name  = map.get("Nome");
        		String number = map.get("Telefono");
        		//mTxtPhoneNo.setText(""+name+"  <"+number+">");
        		mTxtPhoneNo.setText("");
        		addContattoToList(name,number);

        	}

        });
        
        mTxtPhoneNo.setAdapter(mAdapter);        
        
        arrayListContatti = new ArrayList <Contatto>();
        
        listaContatti=(ListView)findViewById(R.id.listaContattiSelezionati);
        listaContatti.setCacheColorHint(getResources().getColor(R.color.grayBlack));

        adapter = new CustomAdapter(this, R.layout.row, arrayListContatti);
        listaContatti.setAdapter(adapter);
        
		
        //carica i contatti nel vettore per l'autocompletamento
    	TaskToLoadContatti asyncTask = new TaskToLoadContatti(mPeopleList , this, mTxtPhoneNo);
		asyncTask.execute();
        
        //se l'applicazione è stata ripristinata, allora recupero l'ArrayList
        if (savedInstanceState != null){
        	ArrayList<String> arrayNomi = savedInstanceState.getStringArrayList( "arrayNomi" );
        	ArrayList<String> arrayNumeri = savedInstanceState.getStringArrayList( "arrayNumeri" );
        	
        	for(int i=0 ; i < arrayNomi.size() ; i++ ){	
        		arrayListContatti.add(new Contatto(arrayNomi.get(i), "", arrayNumeri.get(i)));
        	}
        	
        	adapter.notifyDataSetChanged();
        	
        	TextView txtNumCar = (TextView)findViewById(R.id.numCaratteriInseriti);
        	
        	
        	String numCar = savedInstanceState.getString("numeroCaratteri");
        	String txtSMS = savedInstanceState.getString("testoMessaggio" );
        	
        	txtNumCar.setText(numCar);
        	msgField.setText(txtSMS);

        	
        }
    }

	protected void onStart(){
		
        super.onStart();
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

    	//usato per capire quale capire quale activity 
    	switch (reqCode) {
    	
    	case (SELECT_CONTACT) :

    		//se l'intent ha ritornato un risultato valido
    		if (resultCode == Activity.RESULT_OK) {
    			Uri contactData = data.getData();
    			Cursor cur =  managedQuery(contactData, null, null, null, null);

    			if (cur.moveToFirst()) {
    				//recupero il nome e l'ID del contatto
    				String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
    				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

    				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) 
    				{
    					// Query phone here. Covered next
    					Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null); 

    					//aggiungo tutti i numeri di telefono del contatto all'elenco dei destinatari
    					while (phones.moveToNext()) { 
    						String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

    						Log.d("Number", phoneNumber);

    						addContattoToList(  name , phoneNumber);
    					} 
    					phones.close();

    					Log.d("Names", name + " " );
    				}
    			}
    		}

    	break;
    	}
    	
    	Log.d("debug","fine contact view");
    }


    //se non ègià presente aggiungo un contatto alla all'ArrayList di contatti
	private void addContattoToList(String name, String phoneNumber) {
        
        boolean trovato=false;
        
        //verifico che non ci siano doppioni nella lista
        for( int i=0 ; i < arrayListContatti.size() && !trovato; i++ ){
        	
        	Contatto c1 = arrayListContatti.get(i);
        	if(c1.getNome().equals(name) && c1.getTelefono().equals(phoneNumber) )
        		trovato=true;
        }
        
        if(!trovato){
        
        	arrayListContatti.add(new Contatto(name, "", phoneNumber));
        	adapter.notifyDataSetChanged();
		}else{
			showToast("Contatto già presente nei contatti slezionati!");
		}
        
        Log.d("debug", "OOOOLLLLLLL");
	}
	
	//metodo che sostituisce tutti i caratteri doppio dollaro($$)
	//con la stringa nameToWrite
	private String parsingAndSobstituteSMS(String nameToWrite) {
		
		String ris = smsText.replace("$$", nameToWrite);
		return ris;
	}
	
	
	private boolean smsNeedToParse(EditText msgField) {
		
		boolean ris = false;
		String msgText = msgField.getText() + "";
		if(msgText.contains("$$")){
			ris = true;
			this.smsText=msgText;
		}
		
		return ris;
		
	}

	public String[] splitStringNChar( String stringToSplit, int interval) {

		int arrayLength = (int) Math.ceil(((stringToSplit.length() / (double)interval)));
		String[] result = new String[arrayLength];

		int j = 0;
		int lastIndex = result.length - 1;
		for (int i = 0; i < lastIndex; i++) {
			result[i] = stringToSplit.substring(j, j + interval);
			j += interval;
		} //Add the last bit
		result[lastIndex] = stringToSplit.substring(j);

		return result;
	}
	
	
	//metodo invocato quando per salvare lo stato dell'appllicazione prima che avvenga una rotazione
	//salvo l'ArrayList dei contatti nel Bundle outState, tramite 2 arraylist
	@Override
	public void onSaveInstanceState (Bundle outState){
		
		ArrayList<String> arrayNomi = new ArrayList<String>(); 
		ArrayList<String> arrayNumeri = new ArrayList<String>();
		
		for( Contatto c1 : arrayListContatti ){
			arrayNomi.add(c1.getNome());
			arrayNumeri.add(c1.getTelefono());	
		}
		
		EditText msgBox = (EditText)findViewById(R.id.textBoxSMS);
		TextView numCar = (TextView)findViewById(R.id.numCaratteriInseriti);
		
		
		outState.putString("numeroCaratteri", numCar.getText()+"");
		outState.putString("testoMessaggio", msgBox.getText()+"" );
		outState.putStringArrayList("arrayNomi", arrayNomi);
		outState.putStringArrayList("arrayNumeri", arrayNumeri);
	}



	/**
	 * Metodo invocato dal dialog in caso si voglia spedire l'sms in più parti
	 * @param b
	 * @param indiceContatto
	 */
	public void setDialogResponse(boolean b, int indiceContatto) {
		
		this.rispostaDialog=b;
		
		Log.d( "smstag", "rispostaDialog = " + rispostaDialog);
		
		//se è true allora l'utente vuole inviare il messaggio 
		//diviso in più sms	
		if(rispostaDialog == true){
			
			String[] piecesSMS = vetMsgToSend.get(indiceContatto);
			for(int j=0; j < piecesSMS.length ; j++){
				smsMan.sendTextMessage(arrayListContatti.get(indiceContatto).getTelefono(), null, piecesSMS[j], null, null);
				numSMSInviati++;
			}
			
			showToast("Inviato/i "+numSMSInviati + " SMS");
		}
	}
	
}