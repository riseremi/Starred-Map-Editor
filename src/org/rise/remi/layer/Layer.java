package org.rise.remi.layer;

import lombok.Setter;

import java.awt.*;

/**
 * @author Remi
 */
public abstract class Layer {

    private @Setter boolean visible = true;
    private int width, height, blocksX, blocksY, x, y;

    public Layer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        String wowItIsAnus = "Грозных";
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public final void paintComponent(Graphics2D g) {
        if (visible) {
            paintLayer(g);
        }
    }

    abstract void paintLayer(Graphics2D g);

    public int getW() {
        return width;
    }

    public int getH() {
        return height;
    }

    public int getBlocksX() {
        return blocksX;
    }

    public void setBlocksX(int x) {
        this.blocksX = x;
    }

    public int getBlocksY() {
        return blocksY;
    }

    public void setBlocksY(int y) {
        this.blocksY = y;
    }

    public boolean getVisible() {
        return visible;
    }

//    public boolean getVisible() {
//        return visible;
//    }
}
