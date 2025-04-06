package templates;

import model.DrawableImage;
import model.shapes.*;

public class HouseTemplate extends ImageTemplate {

    public HouseTemplate() {
        super("Domek");
    }

    @Override
    public void createShapes(DrawableImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Podstawa domu
        image.addShape(new Rectangle(width/4, height/2, width/2, height/3));

        // Dach
        image.addShape(new Triangle(
                width/4, height/2,             // Lewy dolny punkt
                width/4 + width/2, height/2,   // Prawy dolny punkt
                width/2, height/4              // Górny punkt (szczyt dachu)
        ));

        // Drzwi
        image.addShape(new Rectangle(width/2 - 20, height/2 + height/6, 40, 80));

        // Okna
        image.addShape(new Rectangle(width/3, height/2 + height/10, 40, 40));
        image.addShape(new Rectangle(2*width/3 - 40, height/2 + height/10, 40, 40));

        // Komin
        image.addShape(new Rectangle(2*width/3, height/3, 20, 50));
    }
}