package ru.mirea.yakovlevma.dialog;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.TimePicker;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

public class MyTimeDialogFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public TimePickerDialog onCreateDialog(Bundle savedInstanceState) {
        int hour = 12;
        int minute = 0;
        return new TimePickerDialog(
                getActivity(),
                this,
                hour,
                minute,
                true
        );
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        String time = "Выбрано: " + hour + ":" + minute;
        Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                time,
                Snackbar.LENGTH_LONG
        ).show();
    }
}