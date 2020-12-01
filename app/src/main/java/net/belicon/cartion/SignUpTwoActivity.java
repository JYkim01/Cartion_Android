package net.belicon.cartion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
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

        onSingUp = new SignUpPresenter(this, this, mAuth, mRetInterface);
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
                onEmailConfirmBtn();
                break;
            case R.id.join_password_edit:
                mJoinPwMessage.setVisibility(View.GONE);
                break;
            case R.id.join_password_confirm_message:
                mJoinPwConfirmMessage.setVisibility(View.GONE);
                break;
            case R.id.join_success_btn:
                String phone = mPhoneEdit.getText().toString();
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