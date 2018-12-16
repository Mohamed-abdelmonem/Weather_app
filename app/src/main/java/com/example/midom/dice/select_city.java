package com.example.midom.dice;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;

public class select_city extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        ConstraintLayout constraintLayout = (ConstraintLayout)findViewById(R.id.constraint);

        if (hour <= 6 || hour >= 18)
        {
            constraintLayout.setBackgroundResource(R.drawable.night);
        }
        else
            constraintLayout.setBackgroundResource(R.drawable.afternoon);
    }

    public void backtomain(View view) {
        EditText city_selected = (EditText)findViewById(R.id.editText);
        Intent home = new Intent(this , MainActivity.class);
        home.putExtra("city" , city_selected.getText().toString() );
        startActivity(home);
    }
}
