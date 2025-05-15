package ru.mirea.yakovlev.mireaproject.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mirea.yakovlev.mireaproject.R;
import ru.mirea.yakovlev.mireaproject.ui.network.NetworkClient;
import ru.mirea.yakovlev.mireaproject.ui.network.User;
import ru.mirea.yakovlev.mireaproject.ui.network.UserResponse;

public class UsersFragment  extends Fragment {
    private TextView usersTextView;
    private ProgressBar progressBar;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NetworkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        usersTextView = view.findViewById(R.id.usersTextView);
        progressBar = view.findViewById(R.id.progressBar);
        loadUserData();
        return view;
    }
    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);
        NetworkClient.getService()
                .getUsers(10)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            displayUsers(response.body().getUsers());
                        } else {
                            usersTextView.setText("Ошибка загрузки данных");
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        usersTextView.setText("Ошибка сети: " + t.getMessage());
                    }
                });
    }

    private void displayUsers(List<User> users) {
        StringBuilder builder = new StringBuilder();
        for (User user : users) {
            builder.append("Имя: ").append(user.getFullName())
                    .append("\nEmail: ").append(user.getEmail())
                    .append("\nТелефон: ").append(user.getPhone())
                    .append("\n\n");
        }
        usersTextView.setText(builder.toString());
    }
}