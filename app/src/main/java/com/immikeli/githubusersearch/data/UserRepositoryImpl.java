package com.immikeli.githubusersearch.data;

import android.os.AsyncTask;

import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    public UserRepositoryImpl() {
    }

    @Override
    public void searchUser(String keyword, SearchUserCallback callback) {
        new SearchUserTask(keyword, callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class SearchUserTask extends AsyncTask<Void, Void, List<User>> {

        private final String keyword;
        private final SearchUserCallback callback;

        SearchUserTask(String keyword, SearchUserCallback callback) {
            this.keyword = keyword;
            this.callback = callback;
        }

        @Override
        protected List<User> doInBackground(Void... voids) {
            // GitHub search user API
            return null;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            callback.onResult(users);
        }
    }
}
