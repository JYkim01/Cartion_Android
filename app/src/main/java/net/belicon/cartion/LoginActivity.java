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
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import net.belicon.cartion.constantes.MainConstants;
import net.belicon.cartion.models.Login;
import net.belicon.cartion.models.Token;
import net.belicon.cartion.models.User;
import net.belicon.cartion.presenters.LoginPresenter;
import net.belicon.cartion.retrofites.RetrofitInterface;
import net.belicon.cartion.retrofites.RetrofitUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private MainConstants.OnLogin onLogin;

    private EditText mEmailEdit, mPasswordEdit;
    private FrameLayout mDialog;

    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doFullScreen();
        setContentView(R.layout.activity_login);

        mEmailEdit = findViewById(R.id.login_email_edit);
        mPasswordEdit = findViewById(R.id.login_password_edit);
        mDialog = findViewById(R.id.login_dialog);

        findViewById(R.id.login_container).setOnClickListener(this);
        findViewById(R.id.login_btn).setOnClickListener(this);
        findViewById(R.id.login_forgot_password_btn).setOnClickListener(this);
        findViewById(R.id.login_sign_up_btn).setOnClickListener(this);

        RetrofitInterface mRetInterface = RetrofitUtility.getRetrofitInterface();

        SharedPreferences preferences = getSharedPreferences("login_key", Context.MODE_PRIVATE);

        onLogin = new LoginPresenter(mAuth, mRetInterface, LoginActivity.this, this, preferences);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onClick(View v) {
        imm.hideSoftInputFromWindow(mEmailEdit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(mPasswordEdit.getWindowToken(), 0);
        switch (v.getId()) {
            case R.id.login_btn:
                mDialog.setVisibility(View.VISIBLE);
                String email = mEmailEdit.getText().toString();
                String password = mPasswordEdit.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    mDialog.setVisibility(View.GONE);
                    Toast.makeText(this, "이메일과 패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    onLogin.setOnLogin(email, password, mDialog);
                }
                break;
            case R.id.login_forgot_password_btn:
                break;
            case R.id.login_sign_up_btn:
                startActivity(new Intent(this, SignUpTwoActivity.class));
                break;
        }
    }

    private void doFullScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}