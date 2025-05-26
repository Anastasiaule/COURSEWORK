package com.example.coursework;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.coursework.domain.entity.Project;
import com.example.coursework.domain.exceptions.ProjectException;
import com.example.coursework.domain.usecase.ProjectUseCase;
import com.example.coursework.repository.indatabase.DBMaterialCatalogRepository;
import com.example.coursework.repository.indatabase.DBProjectRepository;
import com.example.coursework.repository.indatabase.DBToolCatalogRepository;
import com.example.coursework.repository.storage.AppDatabase;
import com.example.coursework.repository.storage.RoomStorage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.SQLException;
import java.util.List;

public class ProjectsFragment extends Fragment {

    private ProjectUseCase projectUseCase;
    private ListView projectsListView;
    private ArrayAdapter<Project> projectsAdapter;

    public ProjectsFragment() {
        // Обязательный пустой конструктор
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_project, container, false);

        projectsListView = view.findViewById(R.id.projectsListView);
        FloatingActionButton fab = view.findViewById(R.id.fabAdd);

        fab.setOnClickListener(v -> showAddProjectDialog());

        AppDatabase db = Room.databaseBuilder(
                        requireContext(),
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

        loadProjects();

        projectsListView.setOnItemClickListener((parent, itemView, position, id) -> {
            Project selectedProject = (Project) parent.getItemAtPosition(position);

            Fragment detailsFragment = ProjectDetailsFragment.newInstance(selectedProject.getId());

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });



        projectsListView.setOnItemLongClickListener((parent, v, position, id) -> {
            Project selectedProject = (Project) parent.getItemAtPosition(position);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Удалить проект")
                    .setMessage("Вы уверены, что хотите удалить проект \"" + selectedProject.getName() + "\"?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        try {
                            projectUseCase.deleteProject(selectedProject);
                            loadProjects();
                            Toast.makeText(requireContext(), "Проект удалён", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Ошибка удаления: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Отмена", null)
                    .show();

            return true;
        });

        return view;
    }

    private void loadProjects() {
        List<Project> projects = projectUseCase.getAllProjects();
        projectsAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, projects);
        projectsListView.setAdapter(projectsAdapter);
    }

    private void showAddProjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
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
                Toast.makeText(requireContext(), "Проект " + project.getName() + " создан", Toast.LENGTH_SHORT).show();
            } catch (ProjectException e) {
                Toast.makeText(requireContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (SQLException e) {
                Toast.makeText(requireContext(), "Ошибка базы данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Отмена", null);
        builder.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProjects();
    }
}

