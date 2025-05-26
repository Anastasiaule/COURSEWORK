package com.example.coursework;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.coursework.domain.entity.*;
import com.example.coursework.domain.usecase.*;
import com.example.coursework.repository.dto.*;
import com.example.coursework.repository.indatabase.*;
import com.example.coursework.repository.storage.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailsFragment extends Fragment {

    private ProjectUseCase projectUseCase;
    private MaterialCatalogUseCase materialUseCase;
    private ToolCatalogUseCase toolUseCase;
    private DBProjectRepository projectRepo;
    private int projectId;
    private Project currentProject;

    private ListView lvMaterials;
    private ListView lvTools;
    private Button btnAddMaterial;
    private Button btnAddTool;
    private TextView tvProjectName;
    private TextView tvProjectType;
    private MaterialStockUseCase materialStockUseCase;
    private ArrayAdapter<MaterialCatalog> materialsAdapter;
    private ArrayAdapter<ToolCatalog> toolsAdapter;

    public ProjectDetailsFragment() {}

    public static ProjectDetailsFragment newInstance(int projectId) {
        ProjectDetailsFragment fragment = new ProjectDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("project_id", projectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_project_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            projectId = getArguments().getInt("project_id", -1);
        }

        if (projectId == -1) {
            Toast.makeText(getContext(), "Неверный ID проекта", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        initViews(view);
        initDatabase();
        loadProjectData();

        FloatingActionButton fabAdd = view.findViewById(R.id.fabBack);
        fabAdd.setOnClickListener(v -> requireActivity().onBackPressed());

        lvMaterials.setOnItemLongClickListener((parent, itemView, position, id) -> {
            MaterialCatalog selectedMaterial = (MaterialCatalog) parent.getItemAtPosition(position);
            new AlertDialog.Builder(requireContext())
                    .setTitle("Удалить материал")
                    .setMessage("Удалить " + selectedMaterial.getName() + " из проекта?")
                    .setPositiveButton("Удалить", (dialog, which) -> new Thread(() -> {
                        try {
                            projectUseCase.unlinkMaterialFromProject(projectId, selectedMaterial.getId());
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Материал удалён", Toast.LENGTH_SHORT).show();
                                loadProjectMaterialsAndTools();
                            });
                        } catch (Exception e) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "Ошибка удаления: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                        }
                    }).start())
                    .setNegativeButton("Отмена", null)
                    .show();
            return true;
        });

        lvTools.setOnItemLongClickListener((parent, itemView, position, id) -> {
            ToolCatalog selectedTool = (ToolCatalog) parent.getItemAtPosition(position);
            new AlertDialog.Builder(requireContext())
                    .setTitle("Удалить инструмент")
                    .setMessage("Удалить " + selectedTool.getName() + " из проекта?")
                    .setPositiveButton("Удалить", (dialog, which) -> new Thread(() -> {
                        try {
                            projectUseCase.unlinkToolFromProject(projectId, selectedTool.getId());
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Инструмент удалён", Toast.LENGTH_SHORT).show();
                                loadProjectMaterialsAndTools();
                            });
                        } catch (Exception e) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "Ошибка удаления: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                        }
                    }).start())
                    .setNegativeButton("Отмена", null)
                    .show();
            return true;
        });
    }

    private void initViews(View view) {
        tvProjectName = view.findViewById(R.id.tvProjectName);
        tvProjectType = view.findViewById(R.id.tvProjectType);
        lvMaterials = view.findViewById(R.id.lvMaterials);
        lvTools = view.findViewById(R.id.lvTools);
        btnAddMaterial = view.findViewById(R.id.btnAddMaterial);
        btnAddTool = view.findViewById(R.id.btnAddTool);

        materialsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        toolsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);

        lvMaterials.setAdapter(materialsAdapter);
        lvTools.setAdapter(toolsAdapter);

        btnAddMaterial.setOnClickListener(v -> showAddMaterialDialog());
        btnAddTool.setOnClickListener(v -> showAddToolDialog());
    }

    private void initDatabase() {
        AppDatabase db = Room.databaseBuilder(
                        requireContext().getApplicationContext(),
                        AppDatabase.class,
                        "coursework-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        RoomStorage storage = new RoomStorage(db);
        materialUseCase = new MaterialCatalogUseCase(new DBMaterialCatalogRepository(storage));
        toolUseCase = new ToolCatalogUseCase(new DBToolCatalogRepository(storage));
        materialStockUseCase = new MaterialStockUseCase(new DBMaterialStockRepository(storage));
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
                List<Project> projects = projectUseCase.getAllProjects();
                for (Project p : projects) {
                    if (p.getId() == projectId) {
                        currentProject = p;
                        break;
                    }
                }

                if (currentProject == null) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Проект не найден", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    });
                    return;
                }

                requireActivity().runOnUiThread(() -> {
                    tvProjectName.setText(currentProject.getName());
                    tvProjectType.setText("Тип: " + currentProject.getCraftType());
                });

                loadProjectMaterialsAndTools();

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                });
            }
        }).start();
    }

    private void loadProjectMaterialsAndTools() {
        new Thread(() -> {
            try {
                List<MaterialCatalog> allMaterials = projectUseCase.getAllMaterials();
                List<ToolCatalog> allTools = projectUseCase.getAllTools();

                List<ProjectMaterialLinkDTO> allMaterialLinks = ((DBProjectRepository) projectRepo).getMaterialLinks();
                List<ProjectToolLinkDTO> allToolLinks = ((DBProjectRepository) projectRepo).getToolLinks();

                List<MaterialCatalog> projectMaterials = new ArrayList<>();
                List<ToolCatalog> projectTools = new ArrayList<>();

                for (ProjectMaterialLinkDTO link : allMaterialLinks) {
                    if (link.getProjectId() == projectId) {
                        for (MaterialCatalog material : allMaterials) {
                            if (material.getId() == link.getMaterialId()) {
                                projectMaterials.add(material);
                                break;
                            }
                        }
                    }
                }

                for (ProjectToolLinkDTO link : allToolLinks) {
                    if (link.getProjectId() == projectId) {
                        for (ToolCatalog tool : allTools) {
                            if (tool.getId() == link.getToolId()) {
                                projectTools.add(tool);
                                break;
                            }
                        }
                    }
                }

                requireActivity().runOnUiThread(() -> {
                    materialsAdapter.clear();
                    materialsAdapter.addAll(projectMaterials);
                    toolsAdapter.clear();
                    toolsAdapter.addAll(projectTools);
                    materialsAdapter.notifyDataSetChanged();
                    toolsAdapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Ошибка загрузки: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void showAddMaterialDialog() {
        new Thread(() -> {
            try {
                List<MaterialCatalog> allMaterials = materialUseCase.getAllMaterials();
                requireActivity().runOnUiThread(() -> {
                    if (allMaterials.isEmpty()) {
                        Toast.makeText(getContext(), "Нет доступных материалов", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Выберите материал");

                    String[] items = new String[allMaterials.size()];
                    for (int i = 0; i < allMaterials.size(); i++) {
                        items[i] = allMaterials.get(i).getName() + " (" + allMaterials.get(i).getColor() + "), " + allMaterials.get(i).getUnit();
                    }

                    builder.setItems(items, (dialog, which) -> showQuantityDialog(allMaterials.get(which)));
                    builder.setNegativeButton("Отмена", null).show();
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Ошибка загрузки материалов", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void showAddToolDialog() {
        new Thread(() -> {
            try {
                List<ToolCatalog> allTools = toolUseCase.getAllTools();
                requireActivity().runOnUiThread(() -> {
                    if (allTools == null || allTools.isEmpty()) {
                        Toast.makeText(getContext(), "Нет доступных инструментов", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Добавить инструмент");

                    String[] items = new String[allTools.size()];
                    for (int i = 0; i < allTools.size(); i++) {
                        items[i] = allTools.get(i).getName() + " (" + allTools.get(i).getCategory() + ")";
                    }

                    builder.setItems(items, (dialog, which) -> {
                        ToolCatalog selectedTool = allTools.get(which);
                        new Thread(() -> {
                            try {
                                projectUseCase.linkToolToProject(projectId, selectedTool.getId());
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), selectedTool.getName() + " добавлен", Toast.LENGTH_SHORT).show();
                                    loadProjectMaterialsAndTools();
                                });
                            } catch (Exception e) {
                                requireActivity().runOnUiThread(() ->
                                        Toast.makeText(getContext(), "Ошибка добавления: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        }).start();
                    });

                    builder.setNegativeButton("Отмена", null).show();
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Ошибка загрузки инструментов", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void showQuantityDialog(MaterialCatalog material) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Укажите количество для " + material.getName() + ", " + material.getUnit());

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String inputText = input.getText().toString().trim();
            if (inputText.isEmpty()) {
                Toast.makeText(getContext(), "Введите количество", Toast.LENGTH_SHORT).show();
                return;
            }

            int requestedQuantity;
            try {
                requestedQuantity = Integer.parseInt(inputText);
                if (requestedQuantity <= 0) {
                    Toast.makeText(getContext(), "Количество должно быть больше 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Введите корректное число", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                try {
                    List<MaterialStock> stockList = materialStockUseCase.getAllMaterials();
                    MaterialStock matchingStock = null;

                    for (MaterialStock stock : stockList) {
                        if (stock.getName().equals(material.getName()) &&
                                stock.getColor().equals(material.getColor()) &&
                                stock.getUnit().equals(material.getUnit())) {
                            matchingStock = stock;
                            break;
                        }
                    }

                    int stockQuantity = (matchingStock != null) ? matchingStock.getQuantity() : 0;

                    projectUseCase.linkMaterialToProject(projectId, material.getId(), requestedQuantity);

                    if (matchingStock != null) {
                        int newQuantity = stockQuantity - requestedQuantity;
                        MaterialStock updatedStock = new MaterialStock(
                                matchingStock.getId(),
                                matchingStock.getName(),
                                matchingStock.getColor(),
                                newQuantity,
                                matchingStock.getUnit()
                        );
                        materialStockUseCase.delete(matchingStock);
                        materialStockUseCase.createNewMaterialStock(
                                updatedStock.getName(),
                                updatedStock.getColor(),
                                updatedStock.getQuantity(),
                                updatedStock.getUnit()
                        );
                    }

                    requireActivity().runOnUiThread(() -> {
                        if (stockQuantity == 0) {
                            Toast.makeText(getContext(), "Материал отсутствует на складе, но добавлен в проект", Toast.LENGTH_LONG).show();
                        } else if (stockQuantity < requestedQuantity) {
                            int deficit = requestedQuantity - stockQuantity;
                            Toast.makeText(getContext(), "Не хватает " + deficit + " " + material.getUnit() + ", но добавлено", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), material.getName() + " добавлен в проект", Toast.LENGTH_SHORT).show();
                        }

                        loadProjectMaterialsAndTools();
                    });

                } catch (Exception e) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Ошибка добавления: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProjectMaterialsAndTools();
    }
}

