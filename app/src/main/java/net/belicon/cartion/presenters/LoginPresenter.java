package net.belicon.cartion.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import net.belicon.cartion.BottomMenuActivity;
import net.belicon.cartion.LoginActivity;
import net.belicon.cartion.SignUpOneActivity;
import net.belicon.cartion.constantes.MainConstants;
import net.belicon.cartion.models.Login;
import net.belicon.cartion.models.User;
import net.belicon.cartion.retrofites.RetrofitInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter implements MainConstants.OnLogin {

    private FirebaseAuth mAuth;
    private RetrofitInterface retrofit;

    private Context context;
    private Activity activity;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public LoginPresenter(FirebaseAuth mAuth, RetrofitInterface retrofit, Context context, Activity activity, SharedPreferences preferences) {
        this.mAuth = mAuth;
        this.retrofit = retrofit;
        this.context = context;
        this.activity = activity;
        this.preferences = preferences;
    }

    @Override
    public void setOnLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onVerify(email, password);
                        } else {
                            Log.e("AUTH ERROR", task.getException().getLocalizedMessage() + "");
                        }
                    }
                });
    }

    private void onVerify(String email, String password) {
        if (mAuth.getCurrentUser().isEmailVerified()) {
            editor = preferences.edit();
            retrofit.postLogin(new User(email, password))
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
                                            context.startActivity(new Intent(context, BottomMenuActivity.class));
                                        } else {
                                            context.startActivity(new Intent(context, SignUpOneActivity.class));
                                        }
                                        activity.finish();
                                    }
                                }
                            } else {
                                Toast.makeText(context, "로그인을 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Login> call, Throwable t) {
                            Toast.makeText(context, "로그인을 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            mAuth.getCurrentUser().sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "이메일을 확인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
