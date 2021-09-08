package es.tfm.fishcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import es.tfm.fishcare.main.MainActivity;
import es.tfm.fishcare.pojos.Hatchery;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {
    private OkHttpClient client = RestService.getClient();
    private EditText loginName, loginPassword;
    private Button login;
    private TextView textViewSignup;
    private ImageView loginFish;
    private Session session;
    private String jwt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new Session(this);

        loginName = findViewById(R.id.loginName);
        loginPassword = findViewById(R.id.loginPassword);
        login = findViewById(R.id.loginButton);
        textViewSignup = findViewById(R.id.textViewSignup);
        loginFish = findViewById(R.id.loginFish);

        loginName.bringToFront();
        loginPassword.bringToFront();
        loginFish.bringToFront();

        login.setOnClickListener(v -> checkFormData());

        textViewSignup.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Signup.class);
            startActivity(intent);
        });

        // Service worker to pull data from the backend...
        // ... and notify on background if there is any problem.
        PeriodicWorkRequest pullDataWorkRequest = new PeriodicWorkRequest.Builder(PullDataWorker.class,15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("Pull sensor data", ExistingPeriodicWorkPolicy.KEEP, pullDataWorkRequest);
    }

    boolean isEmpty(EditText field) {
        String text = field.getText().toString();
        return (TextUtils.isEmpty(text));
    }

    protected void checkFormData() {
        if (isEmpty(loginName)) {
            loginName.setError("Login name is required");
        }

        if (isEmpty(loginPassword)) {
            loginPassword.setError("Password is required");
        }

        if (loginPassword.getError() != null | loginPassword.getError() != null ) {
            return;
        }

        login(loginName.getText().toString(), loginPassword.getText().toString());
    }

    private void login(String userName, String password) {
        HttpUrl.Builder urlBuilder = RestService.getUserUrlBuilder();
        urlBuilder.addPathSegment("login");
        String url = urlBuilder.build().toString();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", userName)
                .addFormDataPart("password", password)
                .build();

        Request request = new Request.Builder().url(url).post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Login.this.runOnUiThread(() -> Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                System.out.println(response);
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                jwt = response.body().string();
                session.setJwt(jwt);
                getHatcheryId();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getHatcheryId() {
        HttpUrl.Builder urlBuilder = RestService.getHatcheryUrlBuilder();
        urlBuilder.addPathSegment("user");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).get().header("Authorization", jwt).build();

        final Gson gson = new Gson();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Login.this.runOnUiThread(() -> Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show());
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
            }
        });
    }

}