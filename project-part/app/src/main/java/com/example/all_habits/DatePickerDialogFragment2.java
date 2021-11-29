package com.example.all_habits;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

/**
 * Create the date picker for the create and edit/delete activity.
 */
public class DatePickerDialogFragment2 extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        //Gets the current YEAR,MONTH,and DAY
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        DatePickerDialog datePicker2 = new DatePickerDialog(getActivity(),(DatePickerDialog.OnDateSetListener) getActivity(),year, month, day);
        //Sets the date pickers minimum date.
        datePicker2.getDatePicker().setMinDate(System.currentTimeMillis());
        return datePicker2;
    }
}
