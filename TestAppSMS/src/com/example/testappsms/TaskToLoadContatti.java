package com.example.testappsms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class TaskToLoadContatti extends AsyncTask< Void, Void, ArrayList<Map<String, String>>> {

	private MainActivity mainActivity;
	private ArrayList<Map<String, String>> mPeopleList;
	
	public TaskToLoadContatti(ArrayList<Map<String, String>> mPeopleList,MainActivity mainActivity){
		this.mainActivity = mainActivity;
		this.mPeopleList = mPeopleList;
	}
	
	//metodo che carica nell'mPeopleList (l'AutoCompleteView )
    //tutti i contatti della rubrica
    private ArrayList<Map<String, String>> addContattiToAutoCompleteTextView() {
    	
    	ArrayList<Map<String, String>> ris = new ArrayList<Map<String, String>>();
    	
    	//parametro (con il punto di domanda) che si sostituiscono nella query 
    	//String[] mSelectionArgs = {"%" + nomeContatto + "%" };
    	
		ContentResolver contentResolver= mainActivity.getContentResolver();


    	//campi che si desiderno di un contatto
    	String[] campiDaPrelevare = {
    			ContactsContract.Contacts.HAS_PHONE_NUMBER ,
    			ContactsContract.Contacts._ID ,
    			ContactsContract.Contacts.DISPLAY_NAME 
    			
    	};
    	
    	
		//recupero tutti i contatti presenti nella rubrica 
    	Cursor people = contentResolver.query(
    			ContactsContract.Contacts.CONTENT_URI,
    			campiDaPrelevare, 
    			null, 
    			null, 
    			null);
    	
    	//scorro tutto l'elenco dei contatti e lo metto nell'AutoCompleteView
    	while (people.moveToNext()) {

    		//recupero il nome del contatto
    		String contactName = people.getString(people.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
    		
    		//recupero l'id del contatto
    		String contactId = people.getString(people.getColumnIndex(ContactsContract.Contacts._ID));
    		
    		//recupero l'intero che indica se il contatto ha il numero di telefono
    		int hasPhone = Integer.parseInt( people.getString(people.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) ) ;

    		
    		
    		if ( hasPhone > 0){
    			
    			//ora so che il contatto presenta uno o più numeri di telefono
    			
    			
    			//con questa query recupero i numeri di telefono del singolo contatto
    			Cursor phones = contentResolver.query(
    					Phone.CONTENT_URI,
    					null,
    					Phone.CONTACT_ID + " =?",
    					new String[]{ contactId }, // parametro della query che viene sostituito al ?(punto di domanda)
    					Phone.DISPLAY_NAME + " ASC");
    			
    			while (phones.moveToNext()){
    				
    				int indiceColonnaTelefono = phones.getColumnIndex(Phone.NUMBER);
    				int indiceColonnaTipoContatto = phones.getColumnIndex(Phone.TYPE);
    				
    				//store numbers and display a dialog letting the user select which.
    				String phoneNumber = phones.getString(indiceColonnaTelefono);
    				String numberType = phones.getString(indiceColonnaTipoContatto);
    				
    				//hashmap che contiene i dati di un contatto: un contatto viene 
    				//aggiunto più volte se ha più numeri di telefono
    				Map<String, String> namePhoneType = new HashMap<String, String>();
    				
    				namePhoneType.put("Nome", contactName);
    				namePhoneType.put("Telefono", phoneNumber);
    				
    				if(numberType.equals("0")){
    					namePhoneType.put("Tipo", "Work");
    				}else{
    					if(numberType.equals("1")){
    						
    						namePhoneType.put("Tipo", "Home");
    						
    					}else if(numberType.equals("2")){
    						
    						namePhoneType.put("Tipo",  "Mobile");
    						
    					}else{
    						
    						namePhoneType.put("Tipo", "Other");
    						
    					}
    				}
    				//Then add this map to the list.
    				ris.add(namePhoneType);
    			}//fine ciclo per scorrere i contatti della rubrica
    			phones.close();
    		}//fine if di verifica che il contatto abbia un numero di telefono
    	}
    	people.close();
    	
    	return ris;
    }

    
    //fatto in background
	@Override
	protected ArrayList<Map<String, String>> doInBackground(Void... listaParam) {
		
		return addContattiToAutoCompleteTextView();
	}
	
	//carica i contatti nell'array per l'autocompletamento
    protected void onPostExecute(ArrayList<Map<String, String>> nuovaListaAuto) {
        mPeopleList.clear();
    	
    	for(Map<String, String> contatto  : nuovaListaAuto){
        	mPeopleList.add(contatto);
        }
    }

	
}
