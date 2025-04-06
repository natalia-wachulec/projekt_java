package gui;

import model.DrawableImage;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import app.ColoringApp;

public class ColoringPanel extends JPanel {
    private DrawableImage image;
    private Color currentColor = Color.BLACK;

    // Flaga zabezpieczająca przed operacjami w trakcie aktualizacji
    private volatile boolean isUpdating = false;

    public ColoringPanel(DrawableImage image) {
        this.image = image;
        setupMouseListeners();
        setBackground(Color.WHITE);
    }

    private void setupMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isUpdating && image != null) {
                    applyFillAction(e.getX(), e.getY());
                }
            }
        };

        addMouseListener(mouseHandler);
    }

    private void applyFillAction(final int x, final int y) {
        if (image == null || isUpdating) return;

        int imgX = (getWidth() - image.getWidth()) / 2;
        int imgY = (getHeight() - image.getHeight()) / 2;
        final int relativeX = x - imgX;
        final int relativeY = y - imgY;

        if (relativeX < 0 || relativeX >= image.getWidth() || relativeY < 0 || relativeY >= image.getHeight()) {
            return;
        }

        isUpdating = true;
        final Color colorCopy = new Color(currentColor.getRGB());

        ColoringApp.executeInBackground(new ColoringApp.BackgroundTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                image.fill(relativeX, relativeY, colorCopy);
                return true;
            }

            @Override
            public void done(Boolean result) {
                isUpdating = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null && !isUpdating) {
            int x = (getWidth() - image.getWidth()) / 2;
            int y = (getHeight() - image.getHeight()) / 2;

            image.draw(g, Math.max(0, x), Math.max(0, y),
                    Math.min(getWidth(), image.getWidth()),
                    Math.min(getHeight(), image.getHeight()));
        }
    }

    public void setImage(DrawableImage image) {
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

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }
}