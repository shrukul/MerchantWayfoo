package wayfoo.wayfoo.merchantwayfoo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import wayfoo.wayfoo.merchantwayfoo.R;

/**
 * Created by jahid on 12/10/15.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

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
        // Do something with the date chosen by the user
        Toast.makeText(getActivity(), "Year: "+view.getYear()+" Month: "+view.getMonth()+" Day: "+view.getDayOfMonth(), Toast.LENGTH_SHORT).show();
        Intent it = new Intent(getActivity(), OrderHistory.class);
        it.putExtra("date",view.getYear()+"-"+String.format("%02d",view.getMonth()+1) +'-'+String.format("%02d",view.getDayOfMonth()));
        startActivity(it);
        getActivity().finish();
    }
}