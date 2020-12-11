package com.sovereign.budgetmanager.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.sovereign.budgetmanager.MainActivity;
import com.sovereign.budgetmanager.R;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText emailEditText, passwordEditText, passwordReEditText, usernameEditText;
    TextView signupError;

    SpannableString string = new SpannableString("Or, Login");
    ClickableSpan goTOSignUp = new ClickableSpan() {
        @Override
        public void onClick(@NonNull View widget) {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Initialize Firebase Auth
        check();

        mAuth = FirebaseAuth.getInstance();

        string.setSpan(goTOSignUp, 4, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView redirectToLogin = (TextView) findViewById(R.id.redirectToLogin);
        redirectToLogin.setText(string);
        redirectToLogin.setMovementMethod(LinkMovementMethod.getInstance());

        emailEditText = findViewById(R.id.signupEmail);
        passwordEditText = findViewById(R.id.signupPassword);
        passwordReEditText = findViewById(R.id.signupPasswordRe);
        usernameEditText = findViewById(R.id.signupUsername);
        signupError = findViewById(R.id.signupError);

    }

    public void Signup(View view){

        if(TextUtils.isEmpty(emailEditText.getText())){
            signupError.setText("Email Id is mandatory");
            return;
        }
        if(TextUtils.isEmpty(passwordEditText.getText())){
            signupError.setText("Password is mandatory");
            return;
        }
        if (!(passwordEditText.getText().equals(passwordReEditText.getText()))){
            signupError.setText("Passwords do not match");
            return;
        }
        if(passwordEditText.getText().length() < 6) {
            signupError.setText("Password should be longer than 6 characters");
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignUp", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(usernameEditText.getText().toString())
                                    .build();
                            user.updateProfile(userProfileChangeRequest);

                            check();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SignUp", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void check(){
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser()!=null){
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}
