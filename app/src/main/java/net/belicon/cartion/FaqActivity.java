package net.belicon.cartion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.analyzer.VerticalWidgetRun;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.belicon.cartion.adapters.ExpandAdapter;
import net.belicon.cartion.models.ExpandData;
import net.belicon.cartion.models.Faq;
import net.belicon.cartion.models.FaqList;
import net.belicon.cartion.models.User;
import net.belicon.cartion.retrofites.RetrofitInterface;
import net.belicon.cartion.retrofites.RetrofitUtility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaqActivity extends AppCompatActivity {

    private RetrofitInterface mRetInterface;

    private ExpandableListView mFaqListView;
    private FrameLayout mFaqProgress;

    private ExpandAdapter mAdapter;
    private List<ExpandData> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        mFaqListView = findViewById(R.id.faq_expand_list);
        mFaqProgress = findViewById(R.id.faq_progress_bar);

        mRetInterface = RetrofitUtility.getRetrofitInterface();

        mDataList = new ArrayList<>();
        mRetInterface.getFaq("Bearer " + User.getUserToken())
                .enqueue(new Callback<Faq>() {
                    @Override
                    public void onResponse(Call<Faq> call, Response<Faq> response) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                List<FaqList> list = response.body().getData().getFaqList();
                                if (list.size() == 0) {
                                    Toast.makeText(FaqActivity.this, "등록된 FAQ가 없습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    ExpandData data = new ExpandData(list.get(0).getTitle());
                                    data.getChild().add(list.get(0).getBody());
                                    mDataList.add(data);
                                    mAdapter = new ExpandAdapter(mDataList);
                                    mFaqListView.setAdapter(mAdapter);
                                    for (int i = 1; i < list.size(); i++) {
                                        data = new ExpandData(list.get(i).getTitle());
                                        data.getChild().add(list.get(i).getBody());
                                        mDataList.add(data);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                                mFaqProgress.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Faq> call, Throwable t) {

                    }
                });
    }
}