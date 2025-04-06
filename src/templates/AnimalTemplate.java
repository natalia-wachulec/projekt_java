package templates;

import model.DrawableImage;
import model.shapes.*;

public class AnimalTemplate extends ImageTemplate {

    public AnimalTemplate() {
        super("Kotek");
    }

    @Override
    public void createShapes(DrawableImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Głowa kota
        image.addShape(new Circle(width/2, height/3, 70));

        // Uszy
        image.addShape(new Triangle(
                width/2 - 40, height/3 - 50,  // Dolny lewy
                width/2 - 10, height/3 - 100, // Górny
                width/2 - 70, height/3 - 100  // Lewy
        ));

        image.addShape(new Triangle(
                width/2 + 40, height/3 - 50,  // Dolny prawy
                width/2 + 10, height/3 - 100, // Górny
                width/2 + 70, height/3 - 100  // Prawy
        ));

        // Oczy
        image.addShape(new Circle(width/2 - 25, height/3 - 10, 10));
        image.addShape(new Circle(width/2 + 25, height/3 - 10, 10));

        // Nos
        image.addShape(new Triangle(
                width/2, height/3 + 10,      // Górny
                width/2 - 10, height/3 + 25, // Lewy dolny
                width/2 + 10, height/3 + 25  // Prawy dolny
        ));

        // Wąsy
        image.addShape(new Line(width/2 - 10, height/3 + 20, width/2 - 60, height/3 + 15));
        image.addShape(new Line(width/2 - 10, height/3 + 20, width/2 - 60, height/3 + 25));
        image.addShape(new Line(width/2 + 10, height/3 + 20, width/2 + 60, height/3 + 15));
        image.addShape(new Line(width/2 + 10, height/3 + 20, width/2 + 60, height/3 + 25));

        // Uśmiech
        image.addShape(new Line(width/2 - 20, height/3 + 35, width/2 + 20, height/3 + 35));
    }
}