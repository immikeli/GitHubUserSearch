package com.immikeli.githubusersearch.data;

import java.util.List;

public interface UserRepository {

    interface SearchUserCallback {

        void onResult(List<User> users);
    }

    void searchUser(String keyword, SearchUserCallback callback);

}
