package com.skysoftlk.vpnapp.Activities;

import android.content.Intent;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skysoftlk.vpnapp.R;
import com.skysoftlk.vpnapp.Utils.ChinaUtils;
import com.skysoftlk.vpnapp.Utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import top.oneconnectapi.app.api.OneConnect;

public class SplashScreen extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        coordinatorLayout = findViewById(R.id.cordi);

        // Detect if we are in China to adjust services
        boolean inChina = ChinaUtils.isLikelyInChina(this);
        Log.d("SplashScreen", "Likely in China: " + inChina);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    OneConnect oneConnect = new OneConnect();
                    oneConnect.initialize(SplashScreen.this, "K.F.P5RToLk5TXNPi2.4P6edjtY0gyfhZUAO.CWkB1KYhs4I4w"); // Put Your OneConnect Api Key
                    
                    int retryCount = 0;
                    int maxRetries = 3;
                    boolean success = false;
                    
                    while (retryCount < maxRetries && !success) {
                        try {
                            Constants.FREE_SERVERS = oneConnect.fetch(true);
                            Constants.PREMIUM_SERVERS = oneConnect.fetch(false);
                            success = true;
                        } catch (IOException e) {
                            retryCount++;
                            if (retryCount >= maxRetries) {
                                e.printStackTrace();
                            } else {
                                try {
                                    Thread.sleep(2000); // Wait 2 seconds before retry
                                } catch (InterruptedException ie) {
                                    ie.printStackTrace();
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        // Initialize Firebase Auth for anonymous sign-in to handle "Permission Denied" errors
        // if rules require authentication.
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            signInAnonymouslyWithRetry(mAuth, 0, 3);
        } else {
            initializeFirebaseDatabase();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Utility.isOnline(getApplicationContext())) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Check internet connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    startActivity(new Intent(SplashScreen.this, IntroActivity.class));
                    finish();
                }
            }
        }, 6000); // Increased delay to ensure Firebase data is fetched
    }

    private void signInAnonymouslyWithRetry(FirebaseAuth mAuth, int currentRetry, int maxRetries) {
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "signInAnonymously:success");
                initializeFirebaseDatabase();
            } else {
                Exception e = task.getException();
                Log.w("Firebase", "signInAnonymously:failure. Attempt " + (currentRetry + 1) + " of " + maxRetries, e);
                
                if (e instanceof com.google.firebase.FirebaseNetworkException && currentRetry < maxRetries) {
                    // Retry with exponential backoff or simple delay
                    new Handler().postDelayed(() -> signInAnonymouslyWithRetry(mAuth, currentRetry + 1, maxRetries), 2000);
                } else {
                    Log.e("Firebase", "signInAnonymously:final failure. Make sure Anonymous Auth is enabled in Firebase Console.");
                    // Even if sign-in fails, try to initialize (rules might be public)
                    initializeFirebaseDatabase();
                }
            }
        });
    }

    private void initializeFirebaseDatabase() {
        // IMPORTANT: If you get "Permission denied", check your Firebase Console -> Realtime Database -> Rules.
        // Make sure you have ".read": true or ".read": "auth != null" (if using Anonymous Auth).
        // Also ensure Anonymous Authentication is ENABLED in the Firebase Console under Authentication -> Sign-in method.
        
        // Firebase initialization with the correct region URL to prevent redirection/ANRs
        // Suggested URL from logs: https://skysoftvpn-default-rtdb.asia-southeast1.firebasedatabase.app
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://skysoftvpn-default-rtdb.asia-southeast1.firebasedatabase.app");

        // China resilience: If in China, set a shorter timeout or skip Firebase wait if cached data exists
        if (ChinaUtils.isLikelyInChina(this)) {
            // You might want to skip long waits here
            Log.d("SplashScreen", "Applying China-specific Firebase strategy");
        }

        DatabaseReference typeRef = database.getReference("type");
        DatabaseReference indratech_toto_27640849_admob_id = database.getReference("indratech_toto_27640849_admob_id");
        DatabaseReference indratech_toto_27640849_ad_banner = database.getReference("indratech_toto_27640849_ad_banner");
        DatabaseReference indratech_toto_27640849_aad_native = database.getReference("indratech_toto_27640849_aad_native");
        DatabaseReference indratech_toto_27640849_fb_native = database.getReference("indratech_toto_27640849_fb_native");
        DatabaseReference indratech_toto_27640849_fb_interstitial = database.getReference("indratech_toto_27640849_fb_interstitial");
        DatabaseReference indratech_toto_27640849_ad_interstitial = database.getReference("indratech_toto_27640849_ad_interstitial");
        DatabaseReference indratech_toto_27640849_admob_reward = database.getReference("indratech_toto_27640849_admob_reward");
        DatabaseReference indratech_toto_27640849_fb_reward = database.getReference("indratech_toto_27640849_fb_reward");
        DatabaseReference indratech_toto_27640849_all_ads_on_off = database.getReference("indratech_toto_27640849_all_ads_on_off");
        DatabaseReference copyright_ivpnofficial_dont_change_the_value = database.getReference("copyright_ivpnofficial_dont_change_the_value");

        String TAG = "Firebase";

        indratech_toto_27640849_all_ads_on_off.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);


                MainActivity.indratech_toto_27640849_all_ads_on_off = value != null && value.equalsIgnoreCase("on");

                Log.d(TAG,"indratech_toto_27640849_all_ads_on_off "+value);
                Log.d(TAG,"indratech_toto_27640849_all_ads_on_off "+MainActivity.indratech_toto_27640849_all_ads_on_off);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                MainActivity.indratech_toto_27640849_all_ads_on_off = false;
                if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                    Log.e(TAG, "Firebase Permission Denied! Check your Realtime Database Rules and enable Anonymous Auth in Firebase Console.");
                }
                Log.w(TAG, "Failed to read value. Error: " + error.getMessage(), error.toException());
            }
        });

        typeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                MainActivity.type = value;
                Log.d(TAG,"Type"+value);
                Log.d(TAG,"Type"+MainActivity.type);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                    Log.e(TAG, "Firebase Permission Denied! Check your Realtime Database Rules and enable Anonymous Auth in Firebase Console.");
                }
                Log.w(TAG, "Failed to read value. Error: " + error.getMessage(), error.toException());
            }
        });

        indratech_toto_27640849_aad_native.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                MainActivity.indratech_toto_27640849_aad_native_id = value;
                Log.d(TAG,"Native"+value);
                Log.d(TAG,"Native"+MainActivity.indratech_toto_27640849_aad_native_id);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                    Log.e(TAG, "Firebase Permission Denied! Check your Realtime Database Rules and enable Anonymous Auth in Firebase Console.");
                }
                Log.w(TAG, "Failed to read value. Error: " + error.getMessage(), error.toException());
            }
        });


        indratech_toto_27640849_admob_id.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                MainActivity.indratech_toto_27640849_admob_id = value;
                Log.d(TAG,"Admob ID"+value);
                Log.d(TAG,"Admob ID"+MainActivity.indratech_toto_27640849_admob_id);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                    Log.e(TAG, "Firebase Permission Denied! Check your Realtime Database Rules and enable Anonymous Auth in Firebase Console.");
                }
                Log.w(TAG, "Failed to read value. Error: " + error.getMessage(), error.toException());
            }
        });


        indratech_toto_27640849_ad_banner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                MainActivity.indratech_toto_27640849_ad_banner_id = value;
                Log.d(TAG,"Admob Banner"+value);
                Log.d(TAG,"Admob Banner"+MainActivity.indratech_toto_27640849_ad_banner_id);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                    Log.e(TAG, "Firebase Permission Denied! Check your Realtime Database Rules and enable Anonymous Auth in Firebase Console.");
                }
                Log.w(TAG, "Failed to read value. Error: " + error.getMessage(), error.toException());
            }
        });

        // Read from the database
        indratech_toto_27640849_ad_interstitial.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                MainActivity.admob_interstitial_id = value;
                Log.d(TAG,"Admob interstitial"+value);
                Log.d(TAG,"Admob interstitial"+MainActivity.admob_interstitial_id);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                    Log.e(TAG, "Firebase Permission Denied! Check your Realtime Database Rules and enable Anonymous Auth in Firebase Console.");
                }
                Log.w(TAG, "Failed to read value. Error: " + error.getMessage(), error.toException());
            }
        });

        // Read from the database
        indratech_toto_27640849_fb_native.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                MainActivity.indratech_toto_27640849_fb_native_id = value;
                Log.d(TAG,"indratech_toto_27640849_fb_native"+value);
                Log.d(TAG,"indratech_toto_27640849_fb_native"+MainActivity.indratech_toto_27640849_fb_native_id);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                    Log.e(TAG, "Firebase Permission Denied! Check your Realtime Database Rules and enable Anonymous Auth in Firebase Console.");
                }
                Log.w(TAG, "Failed to read value. Error: " + error.getMessage(), error.toException());
            }
        });

        // Read from the database
        indratech_toto_27640849_fb_interstitial.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                MainActivity.indratech_toto_27640849_fb_interstitial_id = value;
                Log.d(TAG,"indratech_toto_27640849_fb_interstitial"+value);
                Log.d(TAG,"indratech_toto_27640849_fb_interstitial"+MainActivity.indratech_toto_27640849_fb_interstitial_id);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                    Log.e(TAG, "Firebase Permission Denied! Check your Realtime Database Rules and enable Anonymous Auth in Firebase Console.");
                }
                Log.w(TAG, "Failed to read value. Error: " + error.getMessage(), error.toException());
            }
        });

        // Read from the database
        indratech_toto_27640849_fb_reward.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                MainActivity.indratech_toto_27640849_fb_reward_id = value;
                Log.d(TAG,"indratech_toto_27640849_fb_reward"+value);
                Log.d(TAG,"indratech_toto_27640849_fb_reward"+MainActivity.indratech_toto_27640849_fb_reward_id);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                    Log.e(TAG, "Firebase Permission Denied! Check your Realtime Database Rules and enable Anonymous Auth in Firebase Console.");
                }
                Log.w(TAG, "Failed to read value. Error: " + error.getMessage(), error.toException());
            }
        });

        // Read from the database
        indratech_toto_27640849_admob_reward.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                MainActivity.indratech_toto_27640849_admob_reward = value;
                Log.d(TAG,"indratech_toto_27640849_admob_reward"+value);
                Log.d(TAG,"indratech_toto_27640849_admob_reward"+MainActivity.indratech_toto_27640849_admob_reward);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                    Log.e(TAG, "Firebase Permission Denied! Check your Realtime Database Rules and enable Anonymous Auth in Firebase Console.");
                }
                Log.w(TAG, "Failed to read value. Error: " + error.getMessage(), error.toException());
            }
        });

        copyright_ivpnofficial_dont_change_the_value.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                MainActivity.copyright_ivpnofficial_dont_change_the_value = value;
                Log.d(TAG,"copyright_ivpnofficial_dont_change_the_value"+value);
                Log.d(TAG,"copyright_ivpnofficial_dont_change_the_value"+MainActivity.copyright_ivpnofficial_dont_change_the_value);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                    Log.e(TAG, "Firebase Permission Denied! Check your Realtime Database Rules and enable Anonymous Auth in Firebase Console.");
                }
                Log.w(TAG, "Failed to read value. Error: " + error.getMessage(), error.toException());
            }
        });
    }
}
