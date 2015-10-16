package org.rise.remi.mapeditor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.Setter;
import org.rise.remi.layer.TiledLayer;

/**
 * @author remi
 */
public class World {

    private ArrayList<TiledLayer> layers = new ArrayList<>();
    private @Getter TiledLayer worldLayer, objectsLayer, nullLayer;
    private @Getter @Setter int layer = 1;

    public World(int tileWidth, int tileHeight, int width, int height) {
        try {
            worldLayer = new TiledLayer(ImageIO.read(getClass().getResourceAsStream(Global.PATH_TO_TILESET)), tileWidth, tileHeight, width, height, true);
            objectsLayer = new TiledLayer(ImageIO.read(getClass().getResourceAsStream(Global.PATH_TO_TILESET)), tileWidth, tileHeight, width, height, true);
            nullLayer = new TiledLayer(ImageIO.read(getClass().getResourceAsStream(Global.PATH_TO_TILESET)), tileWidth, tileHeight, width, height, true);
        } catch (IOException ex) {
        }
    }

    public void recreate() {
        for (int i = 0; i < Global.horizontalTiles; i++) {
            for (int j = 0; j < Global.verticalTiles; j++) {
                worldLayer.setTile(i, j, -1);
                objectsLayer.setTile(i, j, -1);
                nullLayer.setTile(i, j, -1);
            }
        }
    }

    public void paint(Graphics2D g) {
        //сброс прозрачности
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        nullLayer.setBlocksX(worldLayer.getBlocksX());
        nullLayer.setBlocksY(worldLayer.getBlocksY());
        objectsLayer.setBlocksX(worldLayer.getBlocksX());
        objectsLayer.setBlocksY(worldLayer.getBlocksY());

        nullLayer.paintComponent(g);
        //если нуль леер не рисуется, то не ставим полупрозрачность
        if (nullLayer.getVisible()) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
        worldLayer.paintComponent(g);
        objectsLayer.paintComponent(g);

        //тестовая отрисовка сетки ТОЛЬКО поверх слоя
        //в любом случае включаем полупрозрачность, ибо сетка не должна мешать
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        for (int i = 0; i < Global.verticalTiles + 1; i++) {
            g.setColor(Color.GRAY);
            //горизонтальные
            g.drawLine(worldLayer.getBlocksX() * Global.tileWidth,
                    worldLayer.getBlocksY() * Global.tileWidth + i * Global.tileWidth,
                    (worldLayer.getBlocksX() + Global.horizontalTiles) * Global.tileWidth,
                    worldLayer.getBlocksY() * Global.tileWidth + i * Global.tileWidth);
        }
        
          for (int i = 0; i < Global.horizontalTiles + 1; i++) {
            g.setColor(Color.GRAY);
            //вертикальные
            g.drawLine(worldLayer.getBlocksX() * Global.tileWidth + i * Global.tileWidth,
                    worldLayer.getBlocksY() * Global.tileWidth,
                    worldLayer.getBlocksX() * Global.tileWidth + i * Global.tileWidth,
                    (worldLayer.getBlocksY() + Global.verticalTiles) * Global.tileWidth);
        }
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

    }
}
