package com.example.coursework.gui;
/*
import domain.entity.MaterialCatalog;
import domain.entity.Project;
import domain.entity.ToolCatalog;
import domain.exceptions.ProjectException;
import domain.usecase.MaterialCatalogUseCase;
import domain.usecase.ProjectUseCase;
import domain.usecase.ToolCatalogUseCase;
import repository.dto.ProjectMaterialLinkDTO;
import repository.dto.ProjectToolLinkDTO;
import repository.indatabase.DBProjectRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class SimpleTestGUI {
    private final ProjectUseCase projectUseCase;
    private final MaterialCatalogUseCase materialCatalogUseCase;
    private final ToolCatalogUseCase toolCatalogUseCase;
    private DBProjectRepository projectRepo;
    private JFrame frame;
    private JTabbedPane tabbedPane;

    // Модели таблиц
    private final DefaultTableModel projectsModel = new DefaultTableModel(new Object[]{"Название", "Тип"}, 0);
    private final DefaultTableModel materialsModel = new DefaultTableModel(new Object[]{"Название", "Цвет"}, 0);
    private final DefaultTableModel toolsModel = new DefaultTableModel(new Object[]{"Название", "Категория"}, 0);
    private final DefaultTableModel projectMaterialsModel = new DefaultTableModel(new Object[]{"Проект", "Материал","Цвет", "Количество"}, 0);
    private final DefaultTableModel projectToolsModel = new DefaultTableModel(new Object[]{"Проект", "Инструмент","Категория"}, 0);

    private JTable projectMaterialsTable;
    private JTable projectToolsTable;

    public SimpleTestGUI(ProjectUseCase projectUseCase,
                         MaterialCatalogUseCase materialCatalogUseCase,
                         ToolCatalogUseCase toolCatalogUseCase, DBProjectRepository projectRepo) {
        this.projectUseCase = projectUseCase;
        this.materialCatalogUseCase = materialCatalogUseCase;
        this.toolCatalogUseCase = toolCatalogUseCase;
        this.projectRepo = projectRepo;
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Crafting Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        tabbedPane = new JTabbedPane();
        initializeProjectsTab();
        initializeMaterialsTab();
        initializeToolsTab();
        initializeLinksTab();
        initializeLinksTabTools();


        frame.add(tabbedPane);
        frame.setVisible(true);
        // Загружаем данные из базы данных
        loadDataFromDatabase();
    }

    // Метод для загрузки данных из базы данных
    private void loadDataFromDatabase() {
        // Загрузка проектов
        try {
            List<Project> projects = projectUseCase.getAllProjects();
            for (Project project : projects) {
                projectsModel.addRow(new Object[]{project.getName(), project.getCraftType()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error loading projects: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Загрузка материалов
        try {
            List<MaterialCatalog> materials = materialCatalogUseCase.getAllMaterials();
            for (MaterialCatalog material : materials) {
                materialsModel.addRow(new Object[]{material.getName(), material.getColor()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error loading materials: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Загрузка инструментов
        try {
            List<ToolCatalog> tools = toolCatalogUseCase.getAllTools();
            for (ToolCatalog tool : tools) {
                toolsModel.addRow(new Object[]{tool.getName(), tool.getCategory()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error loading tools: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Загрузка материалов для проектов
        try {
            List<ProjectMaterialLinkDTO> links = projectRepo.getMaterialLinks();
            for (ProjectMaterialLinkDTO link : links) {
                projectMaterialsModel.addRow(new Object[]{
                        link.getProjectName(),
                        link.getMaterialName(),
                        link.getColor(),
                        link.getQuantity()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Unknown error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        try {
            List<ProjectToolLinkDTO> links = projectRepo.getToolLinks();
            for (ProjectToolLinkDTO link : links) {
                projectToolsModel.addRow(new Object[]{
                        link.getProjectName(),
                        link.getToolName(),
                        link.getCategory()

                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Unknown error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Инициализация вкладки проектов
    private void initializeProjectsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(projectsModel);

        JButton addButton = new JButton("New Project");
        JButton linkButton = new JButton("Add Material to Project");
        JButton linkToolButton = new JButton("Add Tool to Project");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddProject();
            }
        });

        linkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLinkMaterial();
            }
        });
        linkToolButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLinkTool();
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.add(addButton);
        buttonPanel.add(linkButton);
        buttonPanel.add(linkToolButton);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Projects", panel);
    }

    // Инициализация вкладки материалов
    private void initializeMaterialsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(materialsModel);
        JButton addButton = new JButton("New Material");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddMaterial();
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.SOUTH);

        tabbedPane.addTab("Materials", panel);
    }

    // Инициализация вкладки инструментов
    private void initializeToolsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(toolsModel);
        JButton addButton = new JButton("New Tool");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddTool();
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.SOUTH);

        tabbedPane.addTab("Tools", panel);
    }

    private void initializeLinksTab() {
        projectMaterialsTable = new JTable(projectMaterialsModel); // Используем существующую модель
        JScrollPane scrollPane = new JScrollPane(projectMaterialsTable);
        tabbedPane.addTab("Материалы проектов", scrollPane);
    }

    private void initializeLinksTabTools() {
        projectToolsTable = new JTable(projectToolsModel); // Используем существующую модель
        JScrollPane scrollPane = new JScrollPane(projectToolsTable);
        tabbedPane.addTab("Инструменты проектов", scrollPane);
    }
    // Обработчики событий
    private void handleAddProject() {
        String name = JOptionPane.showInputDialog(frame, "Project name:");
        String type = JOptionPane.showInputDialog(frame, "Project type:");

        if (name != null && !name.trim().isEmpty() && type != null && !type.trim().isEmpty()) {
            try {
                Project project = projectUseCase.createProject(name, type);
                projectsModel.addRow(new Object[]{project.getName(), project.getCraftType()});
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleAddMaterial() {
        String name = JOptionPane.showInputDialog(frame, "Material name:");
        String color = JOptionPane.showInputDialog(frame, "Material color:");

        if (name != null && !name.trim().isEmpty() && color != null && !color.trim().isEmpty()) {
            try {
                MaterialCatalog material = materialCatalogUseCase.createNewMaterialCatalog(name, color);
                materialsModel.addRow(new Object[]{material.getName(), material.getColor()});
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleAddTool() {
        String name = JOptionPane.showInputDialog(frame, "Tool name:");
        String category = JOptionPane.showInputDialog(frame, "Tool category:");

        if (name != null && !name.trim().isEmpty() && category != null && !category.trim().isEmpty()) {
            try {
                ToolCatalog tool = toolCatalogUseCase.createNewToolCatalog(name, category);
                toolsModel.addRow(new Object[]{tool.getName(), tool.getCategory()});
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleLinkMaterial() {
        try {
            // Получение списка проектов
            List<Project> projects = projectUseCase.getAllProjects();
            Project selectedProject = (Project) JOptionPane.showInputDialog(
                    frame,
                    "Выберите проект:",
                    "Выбор проекта",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    projects.toArray(),
                    projects.isEmpty() ? null : projects.get(0)
            );

            // Получение списка материалов
            List<MaterialCatalog> materials = projectUseCase.getAllMaterials();
            MaterialCatalog selectedMaterial = (MaterialCatalog) JOptionPane.showInputDialog(
                    frame,
                    "Выберите материал:",
                    "Выбор материала",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    materials.toArray(),
                    materials.isEmpty() ? null : materials.get(0)
            );

            // Ввод количества
            String quantityStr = JOptionPane.showInputDialog(frame, "Введите количество:");
            if (quantityStr == null || quantityStr.trim().isEmpty()) return;
            int quantity = Integer.parseInt(quantityStr);

            // Сохранение связи
            projectUseCase.linkMaterialToProject(
                    selectedProject.getId(), // int
                    selectedMaterial.getId(), // int
                    quantity
            );

            refreshProjectMaterialsTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Ошибка SQL: " + ex.getMessage(), "Ошибка SQL", JOptionPane.ERROR_MESSAGE);
        } catch (ProjectException e) {
            throw new RuntimeException(e);
        }
    }
    private void handleLinkTool() {
        try {
            // Получение списка проектов
            List<Project> projects = projectUseCase.getAllProjects();
            Project selectedProject = (Project) JOptionPane.showInputDialog(
                    frame,
                    "Выберите проект:",
                    "Выбор проекта",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    projects.toArray(),
                    projects.isEmpty() ? null : projects.get(0)
            );

            // Получение списка материалов
            List<ToolCatalog> tools = toolCatalogUseCase.getAllTools();
            ToolCatalog selectedTool = (ToolCatalog) JOptionPane.showInputDialog( // Исправлено MaterialCatalog -> ToolCatalog
                    frame,
                    "Выберите инструмент:",
                    "Выбор инструмента", // Исправлено текст диалога
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    tools.toArray(),
                    tools.isEmpty() ? null : tools.get(0)
            );

            projectUseCase.linkToolToProject(
                    selectedProject.getId(),
                    selectedTool.getId() // Исправлено selectedMaterial -> selectedTool
            );
            refreshProjectToolsTable();
        } catch (Exception ex) {
            ex.printStackTrace();
        }}
    private void refreshProjectMaterialsTable() {
        DefaultTableModel model = (DefaultTableModel) projectMaterialsTable.getModel();
        model.setRowCount(0); // Очистка таблицы

        List<ProjectMaterialLinkDTO> links = projectRepo.getMaterialLinks();
        // Логируем, сколько строк добавляется в таблицу
        for (ProjectMaterialLinkDTO link : links) {
            System.out.println("Adding to table - Project: " + link.getProjectName() + ", Material: " + link.getMaterialName() + ", Quantity: " + link.getQuantity());
            model.addRow(new Object[]{
                    link.getProjectName(),
                    link.getMaterialName(),
                    link.getQuantity()
            });
        }
    }

    public static void show(final ProjectUseCase projectUseCase,
                            final MaterialCatalogUseCase materialCatalogUseCase,
                            final ToolCatalogUseCase toolCatalogUseCase, final DBProjectRepository projectRepo) {
        SwingUtilities.invokeLater(() -> new SimpleTestGUI(projectUseCase, materialCatalogUseCase, toolCatalogUseCase, projectRepo));
    }


    private void refreshProjectToolsTable() {
        DefaultTableModel model = (DefaultTableModel) projectToolsTable.getModel();
        model.setRowCount(0); // Очистка таблицы

        List<ProjectToolLinkDTO> links = projectRepo.getToolLinks();
        for (ProjectToolLinkDTO link : links) {
            System.out.println("Adding to table - Project: " + link.getProjectName() + ", Tool: " + link.getToolName() + ", Category: " + link.getCategory());
            model.addRow(new Object[]{
                    link.getProjectName(),
                    link.getToolName(),
                    link.getCategory()
            });
        }
    }
    }*/
