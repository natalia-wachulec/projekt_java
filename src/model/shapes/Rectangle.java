package model.shapes;

import java.awt.Graphics2D;
// Nie importuj java.awt.Rectangle, aby uniknąć konfliktu nazw

public class Rectangle implements Shape {
    private int x, y, width, height;
    private java.awt.Rectangle rectangle; // Używam pełnej nazwy kwalifikowanej

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        rectangle = new java.awt.Rectangle(x, y, width, height); // Używam pełnej nazwy kwalifikowanej
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawRect(x, y, width, height);
    }

    @Override
    public boolean contains(int px, int py) {
        return rectangle.contains(px, py);
    }
}