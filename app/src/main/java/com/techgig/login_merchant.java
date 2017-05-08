package com.techgig;

/**
 * Created by Vishal Mago on 05/06/2017.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login_merchant extends Activity implements View.OnClickListener {
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText mEmail;
    private EditText mPassword;
    private Button mlogin;
    private Intent intent;
    private ProgressDialog progressDialog;
    final Context context = this;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_merchant);
        if(isRegistered()){
            Intent i=new Intent(getApplicationContext(),EmailPasswordActivity.class);
            startActivity(i);
            finish();
        }
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mEmail = (EditText) findViewById(R.id.memail);
        mPassword = (EditText) findViewById(R.id.mpassword);
        mlogin=(Button)findViewById(R.id.login_merchant);
        mlogin.setOnClickListener(this);
    }
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.login_merchant:
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
                signIn(mEmail.getText().toString(), mPassword.getText().toString());
                break;
        }
    }
    private void signIn(String email, String password){
        Log.d(TAG, "signIn:" + email);
        boolean evalid = true,pvalid=true;
        email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
            evalid = false;
        } else {
            mEmail.setError(null);
        }
        password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            pvalid = false;
        } else {
            mPassword.setError(null);
        }
        if(evalid&&pvalid) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                FirebaseUser user = mAuth.getCurrentUser();
                                mDatabase.child("users").child(user.getUid()).child("type").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final int type = Integer.valueOf(dataSnapshot.getValue().toString());
                                        System.out.println("Heloo......." + type);
                                        if (type == 2) {
                                            //Opening shared preference
                                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);

                                            //Opening the shared preferences editor to save values
                                            SharedPreferences.Editor editor = sharedPreferences.edit();

                                            //Saving the boolean as true i.e. the device is registered
                                            editor.putBoolean(Constants.MREGISTERED, true);
                                            editor.apply();

                                            Log.d(TAG, "signInWithEmail:success");
                                            Toast.makeText(login_merchant.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            intent = new Intent(getApplicationContext(), EmailPasswordActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                                            alertDialogBuilder
                                                    .setTitle("PayOff")
                                                    .setIcon(R.drawable.logo)
                                                    .setMessage("Invalid Merchant ID. Fill the correct Merchant ID and try again")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .setCancelable(false);

                                            // create alert dialog
                                            AlertDialog alertDialog = alertDialogBuilder.create();

                                            // show it
                                            alertDialog.show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });

                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                progressDialog.dismiss();
                                Toast.makeText(login_merchant.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                ;
                            }
                            if (!task.isSuccessful()) {
                                progressDialog.dismiss();
                                Log.w(TAG, "Task Not Sucessful");
                            }
                        }
                    });
        }
    }
    private boolean isRegistered() {
        //Getting shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);

        //Getting the value from shared preferences
        //The second parameter is the default value
        //if there is no value in sharedpreference then it will return false
        //that means the device is not registered
        return sharedPreferences.getBoolean(Constants.MREGISTERED, false);
    }
}
