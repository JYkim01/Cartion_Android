package net.belicon.cartion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MoreFragment extends Fragment implements View.OnClickListener {

    public MoreFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.more_faq_btn).setOnClickListener(this);
        view.findViewById(R.id.more_cartion_learn_btn).setOnClickListener(this);
        view.findViewById(R.id.more_app_manual_btn).setOnClickListener(this);
        view.findViewById(R.id.more_notice_btn).setOnClickListener(this);
        view.findViewById(R.id.more_app_info_btn).setOnClickListener(this);
        view.findViewById(R.id.more_coupon_btn).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomMenuActivity.mHomeBtn.setChecked(false);
        BottomMenuActivity.mMusicBtn.setChecked(false);
        BottomMenuActivity.mMyPageBtn.setChecked(false);
        BottomMenuActivity.mMoreBtn.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_faq_btn:
                startActivity(new Intent(getActivity(), FaqActivity.class));
                break;
            case R.id.more_cartion_learn_btn:
                startActivity(new Intent(getActivity(), CartionLearnActivity.class));
                break;
            case R.id.more_app_manual_btn:
                startActivity(new Intent(getActivity(), AppManualActivity.class));
                break;
            case R.id.more_notice_btn:
                startActivity(new Intent(getActivity(), NoticeActivity.class));
                break;
            case R.id.more_app_info_btn:
                startActivity(new Intent(getActivity(), AppInformationActivity.class));
                break;
            case R.id.more_coupon_btn:
                break;
        }
    }
}
