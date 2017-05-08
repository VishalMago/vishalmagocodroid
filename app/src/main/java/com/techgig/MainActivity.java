package com.techgig;

/**
 * Created by Vishal Mago on 05/05/2017.
 */
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener {
    private Button clogin,mlogin;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);

        //Getting the value from shared preferences
        //The second parameter is the default value
        //if there is no value in sharedpreference then it will return false
        //that means the device is not registered
        boolean first=sharedPreferences.getBoolean(Constants.REGISTERED,false);
        boolean second=sharedPreferences.getBoolean(Constants.MREGISTERED,false);
        if(first==true||second==true){
            Intent i=new Intent(getApplicationContext(),EmailPasswordActivity.class);
            startActivity(i);
            finish();
        }

        clogin=(Button)findViewById(R.id.Clogin);
        mlogin=(Button)findViewById(R.id.Mlogin);
        clogin.setOnClickListener(this);
        mlogin.setOnClickListener(this);
    }
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.Clogin:
                Intent intent = new Intent(this,login.class);
                this.startActivity(intent);
                finish();
                break;
            case R.id.Mlogin:
                intent = new Intent(this, login_merchant.class);
                this.startActivity(intent);
                finish();
                break;
        }
    }
}
