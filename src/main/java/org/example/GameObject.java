// GameObject.java
package org.example;

import java.awt.*;
import java.awt.geom.Area;

public abstract class GameObject {
    protected int x, y;
    protected int width, height;

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void update();
    public abstract void draw(Graphics g);

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean intersectsShape(Shape shape) {
        if (shape == null) return false;
        Area areaA = new Area(getBounds());
        Area areaB = new Area(shape);
        return areaA.intersects(areaB.getBounds2D());
    }

    public int getX() { return x; }
    public int getY() { return y; }
}