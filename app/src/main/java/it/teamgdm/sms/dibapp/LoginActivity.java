package it.teamgdm.sms.dibapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonSignIn;
    private Button buttonRegister;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -onCreate-");
        super.onCreate(savedInstanceState);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);

        buttonSignIn = findViewById(R.id.sign_inButton);
        buttonRegister = findViewById(R.id.registerButton);

        disableBackButton();
        disableToolbar();
    }

    @Override
    int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected void onStart() {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -onStart-");
        super.onStart();

        buttonSignIn.setOnClickListener(buttonSignInListener);
        buttonRegister.setOnClickListener(buttonRegisterListener);
    }

    private final OnClickListener buttonSignInListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(Constants.TAG, getClass().getSimpleName() + " -OnClickListener-buttonSignInListener-onClick-");
            email = editTextEmail.getText().toString().toLowerCase().trim();
            password = editTextPassword.getText().toString().trim();
            loginInit();
        }
    };

    public void loginInit() {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -loginInit-");
        if(validateInputs() & login()) {
            Log.i(Constants.TAG, getClass().getSimpleName() + " -loginInit-Login ok");
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            mainActivityIntent.putExtra(Constants.USER_LOGIN, Constants.OK_CODE);
            mainActivityIntent.putExtra(Constants.KEY_USER_EMAIL, email);
            startActivity(mainActivityIntent);
            finish();
        } else {
            Log.i(Constants.TAG, getClass().getSimpleName() + " -loginInit-RESULT_CANCELED");
        }

    }

    private final OnClickListener buttonRegisterListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(Constants.TAG, getClass().getSimpleName() + " -OnClickListener-buttonRegisterListener-onClick-");
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
            finish();
        }
    };

    private boolean validateInputs() {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -validateInputs-");
        if(email.equals(Constants.KEY_EMPTY) ){
            editTextEmail.setError(getResources().getString(R.string.emailPromptHint) + " " + getResources().getString(R.string.inputCannotBeEmpty));
            editTextEmail.requestFocus();
            return false;
        }
        if(password.equals(Constants.KEY_EMPTY)){
            editTextPassword.setError(getResources().getString(R.string.passwordPromptHint) + " " + getResources().getString(R.string.inputCannotBeEmpty));
            editTextPassword.requestFocus();
            return false;
        }
        if(!isEmailValid(email)) {
            editTextEmail.setError(getResources().getString(R.string.emailNotValid));
            editTextEmail.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isEmailValid(CharSequence email) {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -isEmailValid-");
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean login() {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -login-");
        if(DAO.loginUser(email, password)) {
            Log.i(Constants.TAG, getClass().getSimpleName() + " -setAccess-Constants.OK-");
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.user_login_ok), Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Log.i(Constants.TAG, getClass().getSimpleName() + " -setAccess-NOT_OK-");
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.user_login_not_ok), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
