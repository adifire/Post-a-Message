package com.pam;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TwoLineListItem;
import android.support.v4.app.NavUtils;

public class PaM_Main extends ListActivity {
	Handler handler;
	
	
	private OnClickListener postMessageListener = new OnClickListener() {
		
		public void onClick(View v) {
			Intent postMessageIntent = new Intent(PaM_Main.this, PaM_New_Post.class);
			startActivity(postMessageIntent);
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pam_main);
        
        handler = new Handler();
        Button postButton = (Button) findViewById(R.id.post_button);
        postButton.setOnClickListener(postMessageListener);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	new GetMessages().execute("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pam_main, menu);
        return true;
    }

    class GetMessages extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String uri = "http://10.0.2.2:8090/messages";
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(uri);
			//Log.d("loltale:params", Integer.toString(params.length));
			
			try {
				HttpResponse response = httpClient.execute(httpGet);
				HttpEntity entity = response.getEntity();
				
				if(entity != null) {
					return EntityUtils.toString(entity);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.d("PaM:result:", result);
			try {
				JSONArray messages = new JSONArray(result);
				ArrayList<PaM_Message> list = new ArrayList<PaM_Message>();
				if (messages != null) { 
				   int len = messages.length();
				   for (int i=0;i<len;i++) { 
				    list.add(new PaM_Message(messages.get(i).toString()));
				   } 
				}
				
				final ArrayList<PaM_Message> mList = list;
				handler.post(new Runnable() {
					
					public void run() {
						setListAdapter(new MessagesAdapter(getApplicationContext(), mList));
					}
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
    }
    
    class MessagesAdapter extends ArrayAdapter<PaM_Message> {
    	private List<PaM_Message> messages;
    	
    	public MessagesAdapter(Context context, List<PaM_Message> messages) {
    		super(context, android.R.layout.simple_list_item_2, messages);
    		this.messages = messages;
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		TwoLineListItem view;
    		
    		if(convertView == null) {
    			LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = (TwoLineListItem)inflater.inflate(android.R.layout.simple_list_item_2, null);
    		}
    		else {
    			view = (TwoLineListItem)convertView;
    		}
    		PaM_Message data = messages.get(position);
            view.getText1().setText(data.getMessage());
            view.getText2().setText(data.getUser());
    		return view;
    	}
    }
}
