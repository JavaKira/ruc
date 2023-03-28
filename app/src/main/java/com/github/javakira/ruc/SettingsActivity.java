package com.github.javakira.ruc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;

import com.github.javakira.ruc.model.Employee;
import com.github.javakira.ruc.model.SpinnerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SettingsActivity extends AppCompatActivity {
    private SpinnerFacade branchSpinnerFacade;
    private SpinnerFacade employeeSpinnerFacade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_first);

        List<SpinnerItem> items = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            items.add(new Employee("Филиал " + i, "4935b400-0858-11e0-8be3-005056bd3ce5"));
        }

        branchSpinnerFacade = new SpinnerFacade(
                findViewById(R.id.settings_first_branch_recycler),
                findViewById(R.id.settings_first_branch_title),
                findViewById(R.id.spinner_title),
                items.get(0),
                this,
                items
        );

        List<SpinnerItem> items1 = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            items1.add(new Employee("Работник " + i, "3c14c52e-132f-11ed-b15e-3cecef02455b"));
        }

        employeeSpinnerFacade = new SpinnerFacade(
                findViewById(R.id.settings_first_employee_recycler),
                findViewById(R.id.settings_first_employee_title),
                findViewById(R.id.settings_first_employee),
                items1.get(0),
                this,
                items1
        );

        CardView endButton = findViewById(R.id.settings_first_button);
        endButton.setOnClickListener(view -> {
            Properties properties = FileIO.loadProps("config.txt", this);
            properties.setProperty("branch", branchSpinnerFacade.getItem().getValue());
            properties.setProperty("employee", employeeSpinnerFacade.getItem().getValue());
            FileIO.writeProps("config.txt", this, properties);
        });
    }
}