package net.belicon.cartion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;

import net.belicon.cartion.adapters.ImageMoreAdapter;
import net.belicon.cartion.models.AppManualList;
import net.belicon.cartion.models.ExpandData;
import net.belicon.cartion.models.Manual;
import net.belicon.cartion.models.User;
import net.belicon.cartion.retrofites.RetrofitInterface;
import net.belicon.cartion.retrofites.RetrofitUtility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppManualActivity extends AppCompatActivity {

    private RetrofitInterface mRetInterface;

    private RecyclerView mManualRecyclerView;
    private FrameLayout mManualProgress;

    private ImageMoreAdapter mAdapter;
    private List<String> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manual);

        mManualRecyclerView = findViewById(R.id.manual_recycler_view);
        mManualProgress = findViewById(R.id.manual_progress_bar);

        mRetInterface = RetrofitUtility.getRetrofitInterface();

        mDataList = new ArrayList<>();
        mRetInterface.getManual("Bearer " + User.getUserToken())
                .enqueue(new Callback<Manual>() {
                    @Override
                    public void onResponse(Call<Manual> call, Response<Manual> response) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                List<AppManualList> list = response.body().getData().getAppManualList();
                                for (int i = 0; i < list.size(); i++) {
                                    mDataList.add(list.get(i).getImageUrl());
                                }
                                mAdapter = new ImageMoreAdapter(mDataList);
                                mManualRecyclerView.setAdapter(mAdapter);
                                mManualProgress.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Manual> call, Throwable t) {

                    }
                });
    }
}