package org.rise.remi.blocks;

import java.awt.Point;
import java.awt.Rectangle;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Riseremi
 */
public class Message {

    private @Getter @Setter Rectangle rectangle;
    private @Getter @Setter String text;

    public Message(Rectangle rectangle, String text) {
        this.rectangle = rectangle;
        this.text = text;
    }

    public int getSourceX() {
        return rectangle.x;
    }

    public int getSourceY() {
        return rectangle.y;
    }

    public int getWidth() {
        return (int) rectangle.getWidth();
    }

    public int getHeight() {
        return (int) rectangle.getHeight();
    }

    @Override
    public String toString() {
        return "x: " + getSourceX() + ", y: " + getSourceY() + " - " + getText();
    }
}
