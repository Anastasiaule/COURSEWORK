package com.example.coursework;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.coursework.domain.entity.MaterialCatalog;
import com.example.coursework.domain.entity.MaterialStock;
import com.example.coursework.domain.entity.ToolCatalog;
import com.example.coursework.domain.entity.ToolStock;
import com.example.coursework.domain.exceptions.MaterialException;
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

public class StockActivity extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog); // можно заменить на activity_stock.xml
        setupMenu();
        initDatabase();

        etSearch = findViewById(R.id.etSearch);
        tabLayout = findViewById(R.id.tabLayout);
        lvStock = findViewById(R.id.lvCatalog);

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
        lvStock.setOnItemLongClickListener((parent, view, position, id) -> {
            int selectedTab = tabLayout.getSelectedTabPosition();
            if (selectedTab == 0) {
                MaterialStock selected = (MaterialStock) parent.getItemAtPosition(position);
                showDeleteMaterialStockDialog(selected);
            } else {
                ToolStock selected = (ToolStock) parent.getItemAtPosition(position);
                showDeleteToolStockDialog(selected);
            }
            return true;
        });

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
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
    }

    private void initDatabase() {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "coursework-db")
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
            runOnUiThread(this::updateListView);
        }).start();
    }

    private void updateListView() {
        int selectedTab = tabLayout.getSelectedTabPosition();
        if (selectedTab == 0) {
            currentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allMaterialStock);
            lvStock.setAdapter(currentAdapter);
        } else {
            currentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allToolStock);
            lvStock.setAdapter(currentAdapter);
        }
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
            currentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filtered);
        } else {
            List<ToolStock> filtered = new ArrayList<>();
            for (ToolStock t : allToolStock) {
                if (t.getName().toLowerCase().startsWith(lowerQuery)) {
                    filtered.add(t);
                }
            }
            currentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filtered);
        }
        lvStock.setAdapter(currentAdapter);
    }

    private void showAddMaterialStockDialog() {
        new Thread(() -> {
            try {
                List<MaterialCatalog> allMaterials = materialCatalogUseCase.getAllMaterials();

                runOnUiThread(() -> {
                    if (allMaterials.isEmpty()) {
                        Toast.makeText(this, "Каталог материалов пуст", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                runOnUiThread(() -> Toast.makeText(this, "Ошибка загрузки каталога материалов", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }



    private void showAddToolStockDialog() {
        new Thread(() -> {
            try {
                List<ToolCatalog> allTools = toolCatalogUseCase.getAllTools();

                runOnUiThread(() -> {
                    if (allTools.isEmpty()) {
                        Toast.makeText(this, "Каталог инструментов пуст", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                                runOnUiThread(this::updateListView);
                            } catch (SQLException e) {
                                runOnUiThread(() -> Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show());
                            } catch (ToolException e) {
                                throw new RuntimeException(e);
                            }
                        }).start();
                    });

                    builder.setNegativeButton("Отмена", null).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Ошибка загрузки каталога инструментов", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void showMaterialQuantityDialog(MaterialCatalog material) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Количество для " + material.getName() + " (" + material.getUnit() + ")");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            try {
                int quantity = Integer.parseInt(input.getText().toString());
                if (quantity <= 0) {
                    Toast.makeText(this, "Введите положительное количество", Toast.LENGTH_SHORT).show();
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
                        runOnUiThread(this::updateListView);
                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Ошибка добавления: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }).start();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Некорректное количество", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Отмена", null).show();
    }

    private void showDeleteMaterialStockDialog(MaterialStock stock) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить со склада")
                .setMessage("Удалить \"" + stock.getName() + "\" (" + stock.getColor() + ")?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            materialStockUseCase.delete(stock);
                            runOnUiThread(() -> {
                                allMaterialStock.remove(stock);
                                updateListView(); // Обнови список
                                Toast.makeText(this, "Материал удалён со склада", Toast.LENGTH_SHORT).show();
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
    }

    private void showDeleteToolStockDialog(ToolStock stock) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить со склада")
                .setMessage("Удалить \"" + stock.getName() + "\" (" + stock.getCategory() + ")?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            toolStockUseCase.delete(stock);
                            runOnUiThread(() -> {
                                allToolStock.remove(stock);
                                updateListView();
                                Toast.makeText(this, "Инструмент удалён со склада", Toast.LENGTH_SHORT).show();
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
    }

}
