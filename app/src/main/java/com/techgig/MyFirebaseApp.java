package com.techgig;

/**
 * Created by Vishal Mago on 05/06/2017.
 */

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Hp on 5/5/2017.
 */
public class MyFirebaseApp extends android.app.Application{

        @Override
        public void onCreate() {
            super.onCreate();
            /* Enable disk persistence  */
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
}