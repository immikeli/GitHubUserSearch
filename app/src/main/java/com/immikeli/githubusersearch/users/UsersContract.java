package com.immikeli.githubusersearch.users;

import com.immikeli.githubusersearch.data.User;

import java.util.List;

public class UsersContract {

    interface View {

        void setProgress(boolean active);

        void showUsers(List<User> users);

        void showLoadError();

        void showEmptyResult();
    }

    interface ActionsListener {

        void searchUser(String keyword);

        void loadMoreUsers();

    }

}
