package com.immikeli.githubusersearch.data;

import java.util.List;

public interface UserRepository {

    interface SearchUserCallback {

        void onResult(List<User> users);
    }

    void searchUser(int pageIdx, String keyword, SearchUserCallback callback);

}
