package com.techgig;

/**
 * Created by Vishal Mago on 05/05/2017.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class sign_up extends BaseActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    final Context context = this;
    private static final String TAG = "EmailPassword";
    private EditText regEmailField;
    private EditText regPasswordField;
    private Button SignUp;
    private TextView signin;
    private DatabaseReference mDatabase;
    private boolean signup_status=false;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        regEmailField=(EditText)findViewById(R.id.reg_email);
        regPasswordField=(EditText)findViewById(R.id.reg_pass);
        signin=(TextView)findViewById(R.id.textSignIn);
        SignUp=(Button)findViewById(R.id.buttonsSignUp);
        SignUp.setOnClickListener(this);
        signin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonsSignUp:
                createAccount(regEmailField.getText().toString(), regPasswordField.getText().toString());
                break;
            case R.id.textSignIn:
                Intent i=new Intent(sign_up.this,login.class);
                startActivity(i);
                finish();
                break;
        }
    }
    private void createAccount(final String email, final String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String username = usernameFromEmail(user.getEmail());
                            writeNewUser(user.getUid(),username,email,password);
                            signup_status=true;
                            if(signup_status==true) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                                alertDialogBuilder
                                        .setTitle("PayOff")
                                        .setIcon(R.drawable.logo)
                                        .setMessage("User Account successfully created. Press OK to Login")
                                        .setPositiveButton("OK",new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog,int which){
                                                Intent intent = new Intent(getApplicationContext(),login.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .setCancelable(false);

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();
                            }
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(sign_up.this, "User ID already exists",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }
    private void writeNewUser(String userId,String username,String email,String password) {
        user_model user = new user_model(username, email,password,500);
        mDatabase.child("users").child(userId).setValue(user);
    }
    private boolean validateForm() {
        boolean valid = true;
        String email = regEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            regEmailField.setError("Required.");
            valid = false;
        } else {
            regEmailField.setError(null);
        }
        String password = regPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            regPasswordField.setError("Required.");
            valid = false;
        } else {
            regPasswordField.setError(null);
        }
        return valid;
    }
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
}
