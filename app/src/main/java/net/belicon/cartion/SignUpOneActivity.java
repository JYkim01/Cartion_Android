package net.belicon.cartion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.belicon.cartion.models.Login;
import net.belicon.cartion.models.User;
import net.belicon.cartion.retrofites.RetrofitInterface;
import net.belicon.cartion.retrofites.RetrofitUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpOneActivity extends AppCompatActivity implements View.OnClickListener {

    private RetrofitInterface mRetInterface;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_one);

        findViewById(R.id.sign_one_next_btn).setOnClickListener(this);

        mRetInterface = RetrofitUtility.getRetrofitInterface();
    }

    @Override
    public void onClick(View v) {
        preferences = getSharedPreferences("login_key", Context.MODE_PRIVATE);
        editor = preferences.edit();
        mRetInterface.putAgreement("Bearer " + User.getUserToken())
                .enqueue(new Callback<Login>() {
                    @Override
                    public void onResponse(Call<Login> call, Response<Login> response) {
                        Log.e("AGREEMENT CODE", "" + response.code());
                        if (response.code() == 200) {
                            token = response.body().getData().getToken().getRefreshToken();
                            User.setUserToken(token);
                            editor.putString("token", token);
                            editor.apply();
                            startActivity(new Intent(SignUpOneActivity.this, BottomMenuActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Login> call, Throwable t) {
                        Toast.makeText(SignUpOneActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}