package ru.mirea.yakovlev.mireaproject.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.mirea.yakovlev.mireaproject.BackgroundWorker;
import ru.mirea.yakovlev.mireaproject.R;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BackgroundTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BackgroundTaskFragment extends Fragment {
    private TextView statusText;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BackgroundTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BackgroundTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BackgroundTaskFragment newInstance(String param1, String param2) {
        BackgroundTaskFragment fragment = new BackgroundTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_background_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        statusText = view.findViewById(R.id.statusText);
        Button startButton = view.findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBackgroundTask();
            }
        });
    }

    private void startBackgroundTask() {
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(BackgroundWorker.class)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(requireContext()).enqueue(workRequest);

        WorkManager.getInstance(requireContext())
                .getWorkInfoByIdLiveData(workRequest.getId())
                .observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null) {
                            switch (workInfo.getState()) {
                                case ENQUEUED:
                                    statusText.setText("Статус: В очереди");
                                    break;
                                case RUNNING:
                                    statusText.setText("Статус: Выполняется");
                                    break;
                                case SUCCEEDED:
                                    statusText.setText("Статус: Успешно завершено");
                                    break;
                                case FAILED:
                                    statusText.setText("Статус: Ошибка");
                                    break;
                                default:
                                    statusText.setText("Статус: Неизвестно");
                            }
                        }
                    }
                });
    }
}
