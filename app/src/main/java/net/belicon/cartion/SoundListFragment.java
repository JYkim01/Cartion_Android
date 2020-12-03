package net.belicon.cartion;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
    private TextView mMoreBtn;
//    private MaterialSpinner mSoundListMaterialSpinner;

    private MusicListAdapter mAdapter;
    private CategoryAdapter mCategoryAdapter;
    private List<HornList> mMusicList;
    private List<String> mDownList;

    private String token, email, categoryPos, categoryName, type;
    private int offset = 0;
    private int limit = 10;

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
        mMoreBtn = view.findViewById(R.id.sound_list_more_btn);

        mRetInterface = RetrofitUtility.getRetrofitInterface();

        token = "Bearer " + User.getUserToken();
        if (mAuth.getCurrentUser() != null) {
            email = mAuth.getCurrentUser().getEmail();
        }

        view.findViewById(R.id.sound_list_search_btn).setOnClickListener(this);
        mMoreBtn.setOnClickListener(this);

        type = "horn";
        mSoundListTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if (pos == 0) {
                    type = "horn";
                    onSoundResponse();
                } else {
                    type = "customHorn";
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
                                names.add("전체");
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
                                List<HornList> item = response.body().getData().getHornList();
                                for (int i = 0; i < item.size(); i++) {
                                    mMusicList.add(new HornList(item.get(i).getHornId(), item.get(i).getHornName(), item.get(i).getWavPath(), item.get(i).getAdpcmPath()));
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
        mRetInterface.getMusicList("Bearer " + User.getUserToken(), "" + offset, "" + limit, categoryPos)
                .enqueue(new Callback<Horn>() {
                    @Override
                    public void onResponse(Call<Horn> call, Response<Horn> response) {
                        Log.e("HORN CODE", "" + response.code());
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                List<HornList> item = response.body().getData().getHornList();
                                for (int i = 0; i < item.size(); i++) {
                                    mMusicList.add(new HornList(item.get(i).getHornId(), item.get(i).getHornName(), item.get(i).getWavPath(), item.get(i).getAdpcmPath()));
                                }
                                mAdapter.notifyDataSetChanged();
                            }
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
        mRetInterface.getMyMusicList(token)
                .enqueue(new Callback<Horn>() {
                    @Override
                    public void onResponse(Call<Horn> call, Response<Horn> response) {
                        Log.e("HORN CODE", "" + response.code());
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                List<HornList> item = response.body().getData().getHornList();
                                for (int i = 0; i < item.size(); i++) {
                                    mMusicList.add(new HornList(item.get(i).getHornId(), item.get(i).getHornName(), item.get(i).getWavPath(), item.get(i).getAdpcmPath()));
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Horn> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sound_list_search_btn:
                limit = 10;
                mSoundListTabLayout.selectTab(mSoundListTabLayout.getTabAt(0));
                onSoundResponse();
                mMoreBtn.setVisibility(View.VISIBLE);
                break;
            case R.id.sound_list_more_btn:
                limit = 9999;
                mMoreBtn.setVisibility(View.GONE);
                if (mSoundListTabLayout.getSelectedTabPosition() == 0) {
                    onSoundResponse();
                } else {
                    mSoundListTabLayout.selectTab(mSoundListTabLayout.getTabAt(0));
                }
                break;
        }
    }
}
