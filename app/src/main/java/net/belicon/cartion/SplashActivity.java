package net.belicon.cartion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;

import net.belicon.cartion.models.Login;
import net.belicon.cartion.models.RefreshToken;
import net.belicon.cartion.models.User;
import net.belicon.cartion.retrofites.RetrofitInterface;
import net.belicon.cartion.retrofites.RetrofitUtility;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RetrofitInterface mRetInterface;

    private VideoView mSplashVideo;
    private ImageView mSplashImage;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doFullScreen();
        setContentView(R.layout.activity_splash);

        mSplashVideo = findViewById(R.id.splash_video);
        mSplashImage = findViewById(R.id.splash_image);

        mRetInterface = RetrofitUtility.getRetrofitInterface();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(3)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                        RealmSchema schema = realm.getSchema();
                        RealmObjectSchema mUserMobileSchema = schema.get("UserMobile");
                        if (newVersion == 1) {
                            if (mUserMobileSchema != null) {
                                mUserMobileSchema.addField("categoryName", String.class, (FieldAttribute) null);
                            }

                            oldVersion++;
                        } else if (newVersion == 2) {
                            if (mUserMobileSchema != null) {
                                mUserMobileSchema.addField("seq", int.class, (FieldAttribute) null);
                            }

                            oldVersion++;
                        } else if (newVersion == 3) {
                            if (mUserMobileSchema != null) {
                                mUserMobileSchema.addField("wavPath", String.class, (FieldAttribute) null);
                            }

                            oldVersion++;
                        }
                    }
                })
                .build();
        Realm.setDefaultConfiguration(config);

        Handler mHandler = new Handler();
        if (mAuth.getCurrentUser() != null) {
            preferences = getSharedPreferences("login_key", Context.MODE_PRIVATE);
            editor = preferences.edit();
            if (preferences != null) {
                if (!preferences.getString("token", "").equals("")) {
                    token = preferences.getString("token", "");
                    Log.e("TOKEN", "Bearer " + token);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRetInterface.putToken(new RefreshToken("Bearer " + token))
                                    .enqueue(new Callback<Login>() {
                                        @Override
                                        public void onResponse(Call<Login> call, Response<Login> response) {
                                            Log.e("TOKEN", "" + response.code());
                                            if (response.code() == 200) {
                                                token = response.body().getData().getToken().getRefreshToken();
                                                User.setUserToken(token);
                                                editor.putString("token", token);
                                                editor.apply();
                                                startActivity(new Intent(SplashActivity.this, BottomMenuActivity.class));
                                            } else {
                                                Toast.makeText(SplashActivity.this, "다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                            }
                                            finish();
                                        }

                                        @Override
                                        public void onFailure(Call<Login> call, Throwable t) {

                                        }
                                    });
                        }
                    }, 2000);
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                }
            } else {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        } else {
            mSplashImage.setVisibility(View.GONE);

            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cartion_intro);
            mSplashVideo.setVideoURI(uri);

            mSplashVideo.start();
            mSplashVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            });
        }
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