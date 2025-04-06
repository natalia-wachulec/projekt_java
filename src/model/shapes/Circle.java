package model.shapes;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Circle implements Shape {
    private int x, y, radius;

    public Circle(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    @Override
    public boolean contains(int px, int py) {
        // Sprawdzenie, czy punkt (px, py) znajduje się wewnątrz koła
        double distance = Math.sqrt(Math.pow(px - x, 2) + Math.pow(py - y, 2));
        return distance <= radius;
    }
}