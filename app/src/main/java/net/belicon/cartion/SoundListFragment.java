package net.belicon.cartion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import net.belicon.cartion.adapters.CategoryAdapter;
import net.belicon.cartion.adapters.MusicListAdapter;
import net.belicon.cartion.models.Category;
import net.belicon.cartion.models.CategoryList;
import net.belicon.cartion.models.Horn;
import net.belicon.cartion.models.HornList;
import net.belicon.cartion.models.User;
import net.belicon.cartion.retrofites.RetrofitInterface;
import net.belicon.cartion.retrofites.RetrofitUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SoundListFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RetrofitInterface mRetInterface;

    private TabLayout mSoundListTabLayout;
    private RecyclerView mSoundListRecyclerView;
    private Spinner mCategorySpinner;
    private ImageButton mSoundCustomHornLink;
//    private TextView mMoreBtn;
//    private MaterialSpinner mSoundListMaterialSpinner;

    private MusicListAdapter mAdapter;
    private CategoryAdapter mCategoryAdapter;
    private List<HornList> mMusicList;
    private List<String> mDownList;

    private String token, email, categoryPos, categoryName, type;
    private int offset = 0;
    private int limit = 20;
    private int total;

    public SoundListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sound_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSoundListTabLayout = view.findViewById(R.id.sound_list_tab_layout);
        mSoundListRecyclerView = view.findViewById(R.id.sound_list_recycler_view);
        mCategorySpinner = view.findViewById(R.id.sound_list_search_spinner);
        mSoundCustomHornLink = view.findViewById(R.id.sound_custom_horn_message);
//        mMoreBtn = view.findViewById(R.id.sound_list_more_btn);

        mRetInterface = RetrofitUtility.getRetrofitInterface();

        token = "Bearer " + User.getUserToken();
        if (mAuth.getCurrentUser() != null) {
            email = mAuth.getCurrentUser().getEmail();
        }

        view.findViewById(R.id.sound_list_search_btn).setOnClickListener(this);
        mSoundCustomHornLink.setOnClickListener(this);
//        mMoreBtn.setOnClickListener(this);

        type = "horn";
        mSoundListTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                mMusicList.clear();
                mAdapter.notifyDataSetChanged();
                if (pos == 0) {
                    type = "horn";
                    mSoundCustomHornLink.setVisibility(View.GONE);
                    onSoundResponse();
                } else {
                    type = "customHorn";
                    mSoundCustomHornLink.setVisibility(View.VISIBLE);
                    onMySoundResponse();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mRetInterface.getCategory(token)
                .enqueue(new Callback<Category>() {
                    @Override
                    public void onResponse(Call<Category> call, Response<Category> response) {
                        Log.e("Category CODE", "" + response.code());
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                List<CategoryList> dataList = response.body().getData().getCategoryList();
                                List<String> names = new ArrayList<>();
                                names.add("??????");
                                for (int i = 0; i < dataList.size(); i++) {
                                    names.add(dataList.get(i).getCategoryName());
                                }
                                mCategoryAdapter = new CategoryAdapter(names);
                                mCategorySpinner.setAdapter(mCategoryAdapter);
//                                mSoundListMaterialSpinner.setItems(names);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Category> call, Throwable t) {

                    }
                });

        categoryPos = "";
        mMusicList = new ArrayList<>();
        mDownList = new ArrayList<>();
        File file = new File(String.valueOf(getActivity().getExternalFilesDir(null)));
        File[] files = file.listFiles();
        for (File tempFile : files) {
            mDownList.add(tempFile.getName());
        }
        mRetInterface.getMusicList("Bearer " + User.getUserToken(), "" + offset, "" + limit, categoryPos)
                .enqueue(new Callback<Horn>() {
                    @Override
                    public void onResponse(Call<Horn> call, Response<Horn> response) {
                        Log.e("HORN CODE", "" + response.code());
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                total = response.body().getTotal();
                                List<HornList> item = response.body().getData().getHornList();
                                for (int i = 0; i < item.size(); i++) {
                                    mMusicList.add(new HornList(item.get(i).getHornId(), item.get(i).getHornName(), item.get(i).getCategoryName(), item.get(i).getWavPath(), item.get(i).getAdpcmPath()));
                                }
                                mAdapter = new MusicListAdapter(mRetInterface, mMusicList, mDownList, email, token, type, categoryName);
                                mSoundListRecyclerView.setAdapter(mAdapter);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Horn> call, Throwable t) {
                        Log.e("HORN FAILURE", t.getLocalizedMessage());
                    }
                });

        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    categoryPos = "";
                } else {
                    categoryPos = "" + position;
                }
                categoryName = mCategorySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSoundListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (itemTotalCount > 10) {
                    if (lastVisibleItemPosition <= total || total == 0) {
                        if (lastVisibleItemPosition == itemTotalCount) {
                            limit = limit + 10;
                            if (mSoundListTabLayout.getSelectedTabPosition() == 0) {
                                onSoundResponse();
                            } else {
                                onMySoundResponse();
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomMenuActivity.mHomeBtn.setChecked(false);
        BottomMenuActivity.mMusicBtn.setChecked(true);
        BottomMenuActivity.mMyPageBtn.setChecked(false);
        BottomMenuActivity.mMoreBtn.setChecked(false);
    }

    private void onSoundResponse() {
        mMusicList.clear();
        mRetInterface.getMusicList(token, "" + offset, "" + limit, categoryPos)
                .enqueue(new Callback<Horn>() {
                    @Override
                    public void onResponse(Call<Horn> call, Response<Horn> response) {
                        Log.e("HORN CODE", "" + response.code());
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                total = response.body().getTotal();
                                List<HornList> item = response.body().getData().getHornList();
                                for (int i = 0; i < item.size(); i++) {
                                    mMusicList.add(new HornList(item.get(i).getHornId(), item.get(i).getHornName(), item.get(i).getCategoryName(), item.get(i).getWavPath(), item.get(i).getAdpcmPath()));
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            categoryPos = "";
                        }
                    }

                    @Override
                    public void onFailure(Call<Horn> call, Throwable t) {
                        Log.e("HORN FAILURE", t.getLocalizedMessage());
                    }
                });
    }

    private void onMySoundResponse() {
        mMusicList.clear();
        mAdapter.notifyDataSetChanged();
        mRetInterface.getMyMusicList(token)
                .enqueue(new Callback<Horn>() {
                    @Override
                    public void onResponse(Call<Horn> call, Response<Horn> response) {
                        Log.e("C_HORN CODE", "" + response.code());
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                List<HornList> item = response.body().getData().getHornList();
                                for (int i = 0; i < item.size(); i++) {
                                    mMusicList.add(new HornList(item.get(i).getHornId(), item.get(i).getHornName(), item.get(i).getCategoryName(), item.get(i).getWavPath(), item.get(i).getAdpcmPath()));
                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), "????????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Horn> call, Throwable t) {
                        Log.e("C_HORN FAILURE", t.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sound_list_search_btn:
                mSoundListTabLayout.selectTab(mSoundListTabLayout.getTabAt(0));
                onSoundResponse();
//                mMoreBtn.setVisibility(View.VISIBLE);
                break;
            case R.id.sound_custom_horn_message:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cartion.co.kr/front/community/bbsList.do?bbsId=request")));
                break;
//            case R.id.sound_list_more_btn:
//                limit = 9999;
//                mMoreBtn.setVisibility(View.GONE);
//                if (mSoundListTabLayout.getSelectedTabPosition() == 0) {
//                    onSoundResponse();
//                } else {
//                    mSoundListTabLayout.selectTab(mSoundListTabLayout.getTabAt(0));
//                }
//                break;
        }
    }
}
