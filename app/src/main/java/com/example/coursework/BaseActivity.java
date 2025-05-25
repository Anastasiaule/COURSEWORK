package com.example.coursework;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected void setupMenu() {
        Button btnMain = findViewById(R.id.btnMain);
        Button btnCatalog = findViewById(R.id.btnCatalog);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnWarehouse = findViewById(R.id.btnWarehouse);

        if (btnMain != null) {
            btnMain.setOnClickListener(v -> {
                if (!(this instanceof MainActivity)) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            });
        }

        if (btnCatalog != null) {
            btnCatalog.setOnClickListener(v -> {
                if (!(this instanceof CatalogActivity)) {
                    startActivity(new Intent(this, CatalogActivity.class));
                    finish();
                }
            });
        }


        if (btnAdd != null) {

            btnAdd.setOnClickListener(v -> ((MainActivity) this).showAddProjectDialog());
        }

        /*
        if (btnWarehouse != null) {
            btnWarehouse.setOnClickListener(v -> {
                // Запускаем активити склада (создай WarehouseActivity или как назовёшь)
                startActivity(new Intent(this, WarehouseActivity.class));
            });
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}