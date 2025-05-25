package com.example.coursework;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.coursework.domain.entity.*;
import com.example.coursework.domain.usecase.*;
import com.example.coursework.repository.dto.*;
import com.example.coursework.repository.indatabase.*;
import com.example.coursework.repository.storage.*;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailsActivity extends AppCompatActivity {

    private ProjectUseCase projectUseCase;
    private MaterialCatalogUseCase materialUseCase;
    private ToolCatalogUseCase toolUseCase;
    private DBProjectRepository projectRepo;
    private int projectId;
    private Project currentProject;

        // Объявляем переменные, соответствующие XML
        private ListView lvMaterials;
        private ListView lvTools;
        private Button btnAddMaterial;
        private Button btnAddTool;
        private TextView tvProjectName;
        private TextView tvProjectType;

        private ArrayAdapter<MaterialCatalog> materialsAdapter;
    private ArrayAdapter<ToolCatalog> toolsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);
        // Получаем ID проекта
        projectId = getIntent().getIntExtra("project_id", -1);
        if (projectId == -1) {
            Toast.makeText(this, "Неверный ID проекта", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Инициализация UI
        initViews();

        // Инициализация базы данных
        initDatabase();

        // Загрузка данных проекта
        loadProjectData();
        

        // Долгое нажатие на материал — удаление
        lvMaterials.setOnItemLongClickListener((parent, view, position, id) -> {
            MaterialCatalog selectedMaterial = (MaterialCatalog) parent.getItemAtPosition(position);

            new AlertDialog.Builder(this)
                    .setTitle("Удалить материал")
                    .setMessage("Удалить " + selectedMaterial.getName() + " из проекта?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        new Thread(() -> {
                            try {
                                projectUseCase.unlinkMaterialFromProject(projectId, selectedMaterial.getId());
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "Материал удалён", Toast.LENGTH_SHORT).show();
                                    loadProjectMaterialsAndTools();

                                });
                            } catch (Exception e) {
                                runOnUiThread(() ->
                                        Toast.makeText(this, "Ошибка удаления: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                );
                            }
                        }).start();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();

            return true;
        });

// Долгое нажатие на инструмент — удаление
        lvTools.setOnItemLongClickListener((parent, view, position, id) -> {
            ToolCatalog selectedTool = (ToolCatalog) parent.getItemAtPosition(position);

            new AlertDialog.Builder(this)
                    .setTitle("Удалить инструмент")
                    .setMessage("Удалить " + selectedTool.getName() + " из проекта?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        new Thread(() -> {
                            try {
                                projectUseCase.unlinkToolFromProject(projectId, selectedTool.getId());
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "Инструмент удалён", Toast.LENGTH_SHORT).show();
                                    loadProjectMaterialsAndTools();
                                });
                            } catch (Exception e) {
                                runOnUiThread(() ->
                                        Toast.makeText(this, "Ошибка удаления: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                );
                            }
                        }).start();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();

            return true;
        });

    }

    private void initViews() {
        // Находим все View по ID из XML
        tvProjectName = findViewById(R.id.tvProjectName);
        tvProjectType = findViewById(R.id.tvProjectType);

        lvMaterials = findViewById(R.id.lvMaterials);  // Изменили ID
        lvTools = findViewById(R.id.lvTools);         // Изменили ID

        btnAddMaterial = findViewById(R.id.btnAddMaterial);
        btnAddTool = findViewById(R.id.btnAddTool);

        // Настройка адаптеров
        materialsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        toolsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        lvMaterials.setAdapter(materialsAdapter);
        lvTools.setAdapter(toolsAdapter);

        // Обработчики кликов
        btnAddMaterial.setOnClickListener(v -> showAddMaterialDialog());
        btnAddTool.setOnClickListener(v -> showAddToolDialog());
    }

    private void initDatabase() {
        AppDatabase db = Room.databaseBuilder(
                        getApplicationContext(),
                        AppDatabase.class,
                        "coursework-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        RoomStorage storage = new RoomStorage(db);

        materialUseCase = new MaterialCatalogUseCase(new DBMaterialCatalogRepository(storage));
        toolUseCase = new ToolCatalogUseCase(new DBToolCatalogRepository(storage));
        projectRepo = new DBProjectRepository(storage);
        projectUseCase = new ProjectUseCase(
                projectRepo,
                new DBMaterialCatalogRepository(storage),
                new DBToolCatalogRepository(storage)
        );
    }

    private void loadProjectData() {
        new Thread(() -> {
            try {
                // Получаем базовую информацию о проекте
                List<Project> projects = projectUseCase.getAllProjects();
                for (Project p : projects) {
                    if (p.getId() == projectId) {
                        currentProject = p;
                        break;
                    }
                }

                if (currentProject == null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Проект не найден", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    return;
                }

                runOnUiThread(() -> {
                    tvProjectName.setText(currentProject.getName());
                    tvProjectType.setText("Тип: " + currentProject.getCraftType());
                });


                // Загружаем материалы и инструменты проекта
                loadProjectMaterialsAndTools();

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    private void loadProjectMaterialsAndTools() {
        new Thread(() -> {

            try {
                // 1. Получаем ВСЕ материалы и инструменты из каталогов
                List<MaterialCatalog> allMaterials = projectUseCase.getAllMaterials();
                List<ToolCatalog> allTools = projectUseCase.getAllTools();

                // 2. Получаем ВСЕ связи материалов и инструментов с проектами
                List<ProjectMaterialLinkDTO> allMaterialLinks = ((DBProjectRepository) projectRepo).getMaterialLinks();
                List<ProjectToolLinkDTO> allToolLinks = ((DBProjectRepository) projectRepo).getToolLinks();

                // 3. Фильтруем только те, что относятся к текущему проекту
                List<MaterialCatalog> projectMaterials = new ArrayList<>();
                List<ToolCatalog> projectTools = new ArrayList<>();

                for (ProjectMaterialLinkDTO link : allMaterialLinks) {
                    if (link.getProjectId() == projectId) {
                        for (MaterialCatalog material : allMaterials) {
                            if (material.getId() == link.getMaterialId())
                            {
                                projectMaterials.add(material);
                                break;
                            }
                        }
                    }
                }

                for (ProjectToolLinkDTO link : allToolLinks) {
                    if (link.getProjectId() == projectId) {
                        for (ToolCatalog tool : allTools) {
                            if (tool.getId() == link.getToolId())
                            {
                                projectTools.add(tool);
                                break;
                            }
                        }
                    }
                }

                // 4. Обновляем UI
                runOnUiThread(() -> {
                    materialsAdapter.clear();
                    materialsAdapter.addAll(projectMaterials);

                    toolsAdapter.clear();
                    toolsAdapter.addAll(projectTools);

                    materialsAdapter.notifyDataSetChanged();
                    toolsAdapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Ошибка загрузки: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void showAddMaterialDialog() {
        new Thread(() -> {
            try {
                List<MaterialCatalog> allMaterials = materialUseCase.getAllMaterials();

                runOnUiThread(() -> {
                    if (allMaterials.isEmpty()) {
                        Toast.makeText(this, "Нет доступных материалов", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Выберите материал");

                    String[] items = new String[allMaterials.size()];
                    for (int i = 0; i < allMaterials.size(); i++) {
                        items[i] = allMaterials.get(i).getName() + " (" + allMaterials.get(i).getColor() + "), "+ allMaterials.get(i).getUnit();
                    }

                    builder.setItems(items, (dialog, which) -> {
                        showQuantityDialog(allMaterials.get(which));
                    });

                    builder.setNegativeButton("Отмена", null)
                            .show();
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Ошибка загрузки материалов", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
    private void showAddToolDialog() {
        new Thread(() -> {
            try {
                List<ToolCatalog> allTools = toolUseCase.getAllTools();

                runOnUiThread(() -> {
                    if (allTools == null || allTools.isEmpty()) {
                        Toast.makeText(this, "Нет доступных инструментов", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Добавить инструмент");

                    String[] items = new String[allTools.size()];
                    for (int i = 0; i < allTools.size(); i++) {
                        ToolCatalog t = allTools.get(i);
                        items[i] = t.getName() + " (" + t.getCategory() + ")";
                    }

                    builder.setItems(items, (dialog, which) -> {
                        ToolCatalog selectedTool = allTools.get(which);
                        new Thread(() -> {
                            try {
                                projectUseCase.linkToolToProject(projectId, selectedTool.getId());
                                runOnUiThread(() -> {
                                    Toast.makeText(this, selectedTool.getName() + " добавлен", Toast.LENGTH_SHORT).show();
                                    loadProjectMaterialsAndTools();
                                });
                            } catch (Exception e) {
                                runOnUiThread(() ->
                                        Toast.makeText(this, "Ошибка добавления: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        }).start();
                    });

                    builder.setNegativeButton("Отмена", null)
                            .show();
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Ошибка загрузки инструментов", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    private void showQuantityDialog(MaterialCatalog material) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Укажите количество для " + material.getName()+", "+material.getUnit());

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            try {
                int quantity = Integer.parseInt(input.getText().toString());
                if (quantity <= 0) {
                    Toast.makeText(this, "Количество должно быть больше 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                new Thread(() -> {
                    try {
                        projectUseCase.linkMaterialToProject(projectId, material.getId(), quantity);
                        runOnUiThread(() -> {
                            Toast.makeText(this, material.getName() + " добавлен", Toast.LENGTH_SHORT).show();
                            loadProjectMaterialsAndTools();
                        });
                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Ошибка добавления: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).start();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Введите число", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProjectMaterialsAndTools();
    }
}