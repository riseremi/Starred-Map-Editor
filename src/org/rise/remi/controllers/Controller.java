package org.rise.remi.controllers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import lombok.Getter;
import org.rise.remi.layer.TiledLayer;
import org.rise.remi.mapeditor.Clipboard;
import org.rise.remi.mapeditor.Core;
import org.rise.remi.mapeditor.MapEditor;
import org.rise.remi.mapeditor.World;

/**
 * @author remi
 */
public class Controller implements KeyListener {

    private static @Getter boolean isCtrlPressed;

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (!ke.isControlDown()) { //чтобы при изменении размера кисти не конфликтовало
            //deleting selected message or teleport from the list
            if ((ke.getKeyCode() == KeyEvent.VK_DELETE)) {
                switch (MapEditor.getTab().getSelectedIndex()) {
                    case 1:
                        MapEditor.deleteCurrentTeleport();
                        break;
                    case 2:
                        MapEditor.deleteCurrentMessage();
                        break;
                    default:
                        break;
                }
            }
            //move up
            if ((ke.getKeyCode() == KeyEvent.VK_UP)) {
                Core.getInstance().getWorld().getWorldLayer().move(TiledLayer.MOVE_NONE, TiledLayer.MOVE_UP);
                Core.getInstance().repaint();
            }

            //move down
            if ((ke.getKeyCode() == KeyEvent.VK_DOWN)) {
                Core.getInstance().getWorld().getWorldLayer().move(TiledLayer.MOVE_NONE, TiledLayer.MOVE_DOWN);
                Core.getInstance().repaint();
            }

            //move left
            if ((ke.getKeyCode() == KeyEvent.VK_LEFT)) {
                Core.getInstance().getWorld().getWorldLayer().move(TiledLayer.MOVE_LEFT, TiledLayer.MOVE_NONE);
                Core.getInstance().repaint();
            }

            //move right
            if ((ke.getKeyCode() == KeyEvent.VK_RIGHT)) {
                Core.getInstance().getWorld().getWorldLayer().move(TiledLayer.MOVE_RIGHT, TiledLayer.MOVE_NONE);
                Core.getInstance().repaint();
            }
        }


        isCtrlPressed = ke.isControlDown();


        //увеличить и уменьшить размер кисти, Ctrl + Up/Down, возможно, не лучшая комбинация
        if (ke.isControlDown() && (ke.getKeyCode() == KeyEvent.VK_UP)) {
            Core.getInstance().getMainCursor().increaseBrushSize();
        }

        if (ke.isControlDown() && (ke.getKeyCode() == KeyEvent.VK_DOWN)) {
            Core.getInstance().getMainCursor().decreaseBrushSize();
        }

        //смена слоёв
        if (ke.isControlDown() && (ke.getKeyCode() == KeyEvent.VK_RIGHT)) {
            World world = Core.getInstance().getWorld();
            if (world.getLayer() < 2) {
                world.setLayer(world.getLayer() + 1);
            }
        }

        if (ke.isControlDown() && (ke.getKeyCode() == KeyEvent.VK_LEFT)) {
            World world = Core.getInstance().getWorld();
            if (world.getLayer() > 0) {
                world.setLayer(world.getLayer() - 1);
            }
        }

        //move region mode
        if ((MyMouseController.getState() == MyMouseController.MODE_SELECTION)) {
            int x1 = MyMouseController.getX1();
            int y1 = MyMouseController.getY1();
            int x2 = MyMouseController.getX2();
            int y2 = MyMouseController.getY2();
            int w = MyMouseController.getWidth();
            int h = MyMouseController.getHeight();

            if (ke.isControlDown()) {
                //paste
                if (ke.getKeyCode() == KeyEvent.VK_V) {
                    x2 = Core.getInstance().getMainCursor().getAbsoluteX();
                    y2 = Core.getInstance().getMainCursor().getAbsoluteY();
                    Core.getInstance().getWorld().getNullLayer().pasteFromClipboard(x2, y2, 0, true);
                    Core.getInstance().getWorld().getWorldLayer().pasteFromClipboard(x2, y2, 1, true);
                    Core.getInstance().getWorld().getObjectsLayer().pasteFromClipboard(x2, y2, 2, true);
                }
                //copy
                if ((ke.getKeyCode() == KeyEvent.VK_C) && MyMouseController.isStartSet() && MyMouseController.isEndSet()) {
                    Clipboard.getInstance().clear();
                    Core.getInstance().getWorld().getNullLayer().toClipboard(w, h, x1, y1, Clipboard.MODE_COPY);
                    Core.getInstance().getWorld().getWorldLayer().toClipboard(w, h, x1, y1, Clipboard.MODE_COPY);
                    Core.getInstance().getWorld().getObjectsLayer().toClipboard(w, h, x1, y1, Clipboard.MODE_COPY);

                    MyMouseController.resetSelection();
                }
                //cut
                if ((ke.getKeyCode() == KeyEvent.VK_X) && MyMouseController.isStartSet() && MyMouseController.isEndSet()) {
                    Clipboard.getInstance().clear();
                    Core.getInstance().getWorld().getNullLayer().toClipboard(w, h, x1, y1, Clipboard.MODE_CUT);
                    Core.getInstance().getWorld().getWorldLayer().toClipboard(w, h, x1, y1, Clipboard.MODE_CUT);
                    Core.getInstance().getWorld().getObjectsLayer().toClipboard(w, h, x1, y1, Clipboard.MODE_CUT);

                    MyMouseController.resetSelection();
                }

            }

            //just delete region
            if ((ke.getKeyCode() == KeyEvent.VK_DELETE) && MyMouseController.isStartSet() && MyMouseController.isEndSet()) {
                Core.getInstance().getWorld().getNullLayer().fillRectTile(x1, y1, w, h, -1);
                Core.getInstance().getWorld().getWorldLayer().fillRectTile(x1, y1, w, h, -1);
                Core.getInstance().getWorld().getObjectsLayer().fillRectTile(x1, y1, w, h, -1);

                MyMouseController.resetSelection();
            }
        }

        if (ke.getKeyCode() == KeyEvent.VK_F12) {
            Core.getInstance().takeScreenshot();
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        isCtrlPressed = ke.isControlDown();
    }
}
