package com.example.coursework.repository.storage;

import android.util.Log;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {
                ProjectEntity.class,
                MaterialEntity.class,
                ToolEntity.class,
                ProjectMaterialCrossRef.class,
                ProjectToolCrossRef.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProjectDao projectDao();
    public abstract MaterialDao materialDao();
    public abstract ToolDao toolDao();
    public abstract MaterialStockDao materialStockDao();
    public abstract ToolStockDao toolStockDao();
    public abstract ProjectMaterialDao projectMaterialDao();
    public abstract ProjectToolDao projectToolDao();

    private static final String[] DEFAULT_MATERIALS = {
            "Ткань,Синий,см",
            "Ткань,Красный,см",
            "Ткань,Чёрный,см",
            "Ткань,Белый,см","Ткань,Зеленый,см","Ткань,Жёлтый,см",
            "Пряжа,Синий,гр",
            "Пряжа,Красный,гр",
            "Пряжа,Чёрный,гр",
            "Пряжа,Белый,гр",
            "Пряжа,Зеленый,гр",
            "Пряжа,Жёлтый,гр",
            "Пряжа,Розовый,гр",
            "Нитки,Синий,шт",
            "Нитки,Чёрный,шт",
            "Нитки,Белый,шт",
            "Нитки,Красный,шт",
            "Нитки,Жёлтый,шт",
            "Нитки,Розовый,шт"



    };

    private static final String[] DEFAULT_TOOLS = {
            "Спицы,Маленькие",
            "Спицы,Средние",
            "Спицы,Большие",
            "Крючок,Маленький",
            "Крючок,Средний",
            "Крючок,Большой",
            "Игла,Маленькая",
            "Игла,Средняя",
            "Игла,Большая"
    };

    public static void prepopulateData(AppDatabase db) {
        db.runInTransaction(() -> {
            MaterialDao materialDao = db.materialDao();
            ToolDao toolDao = db.toolDao();

            if (materialDao.count() == 0) {
                for (String material : DEFAULT_MATERIALS) {
                    String[] parts = material.split(",");
                    MaterialEntity entity = new MaterialEntity();
                    entity.name = parts[0];
                    entity.color = parts[1];
                    entity.unit=parts[2];
                    materialDao.insert(entity);
                }
            }

            if (toolDao.count() == 0) {
                for (String tool : DEFAULT_TOOLS) {
                    String[] parts = tool.split(",");
                    ToolEntity entity = new ToolEntity();
                    entity.name = parts[0];
                    entity.category = parts[1];
                    toolDao.insert(entity);
                }
            }
            Log.d("DB", "Materials count after insert: " + materialDao.count());
            Log.d("DB", "Tools count after insert: " + toolDao.count());
        });


    }


}
