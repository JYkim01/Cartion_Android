package net.belicon.cartion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import net.belicon.cartion.adapters.ImageMoreAdapter;
import net.belicon.cartion.models.ExpandData;
import net.belicon.cartion.models.Learn;
import net.belicon.cartion.models.UseAppList;
import net.belicon.cartion.models.User;
import net.belicon.cartion.retrofites.RetrofitInterface;
import net.belicon.cartion.retrofites.RetrofitUtility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartionLearnActivity extends AppCompatActivity {

    private RetrofitInterface mRetInterface;

    private RecyclerView mLearnListView;
    private FrameLayout mLearnProgress;

    private ImageMoreAdapter mAdapter;
    private List<String> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartion_learn);

        mLearnListView = findViewById(R.id.learn_recycler_view);
        mLearnProgress = findViewById(R.id.learn_progress_bar);

        mRetInterface = RetrofitUtility.getRetrofitInterface();

        mDataList = new ArrayList<>();
        mRetInterface.getLearnList("Bearer " + User.getUserToken())
                .enqueue(new Callback<Learn>() {
                    @Override
                    public void onResponse(Call<Learn> call, Response<Learn> response) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                List<UseAppList> list = response.body().getData().getUseAppList();
                                for (int i = 0; i < list.size(); i++) {
                                    mDataList.add(list.get(i).getImageUrl());
                                }
                                mAdapter = new ImageMoreAdapter(mDataList);
                                mLearnListView.setAdapter(mAdapter);
                                mLearnProgress.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Learn> call, Throwable t) {

                    }
                });
    }
}