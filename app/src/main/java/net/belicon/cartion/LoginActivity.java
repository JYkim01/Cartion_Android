package net.belicon.cartion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import net.belicon.cartion.models.Login;
import net.belicon.cartion.models.Token;
import net.belicon.cartion.models.User;
import net.belicon.cartion.retrofites.RetrofitInterface;
import net.belicon.cartion.retrofites.RetrofitUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private RetrofitInterface mRetInterface;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private EditText mEmailEdit, mPasswordEdit;

    private InputMethodManager imm;

    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doFullScreen();
        setContentView(R.layout.activity_login);

        mEmailEdit = findViewById(R.id.login_email_edit);
        mPasswordEdit = findViewById(R.id.login_password_edit);

        findViewById(R.id.login_container).setOnClickListener(this);
        findViewById(R.id.login_btn).setOnClickListener(this);
        findViewById(R.id.login_forgot_password_btn).setOnClickListener(this);
        findViewById(R.id.login_sign_up_btn).setOnClickListener(this);

        mRetInterface = RetrofitUtility.getRetrofitInterface();

        preferences = getSharedPreferences("login_key", Context.MODE_PRIVATE);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onClick(View v) {
        imm.hideSoftInputFromWindow(mEmailEdit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(mPasswordEdit.getWindowToken(), 0);
        switch (v.getId()) {
            case R.id.login_btn:
                email = mEmailEdit.getText().toString();
                password = mPasswordEdit.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "이메일과 패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    onLogin();
                }
                break;
            case R.id.login_forgot_password_btn:
                break;
            case R.id.login_sign_up_btn:
                startActivity(new Intent(this, SignUpTwoActivity.class));
                break;
        }
    }

    private void onVerify() {
        if (mAuth.getCurrentUser().isEmailVerified()) {
            editor = preferences.edit();
            mRetInterface.postLogin(new User(email, password))
                    .enqueue(new Callback<Login>() {
                        @Override
                        public void onResponse(Call<Login> call, Response<Login> response) {
                            Log.e("LOGIN CODE", "" + response.code());
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    if (response.body().getData() != null) {
                                        String token = response.body().getData().getToken().getRefreshToken();
                                        Log.e("TOKEN", token);
                                        editor.putString("token", token);
                                        editor.apply();
                                        User.setUserToken(token);
                                        String eulaYn = response.body().getData().getEulaYn();
                                        if (eulaYn.equals("Y")) {
                                            startActivity(new Intent(LoginActivity.this, BottomMenuActivity.class));
                                        } else {
                                            startActivity(new Intent(LoginActivity.this, SignUpOneActivity.class));
                                        }
                                        finish();
                                    }
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "로그인을 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Login> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "로그인을 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            mAuth.getCurrentUser().sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "이메일을 확인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void onLogin() {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onVerify();
                        } else {
                            Log.e("AUTH ERROR", task.getException().getLocalizedMessage() + "");
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void doFullScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE|
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}