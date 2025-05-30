package com.example.coursework.presentation;

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

import com.example.coursework.R;
import com.example.coursework.data.dto.ProjectMaterialLinkDTO;
import com.example.coursework.data.dto.ProjectToolLinkDTO;
import com.example.coursework.data.repository.DBMaterialCatalogRepository;
import com.example.coursework.data.repository.DBMaterialStockRepository;
import com.example.coursework.data.repository.DBProjectRepository;
import com.example.coursework.data.repository.DBToolCatalogRepository;
import com.example.coursework.data.storage.AppDatabase;
import com.example.coursework.data.storage.RoomStorage;
import com.example.coursework.domain.entity.*;
import com.example.coursework.domain.usecase.*;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailsFragment extends Fragment {
    private ProjectUseCase projectUseCase;
    private MaterialCatalogUseCase materialUseCase;
    private ToolCatalogUseCase toolUseCase;
    private DBProjectRepository projectRepo;
    private MaterialStockUseCase materialStockUseCase;
    private int projectId;
    private Project currentProject;

    private ListView lvMaterials;
    private ListView lvTools;
    private Button btnAddMaterial;
    private Button btnAddTool;
    private TextView tvProjectName;
    private TextView tvProjectType;
    private ArrayAdapter<String> materialsAdapter;
    private ArrayAdapter<ToolCatalog> toolsAdapter;

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

        view.findViewById(R.id.fabBack).setOnClickListener(v -> requireActivity().onBackPressed());

        lvMaterials.setOnItemLongClickListener((parent, itemView, position, id) -> {
            String itemText = materialsAdapter.getItem(position);
            new AlertDialog.Builder(requireContext())
                    .setTitle("Удалить материал")
                    .setMessage("Удалить этот материал из проекта?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        new Thread(() -> {
                            try {
                                ProjectMaterialLinkDTO link = getMaterialLinkByPosition(position);
                                if (link != null) {
                                    projectUseCase.unlinkMaterialFromProject(projectId, link.getMaterialId());

                                    MaterialCatalog material = materialUseCase.getMaterialById(link.getMaterialId());
                                    MaterialStock stock = findMatchingStock(material);
                                    if (stock != null) {
                                        materialStockUseCase.delete(stock);
                                        materialStockUseCase.createNewMaterialStock(
                                                stock.getName(), stock.getColor(), stock.getQuantity() + link.getQuantity(), stock.getUnit()
                                        );
                                    } else {
                                        materialStockUseCase.createNewMaterialStock(
                                                material.getName(), material.getColor(), link.getQuantity(), material.getUnit()
                                        );
                                    }

                                    requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(getContext(), "Материал удалён", Toast.LENGTH_SHORT).show();
                                        loadProjectMaterialsAndTools();
                                    });
                                }
                            } catch (Exception e) {
                                requireActivity().runOnUiThread(() ->
                                        Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                            }
                        }).start();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
            return true;
        });

        lvTools.setOnItemLongClickListener((parent, view1, position, id) -> {
            ToolCatalog tool = toolsAdapter.getItem(position);
            new AlertDialog.Builder(requireContext())
                    .setTitle("Удалить инструмент")
                    .setMessage("Удалить " + tool.getName() + " из проекта?")
                    .setPositiveButton("Удалить", (dialog, which) -> new Thread(() -> {
                        try {
                            projectUseCase.unlinkToolFromProject(projectId, tool.getId());
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
                        requireContext().getApplicationContext(), AppDatabase.class, "coursework-db")
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
                currentProject = projectUseCase.getProjectById(projectId);
                requireActivity().runOnUiThread(() -> {
                    tvProjectName.setText(currentProject.getName());
                    tvProjectType.setText("Тип: " + currentProject.getCraftType());
                });
                loadProjectMaterialsAndTools();
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Ошибка загрузки проекта", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void loadProjectMaterialsAndTools() {
        new Thread(() -> {
            try {
                List<ProjectMaterialLinkDTO> links = projectRepo.getMaterialLinks();
                List<String> materialDescriptions = new ArrayList<>();
                for (ProjectMaterialLinkDTO link : links) {
                    if (link.getProjectId() == projectId) {
                        MaterialCatalog material = materialUseCase.getMaterialById(link.getMaterialId());
                        if (material != null) {
                            materialDescriptions.add(material.getName() + " (" + material.getColor() + "), " +
                                    link.getQuantity() + " " + material.getUnit());
                        }
                    }
                }

                List<ToolCatalog> tools = new ArrayList<>();
                for (ProjectToolLinkDTO link : projectRepo.getToolLinks()) {
                    if (link.getProjectId() == projectId) {
                        ToolCatalog tool = toolUseCase.getToolById(link.getToolId());
                        if (tool != null) tools.add(tool);
                    }
                }

                requireActivity().runOnUiThread(() -> {
                    materialsAdapter.clear();
                    materialsAdapter.addAll(materialDescriptions);
                    toolsAdapter.clear();
                    toolsAdapter.addAll(tools);
                    materialsAdapter.notifyDataSetChanged();
                    toolsAdapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Ошибка загрузки: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }



    private ProjectMaterialLinkDTO getMaterialLinkByPosition(int position) {
        List<ProjectMaterialLinkDTO> links = projectRepo.getMaterialLinks();
        int index = 0;
        for (ProjectMaterialLinkDTO link : links) {
            if (link.getProjectId() == projectId) {
                if (index == position) return link;
                index++;
            }
        }
        return null;
    }

    private MaterialStock findMatchingStock(MaterialCatalog material) {
        for (MaterialStock stock : materialStockUseCase.getAllMaterials()) {
            if (stock.getName().equals(material.getName()) &&
                    stock.getColor().equals(material.getColor()) &&
                    stock.getUnit().equals(material.getUnit())) {
                return stock;
            }
        }
        return null;
    }

    private void showAddMaterialDialog() {
        new Thread(() -> {
            try {
                List<MaterialCatalog> allMaterials = materialUseCase.getAllMaterials();
                List<MaterialStock> stockList = materialStockUseCase.getAllMaterials();

                requireActivity().runOnUiThread(() -> {
                    if (allMaterials.isEmpty()) {
                        Toast.makeText(getContext(), "Нет доступных материалов", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Выберите материал");

                    String[] items = new String[allMaterials.size()];
                    for (int i = 0; i < allMaterials.size(); i++) {
                        MaterialCatalog material = allMaterials.get(i);

                        // Найти количество на складе для этого материала
                        int stockQuantity = 0;
                        for (MaterialStock stock : stockList) {
                            if (stock.getName().equals(material.getName())
                                    && stock.getColor().equals(material.getColor())
                                    && stock.getUnit().equals(material.getUnit())) {
                                stockQuantity = stock.getQuantity();
                                break;
                            }
                        }

                        items[i] = material.getName() + " (" + material.getColor() + "), " + material.getUnit()
                                + " - В наличии: " + stockQuantity;
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