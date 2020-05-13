package com.immikeli.githubusersearch.data;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserRepositoryImpl implements UserRepository {
    private static final String TAG = UserRepositoryImpl.class.getSimpleName();

    private OkHttpClient mHttpClient;
    private SearchUserTask mSearchUserTask;

    public UserRepositoryImpl(OkHttpClient client) {
        mHttpClient = client;
    }

    @Override
    public void searchUser(int pageIdx, String keyword, SearchUserCallback callback) {
        if (mSearchUserTask != null) {
            mSearchUserTask.cancel(true);
        }
        mSearchUserTask = new SearchUserTask(pageIdx, keyword, callback);
        mSearchUserTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class SearchUserTask extends AsyncTask<Void, Void, List<User>> {

        private final int pageIdx;
        private final String keyword;
        private final SearchUserCallback callback;

        SearchUserTask(int pageIdx, String keyword, SearchUserCallback callback) {
            this.pageIdx = pageIdx;
            this.keyword = keyword;
            this.callback = callback;
        }

        @Override
        protected List<User> doInBackground(Void... voids) {
            Request request = new Request.Builder()
                    .url("https://api.github.com/search/users?q=" + keyword + "&page=" + pageIdx + "&per_page=100")
                    .build();

            try {
                Response response = mHttpClient.newCall(request).execute();
                JsonObject result = JsonParser.parseString(response.body().string()).getAsJsonObject();
                if (result.has("items")) {
                    List<User> users = new Gson().fromJson(result.getAsJsonArray("items"), new TypeToken<List<User>>(){}.getType());
                    return users;
                } else {
                    return new ArrayList<User>();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            if (isCancelled()) return;
            callback.onResult(users);
        }
    }
}
