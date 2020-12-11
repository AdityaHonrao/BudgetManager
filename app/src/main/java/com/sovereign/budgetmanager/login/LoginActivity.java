package com.sovereign.budgetmanager.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sovereign.budgetmanager.MainActivity;
import com.sovereign.budgetmanager.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText email, password;

    SpannableString string = new SpannableString("Or, Signup");
    ClickableSpan goTOSignUp = new ClickableSpan() {
        @Override
        public void onClick(@NonNull View widget) {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        check();

        string.setSpan(goTOSignUp, 4, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView redirectToSignUp = (TextView) findViewById(R.id.redirectToSignup);
        redirectToSignUp.setText(string);
        redirectToSignUp.setMovementMethod(LinkMovementMethod.getInstance());

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
    }


    public void Login(View view){
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            check();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // ...
                        }

                        // ...
                    }
                });
    }

    private void check(){
        if (mAuth.getCurrentUser()!=null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}
