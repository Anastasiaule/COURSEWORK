package com.example.coursework;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.coursework.domain.entity.MaterialCatalog;
import com.example.coursework.domain.entity.MaterialStock;
import com.example.coursework.domain.entity.ToolCatalog;
import com.example.coursework.domain.entity.ToolStock;
import com.example.coursework.domain.exceptions.ToolException;
import com.example.coursework.domain.usecase.MaterialCatalogUseCase;
import com.example.coursework.domain.usecase.MaterialStockUseCase;
import com.example.coursework.domain.usecase.ToolCatalogUseCase;
import com.example.coursework.domain.usecase.ToolStockUseCase;
import com.example.coursework.repository.indatabase.DBMaterialCatalogRepository;
import com.example.coursework.repository.indatabase.DBMaterialStockRepository;
import com.example.coursework.repository.indatabase.DBToolCatalogRepository;
import com.example.coursework.repository.indatabase.DBToolStockRepository;
import com.example.coursework.repository.storage.AppDatabase;
import com.example.coursework.repository.storage.RoomStorage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockFragment extends Fragment {

    private MaterialStockUseCase materialStockUseCase;
    private ToolStockUseCase toolStockUseCase;
    private MaterialCatalogUseCase materialCatalogUseCase;
    private ToolCatalogUseCase toolCatalogUseCase;

    private List<MaterialStock> allMaterialStock = new ArrayList<>();
    private List<ToolStock> allToolStock = new ArrayList<>();

    private EditText etSearch;
    private TabLayout tabLayout;
    private ListView lvStock;
    private ArrayAdapter<?> currentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_stock, container, false); // ⚠️ Ensure you have this layout

        etSearch = view.findViewById(R.id.etSearch);
        tabLayout = view.findViewById(R.id.tabLayout);
        lvStock = view.findViewById(R.id.lvCatalog);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);

        initDatabase();

        tabLayout.addTab(tabLayout.newTab().setText("Материалы"));
        tabLayout.addTab(tabLayout.newTab().setText("Инструменты"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateListView();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });


        lvStock.setOnItemLongClickListener((parent, v, position, id) -> {
            if (tabLayout.getSelectedTabPosition() == 0) {
                MaterialStock selected = (MaterialStock) parent.getItemAtPosition(position);
                showDeleteMaterialStockDialog(selected);
            } else {
                ToolStock selected = (ToolStock) parent.getItemAtPosition(position);
                showDeleteToolStockDialog(selected);
            }
            return true;
        });

        fabAdd.setOnClickListener(v -> {
            if (tabLayout.getSelectedTabPosition() == 0) {
                showAddMaterialStockDialog();
            } else {
                showAddToolStockDialog();
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

        return view;
    }

    private void initDatabase() {
        AppDatabase db = Room.databaseBuilder(requireContext(), AppDatabase.class, "coursework-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        RoomStorage storage = new RoomStorage(db);
        materialStockUseCase = new MaterialStockUseCase(new DBMaterialStockRepository(storage));
        toolStockUseCase = new ToolStockUseCase(new DBToolStockRepository(storage));
        materialCatalogUseCase = new MaterialCatalogUseCase(new DBMaterialCatalogRepository(storage));
        toolCatalogUseCase = new ToolCatalogUseCase(new DBToolCatalogRepository(storage));
    }

    private void loadData() {
        new Thread(() -> {
            allMaterialStock = materialStockUseCase.getAllMaterials();
            allToolStock = toolStockUseCase.getAllTools();
            requireActivity().runOnUiThread(this::updateListView);
        }).start();
    }

    private void updateListView() {
        int selectedTab = tabLayout.getSelectedTabPosition();
        if (selectedTab == 0) {
            currentAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, allMaterialStock);
        } else {
            currentAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, allToolStock);
        }
        lvStock.setAdapter(currentAdapter);
    }

    private void filterData(String query) {
        String lowerQuery = query.toLowerCase();
        if (tabLayout.getSelectedTabPosition() == 0) {
            List<MaterialStock> filtered = new ArrayList<>();
            for (MaterialStock m : allMaterialStock) {
                if (m.getName().toLowerCase().startsWith(lowerQuery)) {
                    filtered.add(m);
                }
            }
            currentAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, filtered);
        } else {
            List<ToolStock> filtered = new ArrayList<>();
            for (ToolStock t : allToolStock) {
                if (t.getName().toLowerCase().startsWith(lowerQuery)) {
                    filtered.add(t);
                }
            }
            currentAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, filtered);
        }
        lvStock.setAdapter(currentAdapter);
    }

    private void showAddMaterialStockDialog() {
        new Thread(() -> {
            try {
                List<MaterialCatalog> allMaterials = materialCatalogUseCase.getAllMaterials();

                requireActivity().runOnUiThread(() -> {
                    if (allMaterials.isEmpty()) {
                        Toast.makeText(requireContext(), "Каталог материалов пуст", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Выберите материал");

                    String[] items = new String[allMaterials.size()];
                    for (int i = 0; i < allMaterials.size(); i++) {
                        MaterialCatalog m = allMaterials.get(i);
                        items[i] = m.getName() + " (" + m.getColor() + ", " + m.getUnit() + ")";
                    }

                    builder.setItems(items, (dialog, which) -> {
                        MaterialCatalog selectedMaterial = allMaterials.get(which);
                        showMaterialQuantityDialog(selectedMaterial);
                    });

                    builder.setNegativeButton("Отмена", null).show();
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Ошибка загрузки каталога материалов", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void showMaterialQuantityDialog(MaterialCatalog material) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Количество для " + material.getName() + " (" + material.getUnit() + ")");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            try {
                int quantity = Integer.parseInt(input.getText().toString());
                if (quantity <= 0) {
                    Toast.makeText(requireContext(), "Введите положительное количество", Toast.LENGTH_SHORT).show();
                    return;
                }

                new Thread(() -> {
                    try {
                        materialStockUseCase.createNewMaterialStock(
                                material.getName(),
                                material.getColor(),
                                quantity,
                                material.getUnit()
                        );
                        allMaterialStock = materialStockUseCase.getAllMaterials();
                        requireActivity().runOnUiThread(this::updateListView);
                    } catch (Exception e) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Ошибка добавления: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }).start();
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Некорректное количество", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Отмена", null).show();
    }

    private void showAddToolStockDialog() {
        new Thread(() -> {
            try {
                List<ToolCatalog> allTools = toolCatalogUseCase.getAllTools();

                requireActivity().runOnUiThread(() -> {
                    if (allTools.isEmpty()) {
                        Toast.makeText(requireContext(), "Каталог инструментов пуст", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Выберите инструмент");

                    String[] items = new String[allTools.size()];
                    for (int i = 0; i < allTools.size(); i++) {
                        items[i] = allTools.get(i).getName() + " (" + allTools.get(i).getCategory() + ")";
                    }

                    builder.setItems(items, (dialog, which) -> {
                        ToolCatalog selectedTool = allTools.get(which);
                        new Thread(() -> {
                            try {
                                toolStockUseCase.createNewToolStock(selectedTool.getName(), selectedTool.getCategory());
                                allToolStock = toolStockUseCase.getAllTools();
                                requireActivity().runOnUiThread(this::updateListView);
                            } catch (SQLException e) {
                                requireActivity().runOnUiThread(() ->
                                        Toast.makeText(requireContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show());
                            } catch (ToolException e) {
                                throw new RuntimeException(e);
                            }
                        }).start();
                    });

                    builder.setNegativeButton("Отмена", null).show();
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Ошибка загрузки каталога инструментов", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void showDeleteMaterialStockDialog(MaterialStock stock) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Удалить со склада")
                .setMessage("Удалить \"" + stock.getName() + "\" (" + stock.getColor() + ")?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            materialStockUseCase.delete(stock);
                            requireActivity().runOnUiThread(() -> {
                                allMaterialStock.remove(stock);
                                updateListView();
                                Toast.makeText(requireContext(), "Материал удалён со склада", Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Ошибка удаления: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    }).start();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showDeleteToolStockDialog(ToolStock stock) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Удалить со склада")
                .setMessage("Удалить \"" + stock.getName() + "\" (" + stock.getCategory() + ")?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            toolStockUseCase.delete(stock);
                            requireActivity().runOnUiThread(() -> {
                                allToolStock.remove(stock);
                                updateListView();
                                Toast.makeText(requireContext(), "Инструмент удалён со склада", Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Ошибка удаления: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    }).start();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}

