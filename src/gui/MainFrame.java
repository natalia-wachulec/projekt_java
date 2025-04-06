package gui;

import model.DrawableImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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

        // Nasłuch koloru
        toolPanel.addColorChangeListener(e -> {
            SwingUtilities.invokeLater(() -> {
                coloringPanel.setCurrentColor(toolPanel.getCurrentColor());
            });
        });

        // Nasłuch przycisku Zapisz
        toolPanel.addSaveActionListener(() -> {
            SwingUtilities.invokeLater(this::saveImageAction);
        });
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

    // Akcja zapisywania obrazu
    private void saveImageAction() {
        synchronized (imageLock) {
            if (currentImage == null) {
                JOptionPane.showMessageDialog(this, "Nie ma obrazu do zapisania.", "Błąd zapisu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!(currentImage instanceof DrawableImage)) {
                 JOptionPane.showMessageDialog(this, "Błąd wewnętrzny: Nieprawidłowy typ obrazu.", "Błąd zapisu", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            DrawableImage drawableImage = (DrawableImage) currentImage;

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Zapisz jako PNG");
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Pliki PNG (*.png)", "png");
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setFileFilter(filter);

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".png")) {
                    fileToSave = new File(filePath + ".png");
                }
                try {
                    BufferedImage finalImage = drawableImage.getFinalImage();
                    if (finalImage != null) {
                        boolean success = ImageIO.write(finalImage, "png", fileToSave);
                        if (success) {
                            JOptionPane.showMessageDialog(this, "Obraz zapisano pomyślnie!", "Zapisano", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "Nie znaleziono odpowiedniego modułu zapisu dla PNG.", "Błąd zapisu", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Nie można uzyskać obrazu do zapisu (metoda zwróciła null).", "Błąd zapisu", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Wystąpił błąd we/wy podczas zapisu pliku: " + ex.getMessage(), "Błąd zapisu", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                     ex.printStackTrace();
                     JOptionPane.showMessageDialog(this, "Wystąpił nieoczekiwany błąd: " + ex.getMessage(), "Błąd zapisu", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // Klasa do obsługi zdarzeń zamykania okna
    private class WindowHandler extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            // Polskie napisy dla przycisków
            Object[] options = {"Tak, zapisz", "Nie, zamknij", "Anuluj"};
            int response = JOptionPane.showOptionDialog(MainFrame.this,
                    "Czy chcesz zapisać swoją pracę przed zamknięciem?",
                    "Zamykanie aplikacji",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,     // Ikona domyślna
                    options,  // Polskie napisy
                    options[0]); // Domyślny wybór

            if (response == JOptionPane.YES_OPTION) { // Indeks 0: "Tak, zapisz"
                 saveImageAction();
                 dispose();
            } else if (response == JOptionPane.NO_OPTION) { // Indeks 1: "Nie, zamknij"
                dispose();
            } else if (response == JOptionPane.CANCEL_OPTION || response == JOptionPane.CLOSED_OPTION) { // Indeks 2: "Anuluj" lub zamknięcie okna dialogowego
                 // Nic nie rób, okno główne pozostaje otwarte
                 // setDefaultCloseOperation jest już na DO_NOTHING_ON_CLOSE
            }
        }
    }
}