package gui;

import model.DrawableImage;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import app.ColoringApp;

public class ColoringPanel extends JPanel {
    private DrawableImage image;
    private String currentTool = "fill";
    private Color currentColor = Color.BLACK;

    // Flaga zabezpieczająca przed operacjami w trakcie aktualizacji
    private volatile boolean isUpdating = false;

    public ColoringPanel(DrawableImage image) {
        this.image = image;
        setupMouseListeners();
        setBackground(Color.WHITE);
    }

    private void setupMouseListeners() {
        // Klasa anonimowa do obsługi myszy
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isUpdating && image != null) {
                    applyToolAt(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!isUpdating && image != null) {
                    applyToolAt(e.getX(), e.getY());
                }
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private void applyToolAt(final int x, final int y) {
        // Dodajemy sprawdzenie czy obraz istnieje
        if (image == null) return;

        // Sprawdzenie, czy kliknięcie nastąpiło w granicach obrazu
        int imgX = (getWidth() - image.getWidth()) / 2;
        int imgY = (getHeight() - image.getHeight()) / 2;

        // Współrzędne kliknięcia względem obrazu
        final int relativeX = x - imgX;
        final int relativeY = y - imgY;

        // Sprawdzenie, czy kliknięcie jest wewnątrz obrazu
        if (relativeX < 0 || relativeX >= image.getWidth() || relativeY < 0 || relativeY >= image.getHeight()) {
            return; // Kliknięcie poza obrazem, ignoruj
        }

        // Jeśli operacja może być długotrwała (np. fill), wykonujemy ją w tle
        if ("fill".equals(currentTool)) {
            // Sprawdzanie isUpdating powinno być tutaj, przed wykonaniem w tle
            if(isUpdating) return;
            isUpdating = true;

            // Kopia danych potrzebnych w wątku
            final Color colorCopy = new Color(currentColor.getRGB());

            ColoringApp.executeInBackground(new ColoringApp.BackgroundTask<Boolean>() {
                @Override
                public Boolean execute() throws Exception {
                    // Wykonaj wypełnianie w wątku roboczym używając współrzędnych względnych
                    image.fill(relativeX, relativeY, colorCopy);
                    return true;
                }

                @Override
                public void done(Boolean result) {
                    // Po zakończeniu odświeżamy panel (w EDT)
                    isUpdating = false;
                    repaint();
                }
            });
        } else {
            // Usunięto blok else, bo nie ma innych narzędzi
            // // Sprawdzanie isUpdating również dla innych narzędzi, jeśli to konieczne
            // if(isUpdating) return;
            //
            // // Dla prostszych operacji możemy działać bezpośrednio w EDT
            // if ("brush".equals(currentTool)) {
            //     image.colorAt(relativeX, relativeY, currentColor, brushSize);
            // } else if ("eraser".equals(currentTool)) {
            //     // Gumka jako biały pędzel
            //     image.colorAt(relativeX, relativeY, Color.WHITE, brushSize);
            // }
            // // Odświeżenie tylko jeśli coś się zmieniło
            // repaint();
            // Jeśli dodamy nowe narzędzia, logika pójdzie tutaj
            System.out.println("Unknown or unsupported tool: " + currentTool);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null && !isUpdating) {
            // Centrowanie obrazu
            int x = (getWidth() - image.getWidth()) / 2;
            int y = (getHeight() - image.getHeight()) / 2;

            image.draw(g, Math.max(0, x), Math.max(0, y),
                    Math.min(getWidth(), image.getWidth()),
                    Math.min(getHeight(), image.getHeight()));
        }
    }

    public void setImage(DrawableImage image) {
        // Zapewnienie, że zmiana obrazu nastąpi w EDT
        if (SwingUtilities.isEventDispatchThread()) {
            this.image = image;
            repaint();
        } else {
            SwingUtilities.invokeLater(() -> {
                this.image = image;
                repaint();
            });
        }
    }

    public void setCurrentTool(String tool) {
        this.currentTool = tool;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }
}