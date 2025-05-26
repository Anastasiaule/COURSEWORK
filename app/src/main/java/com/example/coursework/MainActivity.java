package com.example.coursework;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.coursework.domain.entity.Project;
import com.example.coursework.domain.exceptions.ProjectException;
import com.example.coursework.domain.usecase.MaterialCatalogUseCase;
import com.example.coursework.domain.usecase.ProjectUseCase;
import com.example.coursework.domain.usecase.ToolCatalogUseCase;
import com.example.coursework.repository.indatabase.DBProjectRepository;
import com.example.coursework.repository.indatabase.DBMaterialCatalogRepository;
import com.example.coursework.repository.indatabase.DBToolCatalogRepository;
import com.example.coursework.repository.storage.AppDatabase;
import com.example.coursework.repository.storage.RoomStorage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.SQLException;
import java.util.List;

public class MainActivity extends BaseActivity  {
    private ProjectUseCase projectUseCase;
    private ListView projectsListView;
    private ArrayAdapter<Project> projectsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupMenu();
        setActiveMenuItem(R.id.nav_main);

        // Инициализация Room
        AppDatabase db = Room.databaseBuilder(
                        getApplicationContext(),
                        AppDatabase.class,
                        "coursework-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        AppDatabase.prepopulateData(db);

        RoomStorage storage = new RoomStorage(db);
        projectUseCase = new ProjectUseCase(
                new DBProjectRepository(storage),
                new DBMaterialCatalogRepository(storage),
                new DBToolCatalogRepository(storage)
        );

        // Инициализация UI
        projectsListView = findViewById(R.id.projectsListView);


        // Загрузка проектов
        loadProjects();



        // Обработчик выбора проекта
        projectsListView.setOnItemClickListener((parent, view, position, id) -> {
            Project selectedProject = (Project) parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, ProjectDetailsActivity.class);
            intent.putExtra("project_id", selectedProject.getId());
            startActivity(intent);
        });

        // Обработчик долгого нажатия — удаление проекта
        projectsListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Project selectedProject = (Project) parent.getItemAtPosition(position);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Удалить проект")
                    .setMessage("Вы уверены, что хотите удалить проект \"" + selectedProject.getName() + "\"?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        try {
                            projectUseCase.deleteProject(selectedProject);
                            loadProjects();
                            Toast.makeText(MainActivity.this, "Проект удалён", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Ошибка удаления: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Отмена", null)
                    .show();

            return true; // true — событие обработано
        });

    }

    private void loadProjects() {
        List<Project> projects = projectUseCase.getAllProjects();
        projectsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, projects);
        projectsListView.setAdapter(projectsAdapter);
    }

    public void showAddProjectDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_project, null);
        builder.setView(dialogView);

        EditText etProjectName = dialogView.findViewById(R.id.etProjectName);
        EditText etProjectType = dialogView.findViewById(R.id.etProjectType);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String name = etProjectName.getText().toString().trim();
            String type = etProjectType.getText().toString().trim();

            try {
                Project project = projectUseCase.createProject(name, type);
                loadProjects();
                Toast.makeText(this, "Проект " + project.getName() + " создан", Toast.LENGTH_SHORT).show();
            } catch (ProjectException e) {
                Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (SQLException e) {
                Toast.makeText(this, "Ошибка базы данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Отмена", null);
        builder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProjects();
    }
}