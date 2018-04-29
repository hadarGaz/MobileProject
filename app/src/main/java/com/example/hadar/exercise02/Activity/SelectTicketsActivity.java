package com.example.hadar.exercise02.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hadar.exercise02.R;

public class SelectTicketsActivity extends AppCompatActivity {

    private TextView m_textViewStandardPrice;
    private TextView m_textViewStudentPrice;
    private TextView m_textViewSoldierPrice;
    private Spinner m_spinnerstandard;
    private Spinner m_spinnerStudent;
    private Spinner m_spinnerSoldieer;
    private ArrayAdapter<CharSequence> m_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tickets);

        m_spinnerstandard = (Spinner)findViewById(R.id.SpinnerStandard);
        m_spinnerStudent= (Spinner)findViewById(R.id.SpinnerStudent);
        m_spinnerSoldieer= (Spinner)findViewById(R.id.SpinnerSoldieer);

        m_textViewStandardPrice= (TextView) findViewById(R.id.textViewStandardPrice);
        m_textViewStandardPrice.setText("2");
        m_textViewStudentPrice= (TextView) findViewById(R.id.textViewStudentPrice);
        m_textViewSoldierPrice= (TextView) findViewById(R.id.textViewSoldierPrice);

        m_adapter = ArrayAdapter.createFromResource(this,R.array.ArrayNumber,android.R.layout.simple_spinner_item );
        m_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_spinnerstandard.setAdapter(m_adapter);
        m_spinnerStudent.setAdapter(m_adapter);
        m_spinnerSoldieer.setAdapter(m_adapter);

        m_spinnerstandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(),parent.getItemAtPosition(position)+ "selected", Toast.LENGTH_LONG).show();
                int priceForStandard = Integer.parseInt(m_textViewStandardPrice.getText().toString()) *
                        Integer.parseInt(parent.getItemAtPosition(position).toString());
                m_textViewStandardPrice.setText(String.valueOf(priceForStandard));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        m_spinnerStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(),parent.getItemAtPosition(position)+ "selected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        m_spinnerSoldieer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(),parent.getItemAtPosition(position)+ "selected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
