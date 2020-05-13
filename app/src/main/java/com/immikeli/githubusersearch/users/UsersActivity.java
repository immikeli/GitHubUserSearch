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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;

public class UsersActivity extends AppCompatActivity implements UsersContract.View {

    private ActionsListener mActionsListener = null;
    private List<User> mUsers = new ArrayList<>();
    private UsersAdapter mListAdapter = null;

    @BindView(R.id.user_editor)
    EditText mUserEditor;
    @BindView(R.id.list)
    RecyclerView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        ButterKnife.bind(this);
        mActionsListener = new UsersPresenter(new UserRepositoryImpl(new OkHttpClient()), this);
        mListAdapter = new UsersAdapter();
        mListView.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
        mListView.setAdapter(mListAdapter);
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
    }

    @Override
    public void showUsers(List<User> users) {
        mUsers.addAll(users);
        mListAdapter.notifyDataSetChanged();
    }

    class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            User user = mUsers.get(position);
            holder.info.setText(user.login);
            Glide.with(UsersActivity.this).load(user.avatar_url).centerCrop().into(holder.avatar);
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
