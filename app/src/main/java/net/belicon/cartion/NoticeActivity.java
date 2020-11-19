package net.belicon.cartion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.Toast;

import net.belicon.cartion.adapters.ExpandAdapter;
import net.belicon.cartion.models.ExpandData;
import net.belicon.cartion.models.Faq;
import net.belicon.cartion.models.FaqList;
import net.belicon.cartion.models.Notice;
import net.belicon.cartion.models.NoticeList;
import net.belicon.cartion.models.User;
import net.belicon.cartion.retrofites.RetrofitInterface;
import net.belicon.cartion.retrofites.RetrofitUtility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeActivity extends AppCompatActivity {

    private RetrofitInterface mRetInterface;

    private ExpandableListView mNoticeListView;
    private FrameLayout mNoticeProgress;

    private ExpandAdapter mAdapter;
    private List<ExpandData> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        mNoticeListView = findViewById(R.id.notice_expand_list);
        mNoticeProgress = findViewById(R.id.notice_progress_bar);

        mRetInterface = RetrofitUtility.getRetrofitInterface();

        mDataList = new ArrayList<>();
        mRetInterface.getNotice("Bearer " + User.getUserToken())
                .enqueue(new Callback<Notice>() {
                    @Override
                    public void onResponse(Call<Notice> call, Response<Notice> response) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                List<NoticeList> list = response.body().getData().getNoticeList();
                                if (list.size() == 0) {
                                    Toast.makeText(NoticeActivity.this, "등록된 공지사항이 없습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    ExpandData data = new ExpandData(list.get(0).getTitle());
                                    data.getChild().add(list.get(0).getBody());
                                    mDataList.add(data);
                                    mAdapter = new ExpandAdapter(mDataList);
                                    mNoticeListView.setAdapter(mAdapter);
                                    for (int i = 1; i < list.size(); i++) {
                                        data = new ExpandData(list.get(i).getTitle());
                                        data.getChild().add(list.get(i).getBody());
                                        mDataList.add(data);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                                mNoticeProgress.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Notice> call, Throwable t) {

                    }
                });
    }
}