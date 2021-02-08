package es.tfm.fishcare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Signup extends AppCompatActivity {
    EditText userName, password, rePassword, serverAddress, instanceName, phoneNumber, email;
    Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        rePassword = findViewById(R.id.rePassword);
        serverAddress = findViewById(R.id.serverAddress);
        instanceName = findViewById(R.id.instanceName);
        phoneNumber = findViewById(R.id.phoneNumber);
        email = findViewById(R.id.email);
        signUp = findViewById(R.id.signupButton);

        userName.bringToFront();
        password.bringToFront();
        rePassword.bringToFront();
        serverAddress.bringToFront();
        instanceName.bringToFront();
        phoneNumber.bringToFront();
        email.bringToFront();

        signUp.bringToFront();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFormData();
            }
        });
    }

    boolean isEmail(EditText field) {
        String email = field.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean isEmpty(EditText field) {
        String text = field.getText().toString();
        return (TextUtils.isEmpty(text));
    }

    boolean checkPass(EditText password, EditText rePassword) {
        String passContent = password.getText().toString();
        String rePassContent = rePassword.getText().toString();

        return (passContent.equals(rePassContent));
    }

    protected void checkFormData() {
        if (isEmpty(userName)) {
            userName.setError("User name is required");
        }

        if (isEmpty(password)) {
            password.setError("Password is required");
        }

        if (isEmpty(rePassword)) {
            rePassword.setError("Password is required");
        }

        if (!isEmail(email)) {
            email.setError("This is not an email");
        }

        if (!checkPass(password, rePassword)) {
            password.setError("Password and Confirm password must have the same value");
            rePassword.setError("Password and Confirm password must have the same value");
        }

        if (isEmpty(serverAddress)) {
            serverAddress.setError("ServerAddress is required");
        }

    }
}