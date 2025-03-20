package ru.mirea.yakovlevma.dialog;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

public class MyDateDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public DatePickerDialog onCreateDialog(Bundle savedInstanceState) {
        int year = 2023;
        int month = 0; // 0 = январь
        int day = 1;
        return new DatePickerDialog(
                getActivity(),
                this,
                year,
                month,
                day
        );
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String date = "Выбрано: " + day + "." + (month + 1) + "." + year;
        Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                date,
                Snackbar.LENGTH_LONG
        ).show();
    }
}