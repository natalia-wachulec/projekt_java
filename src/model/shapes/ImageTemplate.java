package model.shapes;

import model.DrawableImage;

public abstract class ImageTemplate {
    private String name;

    public ImageTemplate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Metoda, którą muszą zaimplementować konkretne szablony
    public abstract void createShapes(DrawableImage image);

    // Fabryka tworząca gotowy obraz z szablonu
    public DrawableImage createImage(int width, int height) {
        DrawableImage image = new DrawableImage(width, height);
        createShapes(image);
        return image;
    }
}