package es.tfm.fishcare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import es.tfm.fishcare.notifications.NotificationUtils;

public class Login extends AppCompatActivity {
    EditText loginName, loginPassword;
    Button login;
    TextView textViewSignup;
    ImageView loginFish;

    private NotificationUtils mNotificationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mNotificationUtils = new NotificationUtils(this);

        loginName = findViewById(R.id.loginName);
        loginPassword = findViewById(R.id.loginPassword);
        login = findViewById(R.id.loginButton);
        textViewSignup = findViewById(R.id.textViewSignup);
        loginFish = findViewById(R.id.loginFish);

        loginName.bringToFront();
        loginPassword.bringToFront();
        loginFish.bringToFront();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkFormData();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Signup.class);
                startActivity(intent);
            }
        });
    }

    boolean isEmpty(EditText field) {
        String text = field.getText().toString();
        return (TextUtils.isEmpty(text));
    }

    protected void checkFormData() {
        if (isEmpty(loginName)) {
            loginName.setError("Login name is required");
            final Intent intent = new Intent();
            Notification.Builder nb = mNotificationUtils.
                    createNotification("Credentials Error", "Login Name is required", intent);

            mNotificationUtils.getManager().notify(101, nb.build());
        }

        if (isEmpty(loginPassword)) {
            loginPassword.setError("Password is required");
        }

    }
}