package ru.mirea.yakovlev.employeedb;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText nameEditText, salaryEditText, superpowerEditText;
    private Button addButton, loadButton;
    private TextView resultTextView;
    private AppDatabase db;
    private EmployeeDao employeeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameEditText = findViewById(R.id.nameEditText);
        salaryEditText = findViewById(R.id.salaryEditText);
        superpowerEditText = findViewById(R.id.superpowerEditText);
        addButton = findViewById(R.id.addButton);
        loadButton = findViewById(R.id.loadButton);
        resultTextView = findViewById(R.id.resultTextView);

        db = App.getInstance().getDatabase();
        employeeDao = db.employeeDao();

        addButton.setOnClickListener(v -> addEmployee());
        loadButton.setOnClickListener(v -> loadEmployees());
    }

    private void addEmployee() {
        Employee employee = new Employee();
        employee.name = nameEditText.getText().toString();
        employee.salary = Integer.parseInt(salaryEditText.getText().toString());
        employee.superpower = superpowerEditText.getText().toString();

        employeeDao.insert(employee);

        Toast.makeText(this, "Герой добавлен", Toast.LENGTH_SHORT).show();
    }

    private void loadEmployees() {
        List<Employee> employees = employeeDao.getAll();
        StringBuilder sb = new StringBuilder();

        for (Employee employee : employees) {
            sb.append("ID: ").append(employee.id)
                    .append(", Имя: ").append(employee.name)
                    .append(", Зарплата: ").append(employee.salary)
                    .append(", Суперспособность: ").append(employee.superpower)
                    .append("\n\n");
        }

        resultTextView.setText(sb.toString());
    }
}