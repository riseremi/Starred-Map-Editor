package org.rise.remi.mapeditor;

import org.rise.remi.layer.TiledLayer;

/**
 *
 * @author Riseremi
 */
public class FloodFill {
    public static void fill(int x, int y, int src, int des, TiledLayer layer, boolean[][] mark) {
        if ((x < 0) || (x >= Global.horizontalTiles)) {
            return;
        }
        
        if ((y < 0) || (y >= Global.verticalTiles)) {
            return;
        }

        if (mark[x][y]) {
            return;
        }

        if (layer.getTile(x, y) != src) {
            return;
        }

        layer.setTile(x, y, des);
        mark[x][y] = true;

        fill(x, y - 1, src, des, layer, mark);
        fill(x, y + 1, src, des, layer, mark);
        fill(x - 1, y, src, des, layer, mark);
        fill(x + 1, y, src, des, layer, mark);
    }
}
