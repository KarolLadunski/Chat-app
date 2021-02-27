package com.example.android.chatapp.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.SupportActivity;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.android.chatapp.R;
import com.example.android.chatapp.SuggestionActivity;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        populateSetDate(year, month+1, day);
    }
    public void populateSetDate(int year, int month, int day) {
        TextView date = (TextView)getActivity().findViewById(R.id.date);
        date.setText(month+"/"+day+"/"+year);
    }
}