package templates;

import model.DrawableImage;
import model.shapes.*;

public class FlowerTemplate extends ImageTemplate {

    public FlowerTemplate() {
        super("Kwiatek");
    }

    @Override
    public void createShapes(DrawableImage image) {
        // Środek kwiatka
        image.addShape(new Circle(image.getWidth()/2, image.getHeight()/2, 30));

        // Płatki
        int centerX = image.getWidth()/2;
        int centerY = image.getHeight()/2;
        int petalRadius = 25;
        int distance = 60;

        // Górny płatek
        image.addShape(new Circle(centerX, centerY - distance, petalRadius));
        // Prawy płatek
        image.addShape(new Circle(centerX + distance, centerY, petalRadius));
        // Dolny płatek
        image.addShape(new Circle(centerX, centerY + distance, petalRadius));
        // Lewy płatek
        image.addShape(new Circle(centerX - distance, centerY, petalRadius));

        // Łodyga
        image.addShape(new Rectangle(centerX - 5, centerY + 30, 10, 100));

        // Liść
        image.addShape(new Triangle(
                centerX, centerY + 70,    // Punkt przyłączenia do łodygi
                centerX - 40, centerY + 50,  // Lewy punkt
                centerX - 20, centerY + 90   // Dolny punkt
        ));
    }
}