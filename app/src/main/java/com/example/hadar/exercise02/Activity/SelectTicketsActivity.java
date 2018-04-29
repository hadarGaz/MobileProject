package com.example.hadar.exercise02.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hadar.exercise02.R;
import com.example.hadar.exercise02.model.Movie;
import com.example.hadar.exercise02.model.UserDetails;

public class SelectTicketsActivity extends AppCompatActivity {

    private static final String TAG = "SelectTicketsActivity";
    private TextView m_textViewStandardPrice;
    private TextView m_textViewStudentPrice;
    private TextView m_textViewSoldierPrice;
    private TextView m_textViewTotalPriceStandard;
    private TextView m_textViewTotalPriceStudent;
    private TextView m_textViewTotalPriceSoldier;
    private TextView m_textViewTotalPriceForMovie;


    private Spinner m_spinnerstandard;
    private Spinner m_spinnerStudent;
    private Spinner m_spinnerSoldieer;
    private ArrayAdapter<CharSequence> m_adapter;
    private Movie m_movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tickets);

        findViews();
        getIntentInput();
        setPrices();
        setSpinnersWithAdapter();
    }

    private void findViews()
    {
        m_spinnerstandard = (Spinner)findViewById(R.id.SpinnerStandard);
        m_spinnerStudent= (Spinner)findViewById(R.id.SpinnerStudent);
        m_spinnerSoldieer= (Spinner)findViewById(R.id.SpinnerSoldieer);

        m_textViewStandardPrice= (TextView) findViewById(R.id.textViewStandardPrice);
        m_textViewStudentPrice= (TextView) findViewById(R.id.textViewStudentPrice);
        m_textViewSoldierPrice= (TextView) findViewById(R.id.textViewSoldierPrice);

        m_textViewTotalPriceForMovie = (TextView) findViewById(R.id.textViewTotalPriceForMovie);

        m_textViewTotalPriceStandard= (TextView) findViewById(R.id.textViewTotalPriceStandard);
        m_textViewTotalPriceStudent= (TextView) findViewById(R.id.textViewTotalPriceStudent);
        m_textViewTotalPriceSoldier= (TextView) findViewById(R.id.textViewTotalPriceSoldier);
    }
    private void getIntentInput()
    {
        Log.e(TAG, "getIntentInput() >> ");

        m_movie = (Movie) getIntent().getSerializableExtra("Movie");

        Log.e(TAG, "getIntentInput() << ");
    }

    private void setPrices()
    {
        m_textViewStandardPrice.setText(String.valueOf(m_movie.getM_price()));
        m_textViewStudentPrice.setText(String.valueOf(m_movie.getM_price()-5));
        m_textViewSoldierPrice.setText(String.valueOf(m_movie.getM_price()-10));
        m_textViewTotalPriceForMovie.setText("0.0");
    }

    private void setSpinnersWithAdapter()
    {
        m_adapter = ArrayAdapter.createFromResource(this,R.array.ArrayNumber,android.R.layout.simple_spinner_item );
        m_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_spinnerstandard.setAdapter(m_adapter);
        m_spinnerStudent.setAdapter(m_adapter);
        m_spinnerSoldieer.setAdapter(m_adapter);

        m_spinnerstandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                spinnerItemSelected("Standard",Double.valueOf(parent.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        m_spinnerStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                spinnerItemSelected("Student",Double.valueOf(parent.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        m_spinnerSoldieer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                spinnerItemSelected("Soldier",Double.valueOf(parent.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void spinnerItemSelected(String i_TicketType,double i_Quantity)
    {
        TextView textView = null;
        double pricePerTicketType = 0;
        switch (i_TicketType)
        {
            case "Standard":
                pricePerTicketType = (Double.valueOf(m_textViewStandardPrice.getText().toString()));
                textView = m_textViewTotalPriceStandard;
                break;
            case "Student":
                pricePerTicketType = (Double.valueOf(m_textViewStudentPrice.getText().toString()));
                textView = m_textViewTotalPriceStudent;
                break;
            case "Soldier":
                pricePerTicketType = (Double.valueOf(m_textViewSoldierPrice.getText().toString()));
                textView = m_textViewTotalPriceSoldier;
                break;
        }

        double priceTotalTicketType = pricePerTicketType * i_Quantity;
        if(textView != null)
            textView.setText(String.valueOf(priceTotalTicketType));

        double totalPrice = Double.valueOf(m_spinnerstandard.getSelectedItemPosition()) *
                Double.valueOf(m_textViewStandardPrice.getText().toString());
        totalPrice = totalPrice + (Double.valueOf(m_spinnerStudent.getSelectedItemPosition()) *
                Double.valueOf(m_textViewStudentPrice.getText().toString()));
        totalPrice = totalPrice + (Double.valueOf(m_spinnerSoldieer.getSelectedItemPosition()) *
                Double.valueOf(m_textViewSoldierPrice.getText().toString()));

        m_textViewTotalPriceForMovie.setText(String.valueOf(totalPrice));

    }
}
