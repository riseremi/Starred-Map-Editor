package org.rise.remi.mapeditor;

/**
 * @author remi
 */
public class Global {

    //TODO add выделение произвольной области и сохранение её в отдельный файл
    //TODO add заливка областей
    //TODO add выбор сразу нескольких тайлов из палитры в качестве кисти
    public final static int tileWidth = 32, tileHeight = 32;
//    public final static int tileWidth = 32, tileHeight = 32;
    //for 32x32
    public final static int windowWidth = 70 * tileWidth, windowHeight = 40 * tileHeight;
    //for 16x16
    //public final static int windowWidth = 38 * 2 * tileWidth, windowHeight = 22 * 2 * tileHeight;
    public final static int verticalTiles = 30 * 3, horizontalTiles = 40 * 3;
    public final static int mapWidth = tileWidth * horizontalTiles, mapHeight = tileHeight * verticalTiles;
    public static final int sidebarWidth = 11;
    public static int paintWidth = windowWidth / tileWidth - sidebarWidth, paintHeight = windowHeight / tileHeight - 1;
    //public final static String PATH_TO_TILESET = "/res/autoTiles1.png";
    //public final static String PATH_TO_TILESET = "/res/tilesOneScreen2.png";
    //public final static String PATH_TO_TILESET = "/res/testTiles.png";
//    public final static String PATH_TO_TILESET = "/res/dungeonTiles.png";
    
    //16x16
    //public final static String PATH_TO_TILESET = "/res/new_tiles_small.png";
    
    //standard 32x32
    public final static String PATH_TO_TILESET = "/res/new_tiles.png";
//    public final static String PATH_TO_TILESET = "/res/pokemon_tileset_guide.png";
}
