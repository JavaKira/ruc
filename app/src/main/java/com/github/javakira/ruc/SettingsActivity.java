package com.github.javakira.ruc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.javakira.ruc.model.Employee;
import com.github.javakira.ruc.model.SpinnerItem;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private SpinnerFacade branchSpinnerFacade;
    private SpinnerFacade employeeSpinnerFacade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_first);

        List<SpinnerItem> items = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            items.add(new Employee("Филиал " + i, "test"));
        }

        branchSpinnerFacade = new SpinnerFacade(
                findViewById(R.id.settings_first_branch_recycler),
                findViewById(R.id.settings_first_branch_title),
                findViewById(R.id.spinner_title),
                items.get(0),
                this,
                items
        );

        employeeSpinnerFacade = new SpinnerFacade(
                findViewById(R.id.settings_first_employee_recycler),
                findViewById(R.id.settings_first_employee_title),
                findViewById(R.id.settings_first_employee),
                items.get(0),
                this,
                items
        );
    }
}