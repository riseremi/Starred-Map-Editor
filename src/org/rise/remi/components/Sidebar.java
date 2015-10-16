package org.rise.remi.components;

import org.rise.remi.mapeditor.Global;
import org.rise.remi.mapeditor.Core;
import lombok.Getter;
import org.rise.remi.layer.TiledLayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * @author remi
 */
public class Sidebar extends JPanel {

    private final int height = 64;
    private int translateX = Global.windowWidth, translateY = Global.windowHeight;
    private @Getter
    TiledLayer tileList;
    private int tile;
    private int curX, curY;

    public Sidebar() {
        try {
            tileList = new TiledLayer(ImageIO.read(getClass().getResourceAsStream(Global.PATH_TO_TILESET)), Global.tileWidth, Global.tileHeight, Global.sidebarWidth, height, false);
        } catch (IOException ex) {
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < Global.sidebarWidth; j++) {
                tileList.setTile(j, i, tile++);
            }
        }
        //tileList.setBlocksX(24);
    }

    public void moveUp() {
        //tileList.moveUp();
        tileList.move(TiledLayer.MOVE_NONE, TiledLayer.MOVE_UP);
        repaint();
    }

    public void moveDown() {
        //tileList.moveDown();
        tileList.move(TiledLayer.MOVE_NONE, TiledLayer.MOVE_DOWN);
        repaint();
    }

//    public TiledLayer getTileList() {
//        return tileList;
//    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, 352, translateY);

        tileList.paintLayer(g2);
        //paint grid
        g.setColor(Color.WHITE);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < Global.sidebarWidth; j++) {
                //g.drawRect(j * Global.tileWidth, i * Global.tileWidth, Global.tileWidth, Global.tileHeight);
            }
        }
        g2.setColor(Core.getInstance().getSidebarCursor().getC());
        g2.draw3DRect(curX * Global.tileWidth, tileList.getBlocksY() * Global.tileHeight - curY * Global.tileHeight, Global.tileWidth, Global.tileHeight, false);
        Core.getInstance().getSidebarCursor().paint(g2);
    }

    public void setCursorCoords(int curX, int curY) {
        this.curX = curX;
        this.curY = curY;
    }

    public int getTileId(int x, int y) {
        return tileList.getTile(x, y);
    }
}
