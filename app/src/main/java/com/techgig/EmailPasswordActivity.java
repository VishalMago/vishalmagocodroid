package com.techgig;

/**
 * Created by Vishal Mago on 05/06/2017.
 */
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmailPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String merchant_id="jNTs8C76GfhyhpmzykLkJklFrl73";
    private static final String TAG = "EmailPassword";
    protected Integer t_money=0,accbalance=0,merchant_bal=0;
    private TextView email,acctype,balance,connection;
    private FrameLayout layout;
    private Resources mResources;
    private Button mpayment;
    private ImageView mImageView;
    private Bitmap mBitmap;

    private DatabaseReference mDatabase,merchantdatabase;
    private DatabaseReference connectedRef;
    private FirebaseAuth mAuth;
    private boolean flag,tflag;
    final Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);
        connectedRef = FirebaseDatabase.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        merchantdatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        merchantdatabase.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        //rounded image drawable
        mResources = getResources();
        mImageView = (ImageView) findViewById(R.id.imageview_rounded_image);
        mBitmap = BitmapFactory.decodeResource(mResources,R.drawable.profile);

        mImageView.setImageBitmap(mBitmap);
        mImageView.setImageBitmap(mBitmap);
        RoundedBitmapDrawable roundedImageDrawable = createRoundedBitmapImageDrawableWithBorder(mBitmap);
        mImageView.setImageDrawable(roundedImageDrawable);
        email=(TextView)findViewById(R.id.email);
        acctype=(TextView)findViewById(R.id.acctype);
        balance=(TextView)findViewById(R.id.balance);
        connection=(TextView)findViewById(R.id.connection);
        layout=(FrameLayout)findViewById(R.id.layout);
        mpayment=(Button)findViewById(R.id.pay);
        mpayment.setOnClickListener(this);
    }
    private RoundedBitmapDrawable createRoundedBitmapImageDrawableWithBorder(Bitmap bitmap){
        int bitmapWidthImage = bitmap.getWidth();
        int bitmapHeightImage = bitmap.getHeight();
        int borderWidthHalfImage = 4;

        int bitmapRadiusImage = Math.min(bitmapWidthImage,bitmapHeightImage)/2;
        int bitmapSquareWidthImage = Math.min(bitmapWidthImage,bitmapHeightImage);
        int newBitmapSquareWidthImage = bitmapSquareWidthImage+borderWidthHalfImage;

        Bitmap roundedImageBitmap = Bitmap.createBitmap(newBitmapSquareWidthImage,newBitmapSquareWidthImage,Bitmap.Config.ARGB_8888);
        Canvas mcanvas = new Canvas(roundedImageBitmap);
        mcanvas.drawColor(Color.GRAY);
        int i = borderWidthHalfImage + bitmapSquareWidthImage - bitmapWidthImage;
        int j = borderWidthHalfImage + bitmapSquareWidthImage - bitmapHeightImage;

        mcanvas.drawBitmap(bitmap, i, j, null);

        Paint borderImagePaint = new Paint();
        borderImagePaint.setStyle(Paint.Style.STROKE);
        borderImagePaint.setStrokeWidth(borderWidthHalfImage*2);
        borderImagePaint.setColor(Color.GRAY);
        mcanvas.drawCircle(mcanvas.getWidth()/2, mcanvas.getWidth()/2, newBitmapSquareWidthImage/2, borderImagePaint);

        RoundedBitmapDrawable roundedImageBitmapDrawable = RoundedBitmapDrawableFactory.create(mResources,roundedImageBitmap);
        roundedImageBitmapDrawable.setCornerRadius(bitmapRadiusImage);
        roundedImageBitmapDrawable.setAntiAlias(true);
        return roundedImageBitmapDrawable;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null) {
            email.setText(currentUser.getEmail());
            updateUI(currentUser);
        }
    }

    private void signOut() {
        new AlertDialog.Builder(this).setMessage("Are you sure you want to Log Out")
                .setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);

                        //Opening the shared preferences editor to save values
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Constants.MREGISTERED,false);
                        editor.putBoolean(Constants.REGISTERED,false);
                        editor.apply();
                        Intent i=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(i);
                        finish();
                        mAuth.signOut();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            mDatabase.child("users").child(user.getUid()).child("type").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final int type = Integer.valueOf(dataSnapshot.getValue().toString());
                    if (type == 1) {
                        acctype.setText("Customer");
                        mpayment.setVisibility(View.VISIBLE);

                    } else if (type == 2) {
                        acctype.setText("Merchant");
                        mpayment.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
            mDatabase.child("users").child(user.getUid()).child("balance").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    accbalance = Integer.valueOf(dataSnapshot.getValue().toString());
                    balance.setText("Rs."+"\t"+accbalance.toString());
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
            merchantdatabase.child("users").child(merchant_id).child("balance").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    merchant_bal = Integer.valueOf(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
            connectedRef.child(".info/connected").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        flag=true;
                        connection.setText("Online");
                        layout.setVisibility(View.INVISIBLE);
                        if (tflag==true) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            accbalance = accbalance - t_money;
                            if (accbalance>=0) {
                                merchant_bal = merchant_bal + t_money;
                                mDatabase.child("users").child(user.getUid()).child("balance").setValue(accbalance);
                                merchantdatabase.child("users").child(merchant_id).child("balance").setValue(merchant_bal);
                                t_money = 0;
                            }
                            else{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                                alertDialogBuilder
                                        .setTitle("PayOff")
                                        .setIcon(R.drawable.logo)
                                        .setMessage("Insufficient Balance")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setCancelable(true);

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();
                            }
                        }
                    } else {
                        flag=false;
                        layout.setVisibility(View.VISIBLE);
                        connection.setText("Offline");
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG,"Listener was cancelled");
                }
            });

        }

    }
    public void onClick(View v) {
        if (v == mpayment) {
            final Dialog dialog = new Dialog(EmailPasswordActivity.this);
            // Include dialog.xml file
            dialog.setContentView(R.layout.moneyamount);
            final NumberPicker quantity = (NumberPicker) dialog.findViewById(R.id.numberPicker);
            final Button makepayment = (Button) dialog.findViewById(R.id.transfer);
            makepayment.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    tflag=true;
                    if (v == makepayment) {
                        if (flag==false) {
                            t_money = t_money + quantity.getValue();
                            dialog.dismiss();
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                                alertDialogBuilder
                                        .setTitle("PayOff")
                                        .setIcon(R.drawable.logo)
                                        .setMessage("You have successfully made the payment of Rs." + "\t" + t_money + "\t" + "to merchant.Your balance will be updated when you will be online.As you are offline a message has been sent to merchant about this transaction")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setCancelable(true);

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();
                                try {
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage("8950435534", null, "A transaction of Rs." + "\t" + t_money + "\t" + "has been made by customer", null, null);
                                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                                            Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(),
                                            "SMS faild, please try again later!",
                                            Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                        }
                        else {
                            t_money = t_money + quantity.getValue();
                            FirebaseUser user = mAuth.getCurrentUser();
                            accbalance = accbalance - t_money;
                            if (accbalance >= 0) {
                                merchant_bal = merchant_bal + t_money;
                                mDatabase.child("users").child(user.getUid()).child("balance").setValue(accbalance);
                                merchantdatabase.child("users").child(merchant_id).child("balance").setValue(merchant_bal);
                                t_money = 0;
                                dialog.dismiss();
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                                alertDialogBuilder
                                        .setTitle("PayOff")
                                        .setIcon(R.drawable.logo)
                                        .setMessage("You have successfully made the payment of Rs." + "\t" + quantity.getValue() + "\t" + "to merchant.Your balance has been updated")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setCancelable(true);

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();
                            }else{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                                alertDialogBuilder
                                        .setTitle("PayOff")
                                        .setIcon(R.drawable.logo)
                                        .setMessage("Insufficient Balance")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setCancelable(true);

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();
                            }
                        }
                    }
                }
            });
            quantity.setMinValue(10);
            //Specify the maximum value/number of NumberPicker
            quantity.setMaxValue(10000);

            //Gets whether the selector wheel wraps when reaching the min/max value.
            quantity.setWrapSelectorWheel(true);
            dialog.setCancelable(true);
            dialog.show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}