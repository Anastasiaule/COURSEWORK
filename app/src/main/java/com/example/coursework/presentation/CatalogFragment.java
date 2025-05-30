package com.example.coursework.presentation;


import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.coursework.R;
import com.example.coursework.domain.entity.MaterialCatalog;
import com.example.coursework.domain.entity.ToolCatalog;
import com.example.coursework.domain.exceptions.ToolException;
import com.example.coursework.domain.usecase.MaterialCatalogUseCase;
import com.example.coursework.domain.usecase.ToolCatalogUseCase;
import com.example.coursework.data.repository.DBMaterialCatalogRepository;
import com.example.coursework.data.repository.DBToolCatalogRepository;
import com.example.coursework.data.storage.AppDatabase;
import com.example.coursework.data.storage.RoomStorage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogFragment extends Fragment {

    private MaterialCatalogUseCase materialUseCase;
    private ToolCatalogUseCase toolUseCase;

    private List<MaterialCatalog> allMaterials = new ArrayList<>();
    private List<ToolCatalog> allTools = new ArrayList<>();

    private EditText etSearch;
    private TabLayout tabLayout;
    private ListView lvCatalog;
    private ArrayAdapter<?> currentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_catalog, container, false); // можно переименовать layout позже
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDatabase();

        etSearch = view.findViewById(R.id.etSearch);
        tabLayout = view.findViewById(R.id.tabLayout);
        lvCatalog = view.findViewById(R.id.lvCatalog);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);

        tabLayout.addTab(tabLayout.newTab().setText("Материалы"));
        tabLayout.addTab(tabLayout.newTab().setText("Инструменты"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                updateListView();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        fabAdd.setOnClickListener(v -> {
            int selectedTab = tabLayout.getSelectedTabPosition();
            if (selectedTab == 0) {
                showAddMaterialDialog();
            } else if (selectedTab == 1) {
                showAddToolDialog();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadData();
    }

    private void initDatabase() {
        AppDatabase db = Room.databaseBuilder(requireContext(), AppDatabase.class, "coursework-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        RoomStorage storage = new RoomStorage(db);
        materialUseCase = new MaterialCatalogUseCase(new DBMaterialCatalogRepository(storage));
        toolUseCase = new ToolCatalogUseCase(new DBToolCatalogRepository(storage));
    }

    private void loadData() {
        new Thread(() -> {
            allMaterials = materialUseCase.getAllMaterials();
            allTools = toolUseCase.getAllTools();
            requireActivity().runOnUiThread(this::updateListView);
        }).start();
    }

    private void updateListView() {
        int selectedTab = tabLayout.getSelectedTabPosition();
        if (selectedTab == 0) {
            currentAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, allMaterials);
            lvCatalog.setAdapter(currentAdapter);
            lvCatalog.setOnItemLongClickListener((parent, view, position, id) -> {
                MaterialCatalog selected = (MaterialCatalog) parent.getItemAtPosition(position);
                showDeleteMaterialDialog(selected);
                return true;
            });
        } else {
            currentAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, allTools);
            lvCatalog.setAdapter(currentAdapter);
            lvCatalog.setOnItemLongClickListener((parent, view, position, id) -> {
                ToolCatalog selected = (ToolCatalog) parent.getItemAtPosition(position);
                showDeleteToolDialog(selected);
                return true;
            });
        }
    }

    private void showDeleteMaterialDialog(MaterialCatalog material) {
        new AlertDialog.Builder(getContext())
                .setTitle("Удалить материал")
                .setMessage("Удалить \"" + material.getName() + "\" из каталога?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            materialUseCase.delete(material);
                            requireActivity().runOnUiThread(() -> {
                                allMaterials.remove(material);
                                updateListView();
                                Toast.makeText(getContext(), "Материал удалён", Toast.LENGTH_SHORT).show();
                            });
                        } catch (SQLException e) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    }).start();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showDeleteToolDialog(ToolCatalog tool) {
        new AlertDialog.Builder(getContext())
                .setTitle("Удалить инструмент")
                .setMessage("Удалить \"" + tool.getName() + "\" из каталога?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            toolUseCase.delete(tool);
                            requireActivity().runOnUiThread(() -> {
                                allTools.remove(tool);
                                updateListView();
                                Toast.makeText(getContext(), "Инструмент удалён", Toast.LENGTH_SHORT).show();
                            });
                        } catch (SQLException e) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    }).start();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void filterData(String query) {
        String lowerQuery = query.toLowerCase();
        int selectedTab = tabLayout.getSelectedTabPosition();

        if (selectedTab == 0) {
            List<MaterialCatalog> filtered = new ArrayList<>();
            for (MaterialCatalog m : allMaterials) {
                if (m.getName().toLowerCase().startsWith(lowerQuery)) {
                    filtered.add(m);
                }
            }
            currentAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, filtered);
        } else {
            List<ToolCatalog> filtered = new ArrayList<>();
            for (ToolCatalog t : allTools) {
                if (t.getName().toLowerCase().startsWith(lowerQuery)) {
                    filtered.add(t);
                }
            }
            currentAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, filtered);
        }

        lvCatalog.setAdapter(currentAdapter);
    }

    public void showAddMaterialDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_material, null);
        EditText etName = dialogView.findViewById(R.id.etMaterialName);
        EditText etColor = dialogView.findViewById(R.id.etMaterialColor);
        Spinner spinnerUnit = dialogView.findViewById(R.id.spinnerUnit);

        String[] units = {"шт", "гр", "кг", "см", "см²", "м", "м²"};
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, units);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        new AlertDialog.Builder(getContext())
                .setTitle("Добавить материал")
                .setView(dialogView)
                .setPositiveButton("Добавить", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String color = etColor.getText().toString().trim();
                    String unit = spinnerUnit.getSelectedItem().toString();

                    if (name.isEmpty() || color.isEmpty()) {
                        Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new Thread(() -> {
                        try {
                            materialUseCase.createNewMaterialCatalog(name, color, unit);
                            allMaterials = materialUseCase.getAllMaterials();
                            requireActivity().runOnUiThread(() -> {
                                loadData();
                                updateListView();
                                Toast.makeText(getContext(), "Материал добавлен", Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "Ошибка сохранения: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    }).start();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showAddToolDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_tool, null);
        EditText etName = view.findViewById(R.id.etToolName);
        EditText etCategory = view.findViewById(R.id.etToolCategory);

        new AlertDialog.Builder(getContext())
                .setTitle("Добавить инструмент")
                .setView(view)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String category = etCategory.getText().toString().trim();

                    if (!name.isEmpty() && !category.isEmpty()) {
                        try {
                            toolUseCase.createNewToolCatalog(name, category);
                        } catch (ToolException | SQLException e) {
                            throw new RuntimeException(e);
                        }
                        loadData();
                        Toast.makeText(getContext(), "Инструмент добавлен", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}
