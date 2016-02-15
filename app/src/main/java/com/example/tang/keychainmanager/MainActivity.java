package com.example.tang.keychainmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tang.keychainstorelibrary.KeyChainStore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    EditText mEditText1;
    EditText mEditText2;
    EditText mEditText3;
    EditText mEditText4;

    KeyChainStore mKeyChainStore ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText1 = (EditText) findViewById(R.id.edittext1);
        mEditText2 = (EditText) findViewById(R.id.edittext2);
        mEditText3 = (EditText) findViewById(R.id.edittext3);
        mEditText4 = (EditText) findViewById(R.id.edittext4);

        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);

        mKeyChainStore = KeyChainStore.getInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button1){
            //encryt
            String initializeText1 = mEditText1.getText().toString();
            String initializeText2 = mEditText2.getText().toString();

            mKeyChainStore.saveInfoToDevice(initializeText1,initializeText2);
        }else{
            String initializeText1 = mEditText1.getText().toString();
            String value = mKeyChainStore.getInfoFromDevice(initializeText1);

            mEditText3.setText(value);
        }

    }
}
