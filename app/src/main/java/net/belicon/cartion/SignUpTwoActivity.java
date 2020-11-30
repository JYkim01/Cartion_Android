package net.belicon.cartion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import net.belicon.cartion.models.Duplicate;
import net.belicon.cartion.models.Login;
import net.belicon.cartion.models.User;
import net.belicon.cartion.retrofites.RetrofitInterface;
import net.belicon.cartion.retrofites.RetrofitUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpTwoActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private RetrofitInterface mRetInterface;

    private EditText mEmailEdit, mPasswordEdit, mPasswordConfirmEdit, mPhoneEdit;
    private TextView mJoinPwMessage, mJoinPwConfirmMessage;
    private ImageButton mEmailConfirmBtn;

    private InputMethodManager imm;

    private String email, password, phone;
    private boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_two);

        mEmailEdit = findViewById(R.id.join_email_edit);
        mEmailConfirmBtn = findViewById(R.id.join_email_confirm_btn);
        mPasswordEdit = findViewById(R.id.join_password_edit);
        mPasswordConfirmEdit = findViewById(R.id.join_password_confirm_edit);
        mPhoneEdit = findViewById(R.id.join_phone_edit);
        mJoinPwMessage = findViewById(R.id.join_password_message);
        mJoinPwConfirmMessage = findViewById(R.id.join_password_confirm_message);

        mEmailConfirmBtn.setOnClickListener(this);
        mPasswordEdit.setOnClickListener(this);
        mPasswordConfirmEdit.setOnClickListener(this);
        findViewById(R.id.join_success_btn).setOnClickListener(this);

        mRetInterface = RetrofitUtility.getRetrofitInterface();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onClick(View v) {
        imm.hideSoftInputFromWindow(mEmailEdit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(mPasswordEdit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(mPasswordConfirmEdit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(mPhoneEdit.getWindowToken(), 0);
        switch (v.getId()) {
            case R.id.join_email_confirm_btn:
                onEmailConfirmBtn();
                break;
            case R.id.join_password_edit:
                mJoinPwMessage.setVisibility(View.GONE);
                break;
            case R.id.join_password_confirm_message:
                mJoinPwConfirmMessage.setVisibility(View.GONE);
                break;
            case R.id.join_success_btn:
                password = mPasswordEdit.getText().toString();
                if (password.length() < 6) {
                    mJoinPwMessage.setVisibility(View.VISIBLE);
                } else if (password.equals(mPasswordConfirmEdit.getText().toString())) {
                    if (isChecked) {
                        onCreateUser();
                    } else {
                        Toast.makeText(this, "아이디 중복체크를 해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mJoinPwConfirmMessage.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void onEmailConfirmBtn() {
        email = mEmailEdit.getText().toString();
        mRetInterface.getDuplicate(email)
                .enqueue(new Callback<Duplicate>() {
                    @Override
                    public void onResponse(Call<Duplicate> call, Response<Duplicate> response) {
                        if (response.code() == 200) {
                            isChecked = response.body().getData().getIsAvailable();
                            if (isChecked) {
                                Toast.makeText(SignUpTwoActivity.this, "사용하실 수 있습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpTwoActivity.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Duplicate> call, Throwable t) {

                    }
                });
    }

    private void onCreateUser() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            phone = mPhoneEdit.getText().toString();
                            mAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUpTwoActivity.this, "이메일을 확인해주세요.", Toast.LENGTH_SHORT).show();
                                                mRetInterface.postJoin(new User(email, password, phone))
                                                        .enqueue(new Callback<Login>() {
                                                            @Override
                                                            public void onResponse(Call<Login> call, Response<Login> response) {
                                                                if (response.code() == 201) {
                                                                    finish();
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<Login> call, Throwable t) {
                                                                Toast.makeText(SignUpTwoActivity.this, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        } else {
                            Log.e("SIGN UP", "회원가입 실패: " + task.getException());
                        }
                    }
                });
    }
}