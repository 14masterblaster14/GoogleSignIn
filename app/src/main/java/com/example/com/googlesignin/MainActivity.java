package com.example.com.googlesignin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "@MasterBlaster";
    private static final int RES_CODE_SIGN_IN = 1001;
    String personPhotoUrl, id, idToken;
    private GoogleApiClient mGoogleApiClient;
    private TextView m_tvStatus, m_tvDispName, m_tvEmail, m_tvId, m_tvIdToken;
    private ImageView imgProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        m_tvStatus = (TextView) findViewById(R.id.tvStatus);
        m_tvDispName = (TextView) findViewById(R.id.tvDispName);
        m_tvEmail = (TextView) findViewById(R.id.tvEmail);
        m_tvId = (TextView) findViewById(R.id.tvId);
        m_tvIdToken = (TextView) findViewById(R.id.tvIdToken);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);

        findViewById(R.id.btnSignIn).setOnClickListener(this::click);
        findViewById(R.id.btnSignOut).setOnClickListener(this::click);
        findViewById(R.id.btnDisconnect).setOnClickListener(this::click);

        // TODO: Create a sign-in options object

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                //         .requestScopes(new Scope(Scope.))
                .build();


        // Build the GoogleApiClient object
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        // TODO: Customize the sign in button
        SignInButton signInButton = (SignInButton) findViewById(R.id.btnSignIn);
        signInButton.setSize(SignInButton.SIZE_WIDE);  // SIZE_STANDARD, SIZE_ICON_ONLY
        signInButton.setColorScheme(SignInButton.COLOR_DARK); // COLOR_AUTO, COLOR_LIGHT
        //     signInButton.setScopes(googleSignInOptions.getScopeArray());
    }

    private void click(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                startSignIn();
                break;
            case R.id.btnSignOut:
                signOut();
                break;
            case R.id.btnDisconnect:
                disconnect();
                break;
        }
    }


    private void startSignIn() {
        // TODO: Create sign-in intent and begin auth flow

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RES_CODE_SIGN_IN);
    }

    private void signOut() {
        // TODO: Sign the user out and update the UI

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

                m_tvStatus.setText(R.string.status_notsignedin);
                m_tvDispName.setText("");
                m_tvEmail.setText("");
                m_tvId.setText("");
                m_tvIdToken.setText("");
            }
        });
    }

    private void disconnect() {
        // TODO: Disconnect this account completely and update UI

        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

                m_tvStatus.setText(R.string.status_notconnected);
                m_tvDispName.setText("");
                m_tvEmail.setText("");
                m_tvId.setText("");
                m_tvIdToken.setText("");
            }
        });
    }

    private void signInResultHandler(GoogleSignInResult result) {

        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            m_tvStatus.setText(R.string.status_signedin);
            try {
                m_tvDispName.setText(account.getDisplayName());
                Log.i(TAG, "Name: " + account.getDisplayName());
                m_tvEmail.setText(account.getEmail());
                Log.i(TAG, "email: " + account.getEmail());
                id = account.getId().toString();
                Log.i(TAG, "Id : " + id);
                m_tvId.setText(id);
                idToken = account.getIdToken();
                Log.i(TAG, "IdToken : " + idToken);
                m_tvIdToken.setText(idToken);
                personPhotoUrl = account.getPhotoUrl().toString();
                Log.i(TAG, "ImageUrl: " + personPhotoUrl);
                Glide.with(getApplicationContext()).load(personPhotoUrl)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgProfilePic);

            } catch (NullPointerException e) {
                Log.d(TAG, "Error retrieving some account information");
                e.printStackTrace();
            }
        } else {
            Status status = result.getStatus();
            int statusCode = status.getStatusCode();
            m_tvStatus.setText(String.valueOf(statusCode));

            if (statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                m_tvStatus.setText(R.string.status_signincancelled);
            } else if (statusCode == GoogleSignInStatusCodes.SIGN_IN_FAILED) {
                m_tvStatus.setText(R.string.status_signinfail);
            } else {
                m_tvStatus.setText(R.string.status_nullresult);
            }

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RES_CODE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            signInResultHandler(result);
        }
    }

    // *************************************************
    // -------- GOOGLE PLAY SERVICES METHODS
    // *************************************************
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Could not connect to Google Play Services");
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

}
