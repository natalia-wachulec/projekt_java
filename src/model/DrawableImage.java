package model;

import model.shapes.Shape;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DrawableImage {
    private BufferedImage baseImage;       // Obraz bazowy (kontur)
    private BufferedImage coloringLayer;   // Warstwa kolorowania
    private List<Shape> shapes;            // Lista kształtów tworzących obraz
    private int width, height;

    // Blokada do bezpiecznej synchronizacji wątków
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public DrawableImage(int width, int height) {
        this.width = width;
        this.height = height;
        this.shapes = new ArrayList<>();

        // Inicjalizacja obrazów
        baseImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        coloringLayer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Czyszczenie warstw
        clearLayer(baseImage);
        clearLayer(coloringLayer);
    }

    private static BufferedImage copyImage(BufferedImage source){
        if (source == null) return null;
        ColorModel cm = source.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = source.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private void clearLayer(BufferedImage layer) {
        Graphics2D g = layer.createGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, layer.getWidth(), layer.getHeight());
        g.dispose();
    }

    public void addShape(Shape shape) {
        lock.writeLock().lock();
        try {
            shapes.add(shape);
            redrawBaseImage();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void redrawBaseImage() {
        clearLayer(baseImage);
        Graphics2D g = baseImage.createGraphics();
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2.0f));

        // Rysowanie wszystkich kształtów
        for (Shape shape : shapes) {
            shape.draw(g);
        }

        g.dispose();
    }

    public void draw(Graphics g, int x, int y, int width, int height) {
        lock.readLock().lock();
        try {
            // Rysowanie obrazu bazowego
            g.drawImage(baseImage, x, y, width, height, null);
            // Nakładanie warstwy kolorowania
            g.drawImage(coloringLayer, x, y, width, height, null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void fill(int x, int y, Color color) {
        lock.writeLock().lock();
        try {
            // Implementacja wypełniania
            int targetColor = coloringLayer.getRGB(x, y);
            floodFill(x, y, targetColor, color.getRGB());
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Nierekurencyjna implementacja flood fill wykorzystująca stos
    private void floodFill(int x, int y, int targetColor, int replacementColor) {
        // Implementacja wykorzystująca stos zamiast rekurencji
        if (targetColor == replacementColor) return;

        // Sprawdzenie koloru linii (czarny) - Zmienione na sprawdzanie przezroczystości
        // int boundaryColorRGB = Color.BLACK.getRGB(); // Stara wersja

        java.util.Stack<Point> stack = new java.util.Stack<>();

        // Sprawdzenie początkowego punktu - czy nie jest linią lub poza granicami
        if (x < 0 || x >= width || y < 0 || y >= height || (baseImage.getRGB(x, y) >> 24) != 0) {
             // Jeśli kliknięto na linię lub poza obrazem, nie rób nic
            return;
        }
        if (coloringLayer.getRGB(x, y) == replacementColor) {
             // Jeśli kliknięto na obszar już w tym kolorze, nie rób nic
             return;
        }


        stack.push(new Point(x, y));

        while (!stack.isEmpty()) {
            Point p = stack.pop();
            int px = p.x;
            int py = p.y;

            // 1. Sprawdzenie granic obrazu
            if (px < 0 || px >= width || py < 0 || py >= height) continue;

            // 2. Sprawdzenie, czy piksel jest linią graniczną w baseImage (czy jest nieprzezroczysty)
            // Sprawdzamy kanał alfa - jeśli nie jest 0, to jest to linia lub coś narysowanego
            if ((baseImage.getRGB(px, py) >> 24) != 0) continue;


            // 3. Sprawdzenie, czy piksel w warstwie kolorowania ma kolor startowy
            if (coloringLayer.getRGB(px, py) != targetColor) continue;

            // 4. Ustawienie nowego koloru
            coloringLayer.setRGB(px, py, replacementColor);

            // 5. Dodanie sąsiadów do stosu
            stack.push(new Point(px + 1, py));
            stack.push(new Point(px - 1, py));
            stack.push(new Point(px, py + 1));
            stack.push(new Point(px, py - 1));
        }
    }

    public boolean isPointInShape(int x, int y) {
        lock.readLock().lock();
        try {
            for (Shape shape : shapes) {
                if (shape.contains(x, y)) {
                    return true;
                }
            }
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Metoda do generowania obrazu końcowego (np. do zapisania)
    public BufferedImage getFinalImage() {
        lock.readLock().lock();
        try {
            BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = result.createGraphics();
            g.drawImage(baseImage, 0, 0, null);
            g.drawImage(coloringLayer, 0, 0, null);
            g.dispose();
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }
}