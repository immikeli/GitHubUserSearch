package com.immikeli.githubusersearch.users;

import android.text.TextUtils;

import com.immikeli.githubusersearch.data.UserRepository;
import com.immikeli.githubusersearch.users.UsersContract.ActionsListener;

public class UsersPresenter implements ActionsListener {

    private final UserRepository mUserRepository;
    private final UsersContract.View mView;
    private int mPageIdx;
    private String mSearchKeyword;

    UsersPresenter(UserRepository userRepo, UsersContract.View view) {
        mUserRepository = userRepo;
        mView =  view;
    }

    @Override
    public void searchUser(String keyword) {
        if (TextUtils.isEmpty(keyword)) return;

        mPageIdx = 1;
        mSearchKeyword = keyword;
        searchUser();
    }

    @Override
    public void loadMoreUsers() {
        mPageIdx++;
        searchUser();
    }

    private void searchUser() {
        mView.setProgress(true);
        mUserRepository.searchUser(mPageIdx, mSearchKeyword, users -> {
            mView.setProgress(false);
            if (users == null) {
                mView.showLoadError();
            } else {
                if (users.size() == 0 && mPageIdx == 1) {
                    mView.showEmptyResult();
                } else {
                    mView.showUsers(users);
                }
            }
        });
    }

}
