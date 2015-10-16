package org.rise.remi.components;

import org.rise.remi.mapeditor.Global;
import org.rise.remi.mapeditor.Core;
import java.awt.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author remi
 */
public class MouseCursor {

    public static final int MAX_SIZE = 12, MIN_SIZE = 1;
    private @Getter @Setter int x, y, tileId;
    private int absoluteX, absoluteY;
    private @Getter @Setter boolean isPaint;
    private @Getter Color c;
    private @Getter @Setter int size = 1;
    private @Getter @Setter String caption;

    public MouseCursor(Color c) {
        this.c = c;
    }

    public void increaseBrushSize() {
        if (size < MAX_SIZE) {
            size++;
        }
    }

    public void decreaseBrushSize() {
        if (size > MIN_SIZE) {
            size--;
        }
    }

    public void paint(Graphics g) {
        if (isPaint) {
            g.setColor(c);
            if (size > MIN_SIZE) {
                g.drawRect(x * Global.tileWidth, y * Global.tileWidth, Global.tileWidth * size, Global.tileHeight * size);
            } else {
                g.draw3DRect(x * Global.tileWidth, y * Global.tileWidth, Global.tileWidth, Global.tileHeight, false);

            }
        }
        Core.getInstance().repaint();
    }

    public void setCoords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void incTileId() {
        if (tileId != 20) {
            tileId++;
        } else {
            tileId = 0;
        }
    }

    public int getAbsoluteX() {
        return x - Core.getInstance().getWorld().getWorldLayer().getBlocksX();
    }

    public int getAbsoluteY() {
        return y - Core.getInstance().getWorld().getWorldLayer().getBlocksY();
    }
}
