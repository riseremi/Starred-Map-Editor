/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rise.remi.components;

import java.awt.Point;
import java.awt.Rectangle;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Riseremi
 */
public class Teleport {

    private @Getter @Setter Rectangle teleport;
    private @Getter @Setter Point destination;

    public Teleport(Rectangle teleport, Point destination) {
        this.teleport = teleport;
        this.destination = destination;
    }

    public Rectangle getRectangle() {
        return teleport;
    }

    public int getDestinationX() {
        return destination.x;
    }

    public int getDestinationY() {
        return destination.y;
    }

    public int getSourceX() {
        return teleport.x;
    }

    public int getSourceY() {
        return teleport.y;
    }

    public int getWidth() {
        return (int) teleport.getWidth();
    }

    public int getHeight() {
        return (int) teleport.getHeight();
    }

    @Override
    public String toString() {
        return "From: " + getSourceX() + ":" + getSourceY() + ", to: " + getDestinationX() + ":" + getDestinationY();
    }
}
