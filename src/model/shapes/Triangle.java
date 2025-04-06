package model.shapes;

import java.awt.Graphics2D;
import java.awt.Polygon;

public class Triangle implements Shape {
    private int x1, y1, x2, y2, x3, y3;
    private Polygon polygon;

    public Triangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;

        // Utworzenie wielokąta do rysowania i testowania zawierania punktu
        polygon = new Polygon();
        polygon.addPoint(x1, y1);
        polygon.addPoint(x2, y2);
        polygon.addPoint(x3, y3);
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawPolygon(polygon);
    }

    @Override
    public boolean contains(int x, int y) {
        return polygon.contains(x, y);
    }
}