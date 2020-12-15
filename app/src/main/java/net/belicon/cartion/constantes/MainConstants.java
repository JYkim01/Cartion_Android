package net.belicon.cartion.constantes;

import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public interface MainConstants {

    interface OnSingUp {
        void setOnEmailConfirm(String email);
        void setOnJoin(TextView message1, TextView message2, String email, String password, String phone, String confirmPassword);
    }

    interface OnLogin {
        void setOnLogin(String email, String password, FrameLayout dialog);
    }
}
