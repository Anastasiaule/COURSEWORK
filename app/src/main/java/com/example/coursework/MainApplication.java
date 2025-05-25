package com.example.coursework;

import android.app.Application;

import androidx.room.Room;

import com.example.coursework.domain.usecase.MaterialCatalogUseCase;
import com.example.coursework.domain.usecase.ProjectUseCase;
import com.example.coursework.domain.usecase.ToolCatalogUseCase;
import com.example.coursework.repository.indatabase.DBMaterialCatalogRepository;
import com.example.coursework.repository.indatabase.DBProjectRepository;
import com.example.coursework.repository.indatabase.DBToolCatalogRepository;
import com.example.coursework.repository.storage.AppDatabase;
import com.example.coursework.repository.storage.RoomStorage;

public class MainApplication extends Application {
    private ProjectUseCase projectUseCase;
    private MaterialCatalogUseCase materialUseCase;
    private ToolCatalogUseCase toolUseCase;

    @Override
    public void onCreate() {
        super.onCreate();
        // Инициализация UseCases
        AppDatabase db = Room.databaseBuilder(
                        getApplicationContext(),
                        AppDatabase.class,
                        "coursework-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        RoomStorage storage = new RoomStorage(db);
        DBProjectRepository projectRepo = new DBProjectRepository(storage);
        DBMaterialCatalogRepository materialRepo = new DBMaterialCatalogRepository(storage);
        DBToolCatalogRepository toolRepo = new DBToolCatalogRepository(storage);

        projectUseCase = new ProjectUseCase(projectRepo, materialRepo, toolRepo);
        materialUseCase = new MaterialCatalogUseCase(materialRepo);
        toolUseCase = new ToolCatalogUseCase(toolRepo);
    }

    public ProjectUseCase getProjectUseCase() {
        return projectUseCase;
    }

    public MaterialCatalogUseCase getMaterialUseCase() {
        return materialUseCase;
    }

    public ToolCatalogUseCase getToolUseCase() {
        return toolUseCase;
    }
}