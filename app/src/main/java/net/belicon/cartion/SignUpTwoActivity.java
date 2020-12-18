package net.belicon.cartion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import net.belicon.cartion.constantes.MainConstants;
import net.belicon.cartion.presenters.SignUpPresenter;
import net.belicon.cartion.retrofites.RetrofitInterface;
import net.belicon.cartion.retrofites.RetrofitUtility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpTwoActivity extends AppCompatActivity implements View.OnClickListener {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private MainConstants.OnSingUp onSingUp;

    private EditText mEmailEdit, mPasswordEdit, mPasswordConfirmEdit, mPhoneEdit;
    private TextView mJoinPwMessage, mJoinPwConfirmMessage;

    private InputMethodManager imm;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_sign_up_two);

        mEmailEdit = findViewById(R.id.join_email_edit);
        ImageButton mEmailConfirmBtn = findViewById(R.id.join_email_confirm_btn);
        mPasswordEdit = findViewById(R.id.join_password_edit);
        mPasswordConfirmEdit = findViewById(R.id.join_password_confirm_edit);
        mPhoneEdit = findViewById(R.id.join_phone_edit);
        mJoinPwMessage = findViewById(R.id.join_password_message);
        mJoinPwConfirmMessage = findViewById(R.id.join_password_confirm_message);

        mEmailConfirmBtn.setOnClickListener(this);
        mPasswordEdit.setOnClickListener(this);
        mPasswordConfirmEdit.setOnClickListener(this);
        findViewById(R.id.join_container).setOnClickListener(this);
        findViewById(R.id.join_success_btn).setOnClickListener(this);

        RetrofitInterface mRetInterface = RetrofitUtility.getRetrofitInterface();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        onSingUp = new SignUpPresenter(SignUpTwoActivity.this, this, mAuth, mRetInterface);

        mPasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 1) {
                    mJoinPwMessage.setVisibility(View.VISIBLE);
                } else {
                    if (s.length() > 5) {
                        mJoinPwMessage.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPasswordConfirmEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("PASSWORD TEXT", "CarSequence = " + s + " ,start = " + start + " ,before = " + before + " ,count = " + count);
                if (!mPasswordEdit.getText().toString().contentEquals(s)) {
                    mJoinPwConfirmMessage.setVisibility(View.VISIBLE);

                } else {
//                    if (mPasswordEdit.getText().toString().contentEquals(s)) {
                    mJoinPwConfirmMessage.setVisibility(View.GONE);
//                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPhoneEdit.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.join_container:
                imm.hideSoftInputFromWindow(mEmailEdit.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPasswordEdit.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPasswordConfirmEdit.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPhoneEdit.getWindowToken(), 0);
                break;
            case R.id.join_email_confirm_btn:
                Pattern p = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\\\-]{0,25})+");
                Matcher m = p.matcher(mEmailEdit.getText().toString());
                if (m.matches()) {
                    onEmailConfirmBtn();
                } else {
                    Toast.makeText(this, "Email 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.join_password_edit:
                mJoinPwMessage.setVisibility(View.GONE);
                break;
            case R.id.join_password_confirm_message:
                mJoinPwConfirmMessage.setVisibility(View.GONE);
                break;
            case R.id.join_success_btn:
                String phone = mPhoneEdit.getText().toString().replaceAll("-", "");
                String password = mPasswordEdit.getText().toString();
                String confirmPassword = mPasswordConfirmEdit.getText().toString();
                onSingUp.setOnJoin(mJoinPwMessage, mJoinPwConfirmMessage, email, password, phone, confirmPassword);
                break;
        }
    }

    private void onEmailConfirmBtn() {
        email = mEmailEdit.getText().toString();
        onSingUp.setOnEmailConfirm(email);
    }
}