package es.tfm.fishcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import es.tfm.fishcare.pojos.Hatchery;
import es.tfm.fishcare.pojos.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Signup extends AppCompatActivity {
    private OkHttpClient client = RestService.getClient();
    private EditText userName, password, rePassword, instanceName, email;
    private Button signUp;
    private Session session;
    private String jwt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        session = new Session(this);

        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        rePassword = findViewById(R.id.rePassword);
        instanceName = findViewById(R.id.instanceName);
        email = findViewById(R.id.email);
        signUp = findViewById(R.id.signupButton);

        userName.bringToFront();
        password.bringToFront();
        rePassword.bringToFront();
        instanceName.bringToFront();
        email.bringToFront();

        signUp.bringToFront();

        signUp.setOnClickListener(v -> {
            checkFormData();

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

        if (isEmpty(instanceName)) {
            instanceName.setError("Instance name is required");
        }

        if (userName.getError() != null | password.getError() != null | rePassword.getError() != null | email.getError() != null | instanceName.getError() != null) {
            return;
        }

        signUp(userName.getText().toString(), password.getText().toString(), email.getText().toString());
    }

    private void signUp(String userName, String password, String email) {
        HttpUrl.Builder urlBuilder = RestService.getUserUrlBuilder();
        urlBuilder.addPathSegment("signup");
        String url = urlBuilder.build().toString();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String json = "{\"userName\":\"" + userName
                + "\", \"password\":\"" + password
                + "\", \"email\":\"" + email
                + "\"}";

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        final Gson gson = new Gson();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                System.out.println(response);
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                User user = gson.fromJson(response.body().charStream(), User.class);
                jwt = user.getJwt();
                session.setJwt(jwt);

                createHatchery(user.getId(), instanceName.getText().toString());
            }
        });
    }

    private void createHatchery(Long userId, String instanceName) {
        HttpUrl.Builder urlBuilder = RestService.getHatcheryUrlBuilder();
        urlBuilder.addPathSegment("create");
        String url = urlBuilder.build().toString();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", userId.toString())
                .addFormDataPart("name", instanceName)
                .build();

        Request request = new Request.Builder().url(url).post(requestBody).header("Authorization", jwt).build();

        final Gson gson = new Gson();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                System.out.println(response);
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                Hatchery hatchery = gson.fromJson(response.body().charStream(), Hatchery.class);
                session.setHatcheryId(hatchery.getHatcheryId().toString());

                Intent intent = new Intent(getApplicationContext(), HatcheryConfig.class);
                startActivity(intent);
            }
        });
    }

}