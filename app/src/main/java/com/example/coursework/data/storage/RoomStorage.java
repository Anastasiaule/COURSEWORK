package com.example.coursework.data.storage;

import android.util.Log;

import com.example.coursework.domain.entity.MaterialCatalog;
import com.example.coursework.domain.entity.MaterialStock;
import com.example.coursework.domain.entity.Project;
import com.example.coursework.domain.entity.ToolCatalog;
import com.example.coursework.domain.entity.ToolStock;
import com.example.coursework.data.dto.ProjectMaterialLinkDTO;
import com.example.coursework.data.dto.ProjectToolLinkDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomStorage {
    private final AppDatabase db;

    public RoomStorage(AppDatabase db) {
        this.db = db;
    }

    // === PROJECTS ===

    public Project addProject(Project project) {
        ProjectEntity e = new ProjectEntity();
        e.name = project.getName();
        e.craftType = project.getCraftType();
        long id = db.projectDao().insert(e);
        project.setId((int) id);
        return project;
    }

    public List<Project> readDBProject() {
        List<Project> out = new ArrayList<>();
        for (ProjectEntity e : db.projectDao().getAll()) {
            Project p = new Project(e.name, e.craftType);
            p.setId(e.id);
            out.add(p);
        }
        return out;
    }

    public void updateProject(Project project) {
        ProjectEntity e = new ProjectEntity();
        e.id = project.getId();
        e.name = project.getName();
        e.craftType = project.getCraftType();
        db.projectDao().update(e);
    }

    public boolean deleteProject(int projectId) {
        ProjectEntity e = new ProjectEntity();
        e.id = projectId;
        db.projectDao().delete(e);
        return true;
    }

    // === MATERIALS ===

    public MaterialCatalog addMaterial(MaterialCatalog m) {
        MaterialEntity e = new MaterialEntity();
        e.name = m.getName();
        e.color = m.getColor();
        e.unit=m.getUnit();
        long id = db.materialDao().insert(e);
        m.setId((int) id);
        return m;
    }

    public List<MaterialCatalog> readMaterials() {
        List<MaterialCatalog> out = new ArrayList<>();
        for (MaterialEntity e : db.materialDao().getAll()) {
            MaterialCatalog m = new MaterialCatalog(e.name, e.color, e.unit);
            m.setId(e.id);
            out.add(m);
        }
        return out;
    }


    public boolean deleteMaterial(int materialId) {
        MaterialEntity e = new MaterialEntity();
        e.id = materialId;
        db.materialDao().delete(e);
        return true;
    }

    public Optional<MaterialCatalog> findMaterialById(int id) {
        for (MaterialEntity e : db.materialDao().getAll()) {
            if (e.id == id) {
                MaterialCatalog m = new MaterialCatalog(e.name, e.color, e.unit);
                m.setId(e.id);
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }

    // === TOOLS ===

    public ToolCatalog addTool(ToolCatalog t) {
        ToolEntity e = new ToolEntity();
        e.name = t.getName();
        e.category = t.getCategory();
        long id = db.toolDao().insert(e);
        t.setId((int) id);
        return t;
    }

    public List<ToolCatalog> readTools() {
        List<ToolCatalog> out = new ArrayList<>();
        for (ToolEntity e : db.toolDao().getAll()) {
            ToolCatalog t = new ToolCatalog(e.name, e.category);
            t.setId(e.id);
            out.add(t);
        }
        return out;
    }


    public boolean deleteTool(int toolId) {
        ToolEntity e = new ToolEntity();
        e.id = toolId;
        db.toolDao().delete(e);
        return true;
    }

    public Optional<ToolCatalog> findToolById(int id) {
        for (ToolEntity e : db.toolDao().getAll()) {
            if (e.id == id) {
                ToolCatalog t = new ToolCatalog(e.name, e.category);
                t.setId(e.id);
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }



    // Добавить материал на склад
    public MaterialStock addMaterialStock(MaterialStock material) {
        MaterialStockEntity existing = db.materialStockDao()
                .findByNameColorUnit(material.getName(), material.getColor(), material.getUnit());

        if (existing != null) {
            // Суммируем и обновляем
            existing.quantity += material.getQuantity();
            db.materialStockDao().update(existing);

            material.setId(existing.id);
            material.setQuantity(existing.quantity);
        } else {
            // Вставляем новый
            MaterialStockEntity e = new MaterialStockEntity();
            e.name = material.getName();
            e.color = material.getColor();
            e.unit = material.getUnit();
            e.quantity = material.getQuantity();

            long id = db.materialStockDao().insert(e);
            material.setId((int) id);
        }

        return material;
    }



    // Прочитать все материалы со склада
    public List<MaterialStock> readMaterialStock() {
        List<MaterialStock> out = new ArrayList<>();
        for (MaterialStockEntity e : db.materialStockDao().getAll()) {
            MaterialStock m = new MaterialStock(e.name, e.color, e.quantity, e.unit);
            m.setId(e.id);
            out.add(m);
        }
        return out;
    }

    // Удалить материал по id
    public boolean deleteMaterialStock(int materialId) {
        MaterialStockEntity e = new MaterialStockEntity();
        e.id = materialId;
        db.materialStockDao().delete(e);
        return true;
    }

    // Найти материал по id
    public Optional<MaterialStock> findMaterialStockById(int id) {
        for (MaterialStockEntity e : db.materialStockDao().getAll()) {
            if (e.id == id) {
                MaterialStock m = new MaterialStock(e.name, e.color, e.quantity, e.unit);
                m.setId(e.id);
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }

    // Добавить инструмент на склад
    public ToolStock addToolStock(ToolStock tool) {
        ToolStockEntity e = new ToolStockEntity();
        e.name = tool.getName();
        e.category = tool.getCategory();
        long id = db.toolStockDao().insert(e);
        tool.setId((int) id);
        return tool;
    }

    // Прочитать все инструменты со склада
    public List<ToolStock> readToolStock() {
        List<ToolStock> out = new ArrayList<>();
        for (ToolStockEntity e : db.toolStockDao().getAll()) {
            ToolStock t = new ToolStock(e.name, e.category);
            t.setId(e.id);
            out.add(t);
        }
        return out;
    }

    // Удалить инструмент по id
    public boolean deleteToolStock(int toolId) {
        ToolStockEntity e = new ToolStockEntity();
        e.id = toolId;
        db.toolStockDao().delete(e);
        return true;
    }

    // Найти инструмент по id
    public Optional<ToolStock> findToolStockById(int id) {
        for (ToolStockEntity e : db.toolStockDao().getAll()) {
            if (e.id == id) {
                ToolStock t = new ToolStock(e.name, e.category);
                t.setId(e.id);
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    // === RELATIONS ===

    public void linkMaterialToProject(int projectId, int materialId, int quantity) {
        ProjectMaterialCrossRef ref = new ProjectMaterialCrossRef();
        ref.projectId = projectId;
        ref.materialId = materialId;
        ref.quantity = quantity;
        db.projectMaterialDao().insert(ref);
    }

    public void linkToolToProject(int projectId, int toolId) {
        ProjectToolCrossRef ref = new ProjectToolCrossRef();
        ref.projectId = projectId;
        ref.toolId = toolId;
        db.projectToolDao().insert(ref);
        Log.d("DB", "Linked toolId " + toolId + " to projectId " + projectId);
    }
    public Optional<Project> findProjectById(int id) {
        // ищем саму сущность
        ProjectEntity pe = db.projectDao().findById(id);
        if (pe == null) return Optional.empty();

        Project project = new Project(pe.name, pe.craftType);
        project.setId(pe.id);

        // загружаем материалы
        List<ProjectMaterialCrossRef> pmRefs = db.projectMaterialDao().getByProjectId(id);
        List<MaterialCatalog> mats = new ArrayList<>();
        for (ProjectMaterialCrossRef ref : pmRefs) {
            MaterialEntity me = db.materialDao().findById(ref.materialId);
            MaterialCatalog m = new MaterialCatalog(me.name, me.color, me.unit);
            m.setId(me.id);
            // quantity можно сохранить в отдельное поле или DTO, но в вашем Project класс quantity не хранится
            mats.add(m);
        }
        project.setMaterials(mats);

        // загружаем инструменты
        List<ProjectToolCrossRef> ptRefs = db.projectToolDao().getByProjectId(id);
        List<ToolCatalog> tls = new ArrayList<>();
        for (ProjectToolCrossRef ref : ptRefs) {
            ToolEntity te = db.toolDao().findById(ref.toolId);
            ToolCatalog t = new ToolCatalog(te.name, te.category);
            t.setId(te.id);
            tls.add(t);
        }
        project.setTools(tls);

        return Optional.of(project);
    }



    public List<ProjectMaterialLinkDTO> getMaterialLinks() {
        return db.projectMaterialDao().getAllLinks();
    }

    public List<ProjectToolLinkDTO> getToolLinks() {
        return db.projectToolDao().getAllLinks();
    }

    public boolean unlinkMaterialFromProject(int projectId, int materialId) {
        return db.projectMaterialDao().deleteLink(projectId, materialId) > 0;
    }

    public boolean unlinkToolFromProject(int projectId, int toolId) {
        return db.projectToolDao().deleteLink(projectId, toolId) > 0;
    }

public List<Project> readProjectsWithMaterials() {
    List<Project> result = new ArrayList<>();
    // сначала базовые проекты
    for (ProjectEntity pe : db.projectDao().getAll()) {
        // можно переиспользовать findProjectById
        findProjectById(pe.id).ifPresent(result::add);
    }
    return result;
}
}
