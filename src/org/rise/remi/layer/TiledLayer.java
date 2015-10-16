package org.rise.remi.layer;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.Vector;
import org.rise.remi.blocks.Teleport;
import org.rise.remi.mapeditor.Clipboard;
import org.rise.remi.mapeditor.Core;
import org.rise.remi.mapeditor.Global;

/**
 * @author LPzhelud use of this class approved by the author 09.11.2012 - 9:15
 * AM
 */
public class TiledLayer extends Layer {

    private final int[][] visiblity;//[x][y]
    private final BufferedImage[] tiles;
    private final int tileWidth, tileHeight;
    private final int horizontalTilesNumber, verticalTilesNumber;
    private int[][] map;//[x][y]
    private int paintWidth, paintHeight;
    public static final int MOVE_NONE = 0, MOVE_UP = 1, MOVE_DOWN = -1, MOVE_LEFT = 1, MOVE_RIGHT = -1;

    public TiledLayer(BufferedImage image, int tileWidth, int tileHeight, int width, int height, boolean isSimpleLayer) {
        super(width * tileWidth, height * tileHeight);
        image = toCompatibleImage(image);

        if (image.getWidth() / tileWidth * tileWidth != image.getWidth()
                || image.getHeight() / tileHeight * tileHeight != image.getHeight()) {
            throw new IllegalArgumentException();
        }
        this.tileHeight = tileHeight;
        this.tileWidth = tileWidth;
        this.horizontalTilesNumber = width;
        this.verticalTilesNumber = height;
        tiles = chopImage(image);
        map = new int[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                map[i][j] = -1;
            }
        }

        visiblity = new int[width][height];

        if (isSimpleLayer) {
            paintWidth = Global.paintWidth;
            paintHeight = Global.paintHeight;
        } else { //if sidebar
            paintWidth = 11;
            paintHeight = image.getHeight() / Global.tileWidth;
        }
    }

    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }

    private BufferedImage[] chopImage(BufferedImage image) {
        int x = 0, y = 0;
        Vector list = new Vector();
        try {
            while (true) {
                while (true) {
                    BufferedImage subImage = image.getSubimage(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
                    list.add(subImage);
                    x++;
                    if ((x + 1) * tileWidth > image.getWidth()) {
                        x = 0;
                        break;
                    }
                }
                y++;
                if ((y + 1) * tileHeight > image.getHeight()) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(x);
            System.out.println(y);
        }
        return (BufferedImage[]) list.toArray(new BufferedImage[list.size()]);
    }

    public void setTile(int x, int y, int tileId) {
        try {
            map[x][y] = tileId;
        } catch (Exception ex) {
        }
    }

    public int getTile(int x, int y) {
        int tileId = 0;
        try {
            tileId = map[x][y];
        } catch (Exception ex) {
        }
        return tileId;
    }

    public void fillRectTile(int x, int y, int w, int h, int tileId) {
        for (int i = y; i < y + h; i++) {
            for (int j = x; j < w + x; j++) {
                setTile(j, i, tileId);
            }
        }
    }

    //отрисовка слоя, при этом рисуются только помещающиеся на экран тайлы
    @Override
    public void paintLayer(Graphics2D g) {
        for (int i = 0; i < paintWidth; i++) {
            for (int j = 0; j < paintHeight; j++) {
                try {
                    int x = i - super.getBlocksX(), y = j - super.getBlocksY();
                    if ((x >= 0) && (y >= 0) && (x < Global.horizontalTiles) && (y < Global.verticalTiles)) {
                        paintTile(g, i * tileWidth, j * tileHeight, map[x][y]);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
            }
        }
    }

    public void paintTile(Graphics2D g, int x, int y, int id) {
        if (id != -1) {
            g.drawImage(tiles[id], x, y, null);
        }
    }

    private BufferedImage toCompatibleImage(BufferedImage image) {
        GraphicsConfiguration gfx_config = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();
        if (image.getColorModel().equals(gfx_config.getColorModel())) {
            return image;
        }
        BufferedImage new_image = gfx_config.createCompatibleImage(
                image.getWidth(), image.getHeight(), image.getTransparency());
        Graphics2D g2d = (Graphics2D) new_image.getGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return new_image;
    }

    /**
     * Copy all layers of selected region to the clipboard.
     *
     * @param width region width
     * @param height region height
     * @param x top-left corner of the region
     * @param y top-left corner of the region
     * @param mode copy or cut
     *
     * @see Clipboard#MODE_COPY
     * @see Clipboard#MODE_CUT
     */
    public void toClipboard(int width, int height, int x, int y, int mode) {
        int[][] tempLayer = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tempLayer[i][j] = map[x + i][y + j];

                //cutting teleports if they are in the selection
                for (Teleport t : Core.getTeleports()) {
                    final int startX = t.getSourceX();
                    final int startY = t.getSourceY();
                    final int endX = t.getDestinationX();
                    final int endY = t.getDestinationY();

                    if (startX == i && startY == j) {
                        //start point in the selection
                        
                    }

                    if (endX == i && endY == j) {
                        //end point in the selection
                        
                    }

                }
                if (mode == Clipboard.MODE_CUT) {
                    map[x + i][y + j] = -1; //delete old region
                }
            }
        }
        Clipboard.getInstance().add(tempLayer);
    }

    /**
     * Paste previously copied region in the specified coords.
     *
     * @param x2 x position of the top-left corner
     * @param y2 y position of the top-left corner
     * @param num number of layer to paint (0 - null, 1 - overworld, 2 -
     * objects)
     * @param alpha if true, empty tiles (id = -1) will be alpha, i.e. they will
     * not be pasted, in their place will be old tiles
     */
    public void pasteFromClipboard(int x2, int y2, int num, boolean alpha) {
        int[][] tempLayer = Clipboard.getInstance().get(num);
        for (int x = 0; x < tempLayer.length; x++) {
            for (int y = 0; y < tempLayer[0].length; y++) {
                if (tempLayer[x][y] != -1) {
                    map[x + x2][y + y2] = tempLayer[x][y];
                }
            }
        }
    }

    public void move(int xOffset, int yOffset) {
        super.setBlocksX(super.getBlocksX() + xOffset);
        super.setBlocksY(super.getBlocksY() + yOffset);
    }
}
