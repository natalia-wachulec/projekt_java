package model.shapes;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class Line implements Shape {
    private int x1, y1, x2, y2;
    private Line2D line;

    public Line(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        line = new Line2D.Double(x1, y1, x2, y2);
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawLine(x1, y1, x2, y2);
    }

    @Override
    public boolean contains(int x, int y) {
        // Sprawdzenie, czy punkt jest blisko linii
        // Można używać dystansu od linii z pewną tolerancją
        return line.ptSegDist(x, y) < 5.0;
    }
}