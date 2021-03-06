package yohan.jkskingdom.com.jokesterskingdom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Yohan on 29/06/2018.
 */

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout inputEmail, inputPassword, inputName;
    private Button btnRegister;
    private TextView goToLogin;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    CheckInternet checkInternet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        inputEmail = findViewById(R.id.textInputLayout2);
        inputPassword = findViewById(R.id.textInputLayout3);
        inputName = findViewById(R.id.textInputLayoutName);
        btnRegister = findViewById(R.id.button);
        goToLogin = findViewById(R.id.textView2);


        //CHECK IF INTERNET WITH ASYNCTASK
        checkInternetMethod();

        //GO TO THE LOGIN ACTIVITY LAYOUT
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        //REGISTER USER IN FIREBASE
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = inputEmail.getEditText().getText().toString().trim();
                String password = inputPassword.getEditText().getText().toString().trim();
                final String username = inputName.getEditText().getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    StyleableToast.makeText(getApplicationContext(), getString(R.string.forget_name), Toast.LENGTH_SHORT, R.style.mytoast).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    StyleableToast.makeText(getApplicationContext(), getString(R.string.forget_mail), Toast.LENGTH_SHORT, R.style.mytoast).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    StyleableToast.makeText(getApplicationContext(), getString(R.string.forget_password), Toast.LENGTH_SHORT, R.style.mytoast).show();
                    return;
                }

                if (password.length() < 6) {
                    StyleableToast.makeText(getApplicationContext(), getString(R.string.password_short), Toast.LENGTH_SHORT, R.style.mytoast).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    StyleableToast.makeText(RegisterActivity.this, getString(R.string.fail_authentication) + task.getException(),
                                            Toast.LENGTH_SHORT, R.style.mytoast).show();
                                } else {

                                    //ADD THE NAME IN THE DB
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("username", username);


                                    firebaseFirestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {


                                        }
                                    });
                                    StyleableToast.makeText(getBaseContext(), getString(R.string.welcome_toast_message), Toast.LENGTH_SHORT, R.style.mytoast).show();
                                    startActivity(new Intent(RegisterActivity.this, JokesFeed.class));
                                    finish();
                                }
                            }
                        });

            }
        });

    }

    public void checkInternetMethod() {
        checkInternet = new CheckInternet(this);
        checkInternet.execute();
    }


}




