package com.immikeli.githubusersearch.users;

import android.text.TextUtils;

import com.immikeli.githubusersearch.data.UserRepository;
import com.immikeli.githubusersearch.users.UsersContract.ActionsListener;

public class UsersPresenter implements ActionsListener {

    private final UserRepository mUserRepository;
    private final UsersContract.View mView;

    UsersPresenter(UserRepository userRepo, UsersContract.View view) {
        mUserRepository = userRepo;
        mView =  view;
    }

    @Override
    public void searchUser(String keyword) {
        if (TextUtils.isEmpty(keyword)) return;

        mView.setProgress(true);
        mUserRepository.searchUser(keyword, users -> {
            mView.setProgress(false);
            mView.showUsers(users);
        });
    }

}
