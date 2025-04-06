package gui;

import model.DrawableImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import model.shapes.ImageTemplate;
import app.ColoringApp;

public class MainFrame extends JFrame {
    private ColoringPanel coloringPanel;
    private ToolPanel toolPanel;
    private JComboBox<String> templateSelector;
    private DrawableImage currentImage;

    // Potrzebny do synchronizacji
    private final Object imageLock = new Object();

    public MainFrame(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);

        // Dodanie obsługi zamknięcia okna
        addWindowListener(new WindowHandler());

        // Inicjalizacja komponentów w EDT
        initComponents();
        setupListeners();

        // Załadowanie pierwszego szablonu
        SwingUtilities.invokeLater(() -> loadTemplate(0));
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel wyboru szablonu
        JPanel templatePanel = new JPanel();
        templatePanel.add(new JLabel("Wybierz obrazek:"));

        templateSelector = new JComboBox<>();
        // Dodanie nazw wszystkich dostępnych szablonów
        for (ImageTemplate template : ColoringApp.TemplateRegistry.getTemplates()) {
            templateSelector.addItem(template.getName());
        }
        templatePanel.add(templateSelector);

        // Przycisk "Nowy obrazek"
        JButton resetButton = new JButton("Nowy obrazek");
        resetButton.addActionListener(e -> {
            int selectedIndex = templateSelector.getSelectedIndex();
            loadTemplate(selectedIndex);
        });
        templatePanel.add(resetButton);

        add(templatePanel, BorderLayout.NORTH);

        // Panel do kolorowania
        coloringPanel = new ColoringPanel(null);
        add(new JScrollPane(coloringPanel), BorderLayout.CENTER);

        // Panel narzędzi
        toolPanel = new ToolPanel();
        add(toolPanel, BorderLayout.EAST);
    }

    private void setupListeners() {
        // Nasłuch wyboru szablonu
        templateSelector.addActionListener(e -> {
            int selectedIndex = templateSelector.getSelectedIndex();
            loadTemplate(selectedIndex);
        });

        // Nasłuch narzędzi
        toolPanel.addToolChangeListener(tool -> {
            SwingUtilities.invokeLater(() -> {
                coloringPanel.setCurrentTool(tool);
                // Opcjonalnie: zmiana kursora w zależności od narzędzia
                updateCursor(tool);
            });
        });

        // Nasłuch koloru
        toolPanel.addColorChangeListener(e -> {
            SwingUtilities.invokeLater(() -> {
                coloringPanel.setCurrentColor(toolPanel.getCurrentColor());
            });
        });
    }

    private void updateCursor(String tool) {
        if ("fill".equals(tool)) {
            coloringPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            coloringPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private void loadTemplate(final int index) {
        // Utworzenie obrazka może być czasochłonne, więc wykonujemy w tle
        ColoringApp.executeInBackground(new ColoringApp.BackgroundTask<DrawableImage>() {
            @Override
            public DrawableImage execute() throws Exception {
                ImageTemplate template = ColoringApp.TemplateRegistry.getTemplate(index);
                if (template != null) {
                    return template.createImage(600, 500);
                }
                return null;
            }

            @Override
            public void done(DrawableImage result) {
                // Aktualizacja w EDT
                if (result != null) {
                    synchronized (imageLock) {
                        currentImage = result;
                        coloringPanel.setImage(currentImage);
                    }
                }
            }
        });
    }

    // Klasa do obsługi zdarzeń zamykania okna
    private class WindowHandler extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            // Zapytanie czy zapisać przed zamknięciem
            int response = JOptionPane.showConfirmDialog(
                    MainFrame.this,
                    "Czy chcesz zapisać swoją pracę przed zamknięciem?",
                    "Zamykanie aplikacji",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                // Implementacja zapisywania (w EDT)
                SwingUtilities.invokeLater(() -> {
                    // TODO: Zaimplementuj zapisywanie obrazu
                    System.out.println("Zapisywanie obrazu...");
                    dispose();
                });
            } else if (response == JOptionPane.CANCEL_OPTION) {
                // Anuluj zamykanie
                setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            } else {
                // Zamknij bez zapisywania
                dispose();
            }
        }
    }
}