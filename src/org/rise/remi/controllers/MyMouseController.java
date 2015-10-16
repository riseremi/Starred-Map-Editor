package org.rise.remi.controllers;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JOptionPane;
import lombok.Getter;
import lombok.Setter;
import org.rise.remi.blocks.Message;
import org.rise.remi.blocks.Teleport;
import org.rise.remi.components.MouseCursor;
import org.rise.remi.layer.LayerLoader;
import org.rise.remi.layer.TiledLayer;
import org.rise.remi.mapeditor.Core;
import org.rise.remi.mapeditor.FloodFill;
import org.rise.remi.mapeditor.Global;
import org.rise.remi.mapeditor.MapEditor;
import org.rise.remi.mapeditor.World;

/**
 * @author remi
 */
public class MyMouseController implements MouseMotionListener, MouseListener {

    private boolean isPressed = false, isMain;
    public final static int STATE_PAINT = 0, STATE_MESSAGE = 1, MODE_TELEPORT = 2, MODE_SELECTION = 3, STATE_FILL = 4;
    //copy mode vars
    private static @Getter @Setter boolean isStartSet, isEndSet;
    private static @Getter @Setter int x1, y1, x2, y2, width, height;
    //
    private int tile = -1;
    private static @Getter @Setter int state = 0;
    private Core core;
    private MapEditor main;
    private World world;
    private boolean teleportFixed = true;
    private Point teleportSource;

    public MyMouseController(boolean isMain, MapEditor main) {
        this.isMain = isMain;
        this.core = Core.getInstance();
        this.main = main;
        this.world = core.getWorld();
    }

    //welcome to reality
    //reality to welcomr
    //asuyjoe to jhgutie
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    public TiledLayer getCurrentLayer() {
        int layerNum = world.getLayer();

        switch (layerNum) {
            default:
                return world.getNullLayer();
            case 0:
                if (world.getNullLayer().getVisible()) {
                    return world.getNullLayer();
                } else {
                    return world.getWorldLayer();
                }
            case 1:
                return world.getWorldLayer();
            case 2:
                return world.getObjectsLayer();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        MouseCursor mainCursor = core.getMainCursor();
        MouseCursor sideCursor = core.getSidebarCursor();

        int x = core.getMainCursor().getAbsoluteX(), y = core.getMainCursor().getAbsoluteY();
        int curX = e.getX() / Global.tileWidth, curY = e.getY() / Global.tileHeight;

        if (state == STATE_PAINT) {
            switch (e.getButton()) {
                case MouseEvent.BUTTON1:
                    tile = mainCursor.getTileId();
                    break;
                case MouseEvent.BUTTON3:
                    tile = -1;
                    break;
            }
        }

        //autotiles test
        //core.getMainCursor().setSize(2);
        //int aSize = core.getMainCursor().getSize();
        //corner tiles
        //int upLeft = getCurrentLayer().getTile(x, y);
        //int upRight = getCurrentLayer().getTile(++x, y);
        //int downLeft = getCurrentLayer().getTile(x, ++y);
        //int downRight = getCurrentLayer().getTile(++x, ++y);
        //
//        if (isPressed) {
//            int left = getCurrentLayer().getTile(x - 1, y);
//            int right = getCurrentLayer().getTile(x + 1, y);
//            int up = getCurrentLayer().getTile(x, y - 1);
//            int down = getCurrentLayer().getTile(x, y + 1);
//
//            System.out.println(left);
//            System.out.println(right);
//            System.out.println(up);
//            System.out.println(down);
//
//
//           if (right == 6) {
//                tile = 5;
//            } else if (left == 6) {
//                tile = 7;
//            } else {
//                tile = 6;
//            }
//
//        }
        //eight neighbour tiles (5 - main tile under the cursor)
        //123
        //456
        //789
//        int b1 = getCurrentLayer().getTile(--x, --y);
//        int b2 = getCurrentLayer().getTile(x, --y);
//        int b3 = getCurrentLayer().getTile(++x, ++y);
//        int b4 = getCurrentLayer().getTile(--x, y);
//        int b5 = getCurrentLayer().getTile(x, y);
//        int b6 = getCurrentLayer().getTile(++x, y);
//        int b7 = getCurrentLayer().getTile(--x, ++y);
//        int b8 = getCurrentLayer().getTile(++x, ++y);
        //around tiles
//        if (isPressed && (state == STATE_PAINT)) {
//            getCurrentLayer().setTile(x, y, tile);
//
//        }
        //ставим тайл 2
        if (isPressed && (state == STATE_PAINT)) {
            int size = mainCursor.getSize();
            for (int i = x; i < x + size; i++) {
                for (int j = y; j < y + size; j++) {
                    getCurrentLayer().setTile(i, j, tile);
                }
            }
        }
        //заливка
        if (isPressed && (state == STATE_FILL)) {
            int size = mainCursor.getSize();
            int src = getCurrentLayer().getTile(core.getMainCursor().getAbsoluteX(), core.getMainCursor().getAbsoluteY());
            int des = mainCursor.getTileId();

            System.out.println(src + ":" + des);
            System.out.println(x + ":" + y);

            int w = Global.horizontalTiles;
            int h = Global.verticalTiles;

            FloodFill.fill(x, y, src, des, getCurrentLayer(), new boolean[w][h]);

        }

        if (isPressed && (state == MODE_TELEPORT) && (e.getButton() == MouseEvent.BUTTON1)) {
            isPressed = false;
            if (teleportFixed) {
                teleportFixed = !teleportFixed;
                teleportSource = new Point(x, y);
            } else {
                teleportFixed = !teleportFixed;
                Core.getTeleports().add(new Teleport(new Rectangle(teleportSource), new Point(x, y)));
                MapEditor.initTeleportsList(LayerLoader.getTeleportsArray());
            }
        }

        if (isPressed && (state == STATE_MESSAGE) && (e.getButton() == MouseEvent.BUTTON1)) {
            //place a message
            isPressed = false;

            String msg = (String) JOptionPane.showInputDialog(
                    MapEditor.getFrames()[0],
                    "Text for this message block:\n",
                    "Respond please",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null, "");

            Core.getMessages().add(new Message(new Rectangle(new Point(x, y)), msg));
            MapEditor.initMessagesList(LayerLoader.getMessagesArray());
        }

        if (isPressed && (state == MODE_SELECTION) && (e.getButton() == MouseEvent.BUTTON1)) {
            isPressed = false;
            // move and copy regions
            if ((MyMouseController.getState() == MyMouseController.MODE_SELECTION)) {
                if (!MyMouseController.isStartSet() && !MyMouseController.isEndSet()) {
                    MyMouseController.setWidth(0);
                    MyMouseController.setHeight(0);

                    MyMouseController.setX1(Core.getInstance().getMainCursor().getAbsoluteX());
                    MyMouseController.setY1(Core.getInstance().getMainCursor().getAbsoluteY());
                    MyMouseController.setStartSet(true);
                    Core.getInstance().setHint("Set the bottom-right corner of the region.");
                    return;
                }

                if (MyMouseController.isStartSet() && !MyMouseController.isEndSet()) {
                    //int width0 = 0, height0 = 0;
                    int x2 = Core.getInstance().getMainCursor().getAbsoluteX();
                    int y2 = Core.getInstance().getMainCursor().getAbsoluteY();
                    if ((x2 >= x1) && (y2 >= y1)) {
                        width = x2 - MyMouseController.getX1() + 1;
                        height = y2 - MyMouseController.getY1() + 1;
                    }/* else if ((x2 < x1) && (y2 < y1)) {
                     width = MyMouseController.getX1() - x2 + 1;
                     height = MyMouseController.getY1() - y2 + 1;
                     }*/

                    MyMouseController.setWidth(width);
                    MyMouseController.setHeight(height);

                    Core.getInstance().setHint("Ctrl-C or Ctrl-X - copy and paste, re-select top-left corner if you want.");

                    MyMouseController.setEndSet(true);
                    return;
                }

                if (MyMouseController.isStartSet() && MyMouseController.isEndSet()) {
                    //MyMouseController.setX2(Core.getInstance().getMainCursor().getAbsoluteX());
                    //MyMouseController.setY2(Core.getInstance().getMainCursor().getAbsoluteY());
                    Core.getInstance().setHint("Set the top-left corner of region.");

                    MyMouseController.setWidth(0);
                    MyMouseController.setHeight(0);

                    MyMouseController.setStartSet(false);
                    MyMouseController.setEndSet(false);
                }
            }
        }

        //если курсор не на сайдбаре, рисуем его, если на сайдбаре - обновляем сайдбар
        //TODO сделать покрасивее, а то ппц какой-то с этой отрисовкой сайдбара. суть в том, что его нельзя перерисовывать постоянно - лагает
        if ((e.getX() < Global.windowWidth - MapEditor.getSidebar().getWidth()) && isMain) {
            mainCursor.setCoords(curX, curY);
            mainCursor.setPaint(true);
            if (sideCursor.isPaint()) {
                sideCursor.setPaint(false);
                MapEditor.getSidebar().repaint();
            }
        } else if (!isMain) { //на сайдбаре
            sideCursor.setCoords(curX, curY);
            sideCursor.setPaint(true);
            mainCursor.setPaint(false);
            MapEditor.getSidebar().repaint();
        }
        core.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX() / Global.tileWidth;
        int y = e.getY() / Global.tileHeight - MapEditor.getSidebar().getTileList().getBlocksY(); //сдвигаем курсор, если палитра проскроллена

        //если не на сайдбаре
        if (isMain && (e.getButton() != MouseEvent.BUTTON2)) {
            isPressed = true;
        } else if (e.getButton() == MouseEvent.BUTTON1) { //если на сайдбаре
            //TODO выпилить куда-то эти транслейты, заменить на методы translateX() и translateY()

            //установка тайла для кисти
            core.getMainCursor().setTileId(MapEditor.getSidebar().getTileId(x, y));
            //установка координат неподвижного курсора на сайдбаре
            MapEditor.getSidebar().setCursorCoords(x, -y);
            MapEditor.getSidebar().repaint();
        }

        //берём тайл прямо с карты кликом колесика
        if (e.getButton() == MouseEvent.BUTTON2) {
            core.getMainCursor().setTileId(getCurrentLayer().getTile(core.getMainCursor().getAbsoluteX(), core.getMainCursor().getAbsoluteY()));
        }

        // продолжаем обработку, чтобы при одиночном клике тайл тоже ставился
        mouseMoved(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isPressed = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public static String getModeInText(int mode) {
        switch (mode) {
            case STATE_PAINT:
                return "Paint mode";
            case MODE_TELEPORT:
                return "Teleport mode";
            case STATE_MESSAGE:
                return "Message mode";
            case MODE_SELECTION:
                return "Selection mode";
            default:
                return "default";
        }
    }

    public static void resetSelection() {
        setWidth(0);
        setHeight(0);
        setStartSet(false);
        setEndSet(false);
    }
}
