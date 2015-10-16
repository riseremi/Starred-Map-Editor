package org.rise.remi.mapeditor;

import org.rise.remi.controllers.MyMouseController;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import org.rise.remi.blocks.Message;
import org.rise.remi.blocks.Teleport;
import org.rise.remi.components.MouseCursor;

/**
 * @author remi
 */
public class Core extends JPanel {

    private static Core instance;
    private @Getter World world;
    private @Getter MouseCursor mainCursor, sidebarCursor;
    //если файл изменяли с последнего сохранения, то true. в заголовок окна выводится звёздочка
    private @Getter @Setter boolean notSaved = true;
    private static @Getter ArrayList<Teleport> teleports = new ArrayList<>();
    private static @Getter @Setter boolean teleportsVisible = true;
    private static @Getter ArrayList<Message> messages = new ArrayList<>();
    private @Setter String hint = "";
    public static @Getter boolean screenshotMode = false;

    private Core() {
        world = new World(Global.tileWidth, Global.tileHeight, Global.horizontalTiles, Global.verticalTiles);
        mainCursor = new MouseCursor(Color.RED);
        sidebarCursor = new MouseCursor(Color.BLUE);
    }

    public static Core getInstance() {
        if (instance == null) {
            instance = new Core();
            return instance;
        } else {
            return instance;
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, Global.windowWidth, Global.windowHeight);
        world.paint(g2d);

        //соединяем телепорты линиями
        if (teleportsVisible) {
            for (Teleport t : teleports) {
                g2d.setColor(Color.LIGHT_GRAY);
                if (t == MapEditor.selectedTeleport) {
                    g2d.setColor(Color.RED);
                }
                g2d.drawLine(t.getSourceX() * 16 + world.getNullLayer().getBlocksX() * 16,
                        t.getSourceY() * 16 + world.getNullLayer().getBlocksY() * 16,
                        t.getDestinationX() * 16 + world.getNullLayer().getBlocksX() * 16,
                        t.getDestinationY() * 16 + world.getNullLayer().getBlocksY() * 16);
            }
        }

        //drawing a message blocks
        for (Message msg : messages) {
            g2d.setColor(Color.LIGHT_GRAY);
            if (msg == MapEditor.selectedMessage) {
                g2d.setColor(Color.RED);
            }
            g2d.drawRect(msg.getSourceX() * 16 + world.getNullLayer().getBlocksX() * 16,
                    msg.getSourceY() * 16 + world.getNullLayer().getBlocksY() * 16,
                    16, 16);

            g2d.drawRect(msg.getSourceX() * 16 + world.getNullLayer().getBlocksX() * 16 + 1,
                    msg.getSourceY() * 16 + world.getNullLayer().getBlocksY() * 16 + 1,
                    14, 14);
        }

        //if not in screenshot mode, paint infobar and selection
        if (!screenshotMode) {
            paintCutCopyRect(g2d);

            mainCursor.paint(g2d);

            g2d.setColor(Color.WHITE);
            g2d.drawString(hint, 4, 16);

            g2d.setColor(new Color(85, 85, 85));
            g2d.fillRect(0, Global.windowHeight - 70, Global.windowWidth, 100);
            g2d.setColor(Color.WHITE);
            g2d.drawString("x: " + mainCursor.getAbsoluteX()
                    + ", y: " + mainCursor.getAbsoluteY() + ", size: "
                    + mainCursor.getSize() + ", layer: " + world.getLayer() + ", "
                    + MyMouseController.getModeInText(MyMouseController.getState()),
                    5, Global.windowHeight - 60);
        }
    }

    private void paintCutCopyRect(Graphics2D g) {
        int x = MyMouseController.getX1() + Core.getInstance().getWorld().getWorldLayer().getBlocksX();
        int y = MyMouseController.getY1() + Core.getInstance().getWorld().getWorldLayer().getBlocksY();
        int w = MyMouseController.getWidth();
        int h = MyMouseController.getHeight();

        g.setColor(Color.MAGENTA);
        if ((w != 0) && (h != 0)) {
            g.drawRect(x * 16, y * 16, w * 16, h * 16);
        }
    }

    public void recreate() {
        getWorld().recreate();
        MapEditor.getFrames()[0].setTitle("Map Editor \"" + new File("new_map.m").getName() + "\"");
    }

    public void takeScreenshot() {
        BufferedImage screenshot;
        screenshotMode = true;

        screenshot = new BufferedImage(Global.horizontalTiles * Global.tileWidth, Global.verticalTiles * Global.tileWidth, BufferedImage.TYPE_INT_RGB);
        Graphics g = screenshot.getGraphics();
        Graphics2D g2d = (Graphics2D) g;

        world.paint(g2d);

        try {
            System.out.println("Trying to save to file...");
            ImageIO.write(screenshot, "png", new File("image.png"));
            JOptionPane.showMessageDialog(MapEditor.getFrames()[0], "Screenshot succesfully saved.");
            screenshotMode = false;
        } catch (IOException | HeadlessException ex) {
            screenshotMode = false;
            JOptionPane.showMessageDialog(MapEditor.getFrames()[0], "Cannot save screenshot.");
        }

    }
}
