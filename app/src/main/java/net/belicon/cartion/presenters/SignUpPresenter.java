package net.belicon.cartion.presenters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import net.belicon.cartion.constantes.MainConstants;
import net.belicon.cartion.models.Duplicate;
import net.belicon.cartion.models.Login;
import net.belicon.cartion.models.User;
import net.belicon.cartion.retrofites.RetrofitInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpPresenter implements MainConstants.OnSingUp {

    private FirebaseAuth mAuth;
    private RetrofitInterface retrofit;

    private Context context;
    private Activity activity;

    private boolean isChecked = false;

    public SignUpPresenter(Context context, Activity activity, FirebaseAuth auth, RetrofitInterface retrofit) {
        this.context = context;
        this.activity = activity;
        this.mAuth = auth;
        this.retrofit = retrofit;
    }

    @Override
    public void setOnEmailConfirm(String email) {
        retrofit.getDuplicate(email)
                .enqueue(new Callback<Duplicate>() {
                    @Override
                    public void onResponse(Call<Duplicate> call, Response<Duplicate> response) {
                        isChecked = response.body().getData().getIsAvailable();
                        if (isChecked) {
                            isChecked = true;
                            Toast.makeText(context, "사용하실 수 있습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            isChecked = false;
                            Toast.makeText(context, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Duplicate> call, Throwable t) {

                    }
                });
    }

    @Override
    public void setOnJoin(TextView message1, TextView message2, String email, String password, String phone, String confirmPassword) {
        if (message1.getVisibility() == View.VISIBLE) {
            Toast.makeText(context, "비밀번호는 6자리 이상 입력해 주세요.", Toast.LENGTH_SHORT).show();
        } else if (password.equals(confirmPassword)) {
            if (isChecked) {
                onCreateUser(email, password, phone);
            } else {
                Toast.makeText(context, "아이디 중복체크를 해주세요.", Toast.LENGTH_SHORT).show();
            }
        } else if (message2.getVisibility() == View.VISIBLE){
            Toast.makeText(context, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void onCreateUser(String email, String password, String phone) {
        retrofit.postJoin(new User(email, password, phone))
                .enqueue(new Callback<Login>() {
                    @Override
                    public void onResponse(Call<Login> call, Response<Login> response) {
                        Log.e("SignUp", response.code() + "");
                        if (response.code() == 201 || response.code() == 200) {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                mAuth.getCurrentUser().sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(context, "이메일 인증 메일을 발송하였습니다. 확인 부탁드립니다.", Toast.LENGTH_LONG).show();
                                                                    activity.finish();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Log.e("SIGN UP", "회원가입 실패: " + task.getException());
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(context, "회원가입이 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Login> call, Throwable t) {
                        Toast.makeText(context, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
