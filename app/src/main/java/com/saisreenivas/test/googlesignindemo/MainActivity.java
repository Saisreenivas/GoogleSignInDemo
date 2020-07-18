package com.saisreenivas.test.googlesignindemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    RelativeLayout signedInLayout;
    SignInButton signInButton;
    Button signOutButton, deleteAccount;
    TextView name, email;
    ImageView image;

    GoogleApiClient googleApiClient;
    private static final int SIGN_IN_REQ_CODE = 0036;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signedInLayout = (RelativeLayout) findViewById(R.id.signed_in_layout);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signOutButton= (Button) findViewById(R.id.sign_out_button);
        name = (TextView) findViewById(R.id.sign_in_name);
        email = (TextView) findViewById(R.id.sign_in_email);
        image = (ImageView) findViewById(R.id.sign_in_image);
        deleteAccount = (Button) findViewById(R.id.delete_account);

        signedInLayout.setVisibility(View.INVISIBLE);
        signInButton.setVisibility(View.VISIBLE);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);

        signInButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
        deleteAccount.setOnClickListener(this);

        //initialize google signin options- default_signin helps in taking the basic details based
        // on (requested )email and build it
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        //send the googlesigninoptions into the googleAPI along with the onFailureListener
        // first the builder, then automanage, then  add the API and connect it to your required
        // fields(googleSignInOptions - all the required options)
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, SIGN_IN_REQ_CODE);
    }
    private void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }

    private void updateUI(boolean isLogin){
        if(isLogin){
            signedInLayout.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.INVISIBLE);
        }else{
            signedInLayout.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.VISIBLE);

        }
    }

    private void revokeAccess(){
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }

    private void handleresult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            String name_account = account.getDisplayName();
            String email_account = account.getEmail();
            String img_url_account = String.valueOf(account.getPhotoUrl());
            Log.v("img_url",  img_url_account);
            name.setText(name_account);
            email.setText(email_account);
            if(img_url_account.equals("null")){
                image.setImageDrawable(getResources().getDrawable(R.drawable.user_img));
            }else {
                Glide.with(this).load(img_url_account).into(image);
            }
            updateUI(true);
        }
        else{
            updateUI(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleresult(result);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.delete_account:
                revokeAccess();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
