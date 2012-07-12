package com.pam;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class PaM_New_Post extends Activity {
	EditText contentEdit, usernameEdit;
	
	private OnClickListener postMessageListener = new OnClickListener() {
		
		public void onClick(View v) {
			String content = contentEdit.getText().toString();
			String username = usernameEdit.getText().toString();
			Log.d("PaM:content:username:",content + ":" + username);
			if(!content.equals("") && !username.equals("")) {
				String user_result = new PaM_API().postUser(username);
				try {
					JSONObject message_result = new JSONObject( new PaM_API().postMessage(content, username) );
					if ( message_result.getString("content").equals(content) ) {
						Toast.makeText(getApplicationContext(), "Message Successfully posted!", Toast.LENGTH_LONG).show();
						contentEdit.setText("");
						usernameEdit.setText("");
					}
					else {
						Toast.makeText(getApplicationContext(), "Something wrong... :( ", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else {
				Toast.makeText(getApplicationContext(), "Some fields missing", Toast.LENGTH_LONG).show();
			}
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_post);
        
        contentEdit = (EditText)findViewById(R.id.messageContentEdit);
        usernameEdit = (EditText)findViewById(R.id.usernameEdit);
        ((Button) findViewById(R.id.postNewMessageButton)).setOnClickListener(postMessageListener);
    }

    protected void postMessage(String content, String username) {
    	
	}

	protected void checkUsername(String username) {
		
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_post, menu);
        return true;
    }

    
}
