package com.example.coursework;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected void setupMenu() {
        BottomNavigationView nav = findViewById(R.id.bottomNavigation);

        if (nav != null) {
            nav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_main && !(this instanceof MainActivity)) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_catalog && !(this instanceof CatalogActivity)) {
                    startActivity(new Intent(this, CatalogActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_warehouse && !(this instanceof StockActivity)) {
                    startActivity(new Intent(this, StockActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_add) {
                    nav.getMenu().findItem(R.id.nav_add).setChecked(false); // убираем выделение

                    if (this instanceof MainActivity) {
                        ((MainActivity) this).showAddProjectDialog();
                        setActiveMenuItem(R.id.nav_main);  // выделяем "Главная" после открытия диалога
                    } else {
                        startActivity(new Intent(this, MainActivity.class));
                    }
                    return false; // не активируем пункт меню
                }


                return false;
            });
        }
    }
    protected void setActiveMenuItem(int itemId) {
        BottomNavigationView nav = findViewById(R.id.bottomNavigation);
        if (nav != null) {
            nav.getMenu().findItem(itemId).setChecked(true);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
