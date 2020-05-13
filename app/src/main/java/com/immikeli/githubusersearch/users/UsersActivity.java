package com.immikeli.githubusersearch.users;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.immikeli.githubusersearch.R;
import com.immikeli.githubusersearch.data.User;
import com.immikeli.githubusersearch.data.UserRepositoryImpl;
import com.immikeli.githubusersearch.users.UsersContract.ActionsListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;

public class UsersActivity extends AppCompatActivity implements UsersContract.View {

    private ActionsListener mActionsListener = null;
    private List<User> mUsers = new ArrayList<>();
    private UsersAdapter mListAdapter = null;
    boolean mIsLoading = false;
    boolean mIsLastPage = false;

    @BindView(R.id.user_editor)
    EditText mUserEditor;
    @BindView(R.id.list)
    RecyclerView mListView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        ButterKnife.bind(this);
        mActionsListener = new UsersPresenter(new UserRepositoryImpl(new OkHttpClient()), this);
        mListAdapter = new UsersAdapter();
        mListView.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
        mListView.setAdapter(mListAdapter);
        mListView.setOnScrollChangeListener(new RecyclerView.OnScrollChangeListener() {

            private static final int PAGE_SIZE = 100;

            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) mListView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int[] firstVisibleItemPositions = layoutManager.findFirstVisibleItemPositions(null);
                if (firstVisibleItemPositions == null || firstVisibleItemPositions.length == 0) {
                    return;
                }

                int firstVisibleItemPosition = firstVisibleItemPositions[0];
                if (!mIsLoading && !mIsLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        mActionsListener.loadMoreUsers();
                    }
                }
            }
        });
        mSwipeRefreshLayout.setEnabled(false);
    }

    @OnClick(R.id.search_btn)
    public void onViewClicked() {
        mActionsListener.searchUser(mUserEditor.getText().toString().trim());
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mUserEditor.getWindowToken(), 0);
        mUsers.clear();
    }

    @Override
    public void setProgress(boolean active) {
        mIsLoading = active;
        mSwipeRefreshLayout.setRefreshing(active);
    }

    @Override
    public void showUsers(List<User> users) {
        if (users.size() == 0) {
            mIsLastPage = true;
            return;
        }

        mUsers.addAll(users);
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoadError() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), R.string.error_search_user, Snackbar.LENGTH_LONG);
        snackbar.setAction(android.R.string.ok, v -> snackbar.dismiss());
        snackbar.show();
    }

    @Override
    public void showEmptyResult() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), R.string.error_search_no_result, Snackbar.LENGTH_LONG);
        snackbar.setAction(android.R.string.ok, v -> snackbar.dismiss());
        snackbar.show();
    }

    class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
        private int baseViewSize = 0;
        private int baseAvatarSize = 0;
        private boolean isPrevBase = false;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (baseViewSize == 0) {
                baseViewSize = holder.itemView.getLayoutParams().width;
                baseAvatarSize = holder.avatar.getLayoutParams().width;
            }
            User user = mUsers.get(position);
            holder.info.setText(user.login);
            Glide.with(UsersActivity.this).load(user.avatar_url).centerCrop().into(holder.avatar);
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            // FIXME: should try to make 2 1x1 card side by side
            int randomType = ((int) (Math.random() * 10)) % 3;
            if (isPrevBase) {
                randomType = 0;
            }
            if (randomType == 0) {
                holder.itemView.getLayoutParams().width = baseViewSize;
                holder.itemView.getLayoutParams().height = baseViewSize;
                holder.avatar.getLayoutParams().width = baseAvatarSize;
                holder.avatar.getLayoutParams().height = baseAvatarSize;
                p.setFullSpan(false);
                isPrevBase = !isPrevBase;
            } else if (randomType == 1) {
                holder.itemView.getLayoutParams().width = baseViewSize * 2;
                holder.itemView.getLayoutParams().height = baseViewSize;
                holder.avatar.getLayoutParams().width = baseAvatarSize;
                holder.avatar.getLayoutParams().height = baseAvatarSize;
                p.setFullSpan(true);
            } else if (randomType == 2) {
                holder.itemView.getLayoutParams().width = baseViewSize * 2;
                holder.itemView.getLayoutParams().height = baseViewSize * 2;
                holder.avatar.getLayoutParams().width = baseAvatarSize * 2;
                holder.avatar.getLayoutParams().height = baseAvatarSize * 2;
                p.setFullSpan(true);
            }
        }

        @Override
        public int getItemCount() {
            return mUsers == null ? 0 : mUsers.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.info_text)
            TextView info;
            @BindView(R.id.img)
            ImageView avatar;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

    }

}