package org.rise.remi.layer;

import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.rise.remi.blocks.Message;
import org.rise.remi.blocks.Teleport;
import org.rise.remi.mapeditor.Core;
import org.rise.remi.mapeditor.Global;
import org.rise.remi.mapeditor.MapEditor;
import org.rise.remi.mapeditor.World;
import org.rise.remi.utils.RLE;

/**
 * @author remi
 */
public class LayerLoader {

    public static void newLoadFromFileToVersion2(String fileName, World world) throws IOException {
        File f = new File(fileName);
        f.createNewFile();

        BufferedReader br = new BufferedReader(new FileReader(fileName));

        System.out.println("Loading map...");

        long start = System.currentTimeMillis();

        //String fileContent = MapHash.getFileData(fileName);
        //fileContent = MapHash.decompress(fileContent);
        //System.out.println("NAME: " + fileName);
        String fileContent = br.readLine();
        //fileContent = MapHash.decompress(fileContent);

        int width = Integer.valueOf(getProperty("width", fileContent));
        int height = Integer.valueOf(getProperty("height", fileContent));

        Core.getTeleports().clear();
        for (int i = 0; i < 50; i++) {
            try {
                String tp = getProperty("teleport-" + i, fileContent);
                int x = Integer.parseInt(getProperty("source-x", tp));
                int y = Integer.parseInt(getProperty("source-y", tp));
                int destX = Integer.parseInt(getProperty("destination-x", tp));
                int destY = Integer.parseInt(getProperty("destination-y", tp));

                Point destination = new Point(destX, destY);
                Rectangle teleport = new Rectangle(x, y, (int) (Global.tileWidth), (int) (Global.tileHeight));

                Core.getTeleports().add(new Teleport(teleport, destination));
                System.out.println("" + x + ":" + y);
                System.out.println("" + destX + ":" + destY);

            } catch (Exception ex) {
            }
        }
        System.out.println("Loaded " + Core.getTeleports().size() + " teleports.");

        //loading messages
        Core.getMessages().clear();
        for (int i = 0; i < 50; i++) {
            try {
                String msg = getProperty("message-" + i, fileContent);
                int x = Integer.parseInt(getProperty("source-x", msg));
                int y = Integer.parseInt(getProperty("source-y", msg));
                String text = getProperty("text", msg);

                Rectangle position = new Rectangle(new Point(x, y));

                Core.getMessages().add(new Message(position, text));
                System.out.println("Msg: " + x + ":" + y);
            } catch (Exception ex) {
            }
        }
        System.out.println("Loaded " + Core.getMessages().size() + " messages.");

        String map = getProperty("map", fileContent);

        String author = getProperty("author", fileContent);
        String name = getProperty("name", fileContent);

        String widthS = getProperty("width", fileContent);
        String heightS = getProperty("height", fileContent);

        String layer0 = getProperty("data-0", fileContent);
        String layer1 = getProperty("data-1", fileContent);
        String layer2 = getProperty("data-2", fileContent);

        //String teleports = getProperty("teleports", fileContent);
        //String animation = getProperty("animation", fileContent);
        //layer0 = MapHash.decode(layer0);
        //layer1 = MapHash.decode(layer1);
        //layer2 = MapHash.decode(layer2);
        layer0 = RLE.decompress(layer0);
        layer1 = RLE.decompress(layer1);
        layer2 = RLE.decompress(layer2);

        ArrayList<ArrayList<Integer>> tempLayout0 = new ArrayList<>();
        ArrayList<ArrayList<Integer>> tempLayout1 = new ArrayList<>();
        ArrayList<ArrayList<Integer>> tempLayout2 = new ArrayList<>();

        String[] values0 = layer0.trim().split(" ");
        String[] values1 = layer1.trim().split(" ");
        String[] values2 = layer2.trim().split(" ");

        //новый цикл чтения карты из строки в двумерный массив
        //слой с препятствиями
        int obstacles = 0;
        for (int i = 0; i < height; i++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                int tile = Integer.parseInt(values0[width * i + j]);
                if (tile != -1) {
                    obstacles++;
                }
                row.add(tile);
            }
            tempLayout0.add(row);
        }

        System.out.println("Loaded " + obstacles + " obstacles.");

        //второй слой с тайлами
        for (int i = 0; i < height; i++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                row.add(Integer.parseInt(values1[width * i + j]));
            }
            tempLayout1.add(row);
        }

        //третий слой с объектами
        for (int i = 0; i < height; i++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                row.add(Integer.parseInt(values2[width * i + j]));
            }
            tempLayout2.add(row);
        }

        System.out.println("Loaded 2 layers.");

        //первый слой - препятствия
        //не трогать, это работает и с XML-версией
        //в tempLayout лежит полная копия слоя, теперь мы переносим её на сам слой
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                getLayer(0).setTile(x, y, tempLayout0.get(y).get(x));
            }
        }

        //второй слой - сплошные тайлы
        //не трогать, это работает и с XML-версией
        //в tempLayout лежит полная копия слоя, теперь мы переносим её на сам слой
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                getLayer(1).setTile(x, y, tempLayout1.get(y).get(x));
            }
        }

        //третий слой - объекты
        //не трогать, это работает и с XML-версией
        //в tempLayout лежит полная копия слоя, теперь мы переносим её на сам слой
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                getLayer(2).setTile(x, y, tempLayout2.get(y).get(x));
            }
        }
        System.out.println("Done!");

        long end = System.currentTimeMillis();
        System.out.println("Elapsed: " + String.valueOf(end - start) + " millis\r\n");

        MapEditor.initTeleportsList(getTeleportsArray());
        MapEditor.initMessagesList(getMessagesArray());
        System.out.println(getTeleportsArray().length);
    }

    public static Teleport[] getTeleportsArray() {
        Teleport telep[] = new Teleport[Core.getTeleports().size()];
        for (Teleport t : Core.getTeleports()) {
            telep[Core.getTeleports().indexOf(t)] = t;
        }
        return telep;
    }

    public static Message[] getMessagesArray() {
        Message msgs[] = new Message[Core.getMessages().size()];
        for (Message msg : Core.getMessages()) {
            msgs[Core.getMessages().indexOf(msg)] = msg;
        }
        return msgs;
    }

    public static void loadFromFileVersion1(String fileName, World world) throws IOException {
        File f = new File(fileName);
        f.createNewFile();

        BufferedReader br = new BufferedReader(new FileReader(fileName));

        int[][] mapTemp = new int[Global.horizontalTiles][Global.verticalTiles];

        //layer 0
        for (int i = 0; i < Global.verticalTiles; i++) {
            String line = br.readLine();
            if (line.equals(".")) {
                break;
            }
            String v[] = line.split(" ");
            for (int j = 0; j < Global.horizontalTiles; j++) {
                mapTemp[j][i] = Integer.parseInt(v[j]);
            }

            for (int x = 0; x < Global.horizontalTiles; x++) {
                for (int y = 0; y < Global.verticalTiles; y++) {
                    getLayer(0).setTile(x, y, mapTemp[x][y]);
                }
            }
        }
        br.readLine();

        //layer 1
        for (int i = 0; i < Global.verticalTiles; i++) {
            String line = br.readLine();
//            if (line.equals(".")) {
//                continue;
//            }
            String v[] = line.split(" ");
            for (int j = 0; j < Global.horizontalTiles; j++) {
                mapTemp[j][i] = Integer.parseInt(v[j]);
            }

            for (int x = 0; x < Global.horizontalTiles; x++) {
                for (int y = 0; y < Global.verticalTiles; y++) {
                    getLayer(1).setTile(x, y, mapTemp[x][y]);
                }
            }
        }

    }

    public static void saveToFileVersion1(String fileName) throws IOException {
        File f = new File(fileName);
        if (!f.exists()) {
            f.createNewFile();
        }

        StringBuilder builder = new StringBuilder();

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"))) {
            System.out.println("Saving " + getLayer(1).getW() / Global.tileWidth + "x" + getLayer(0).getH() / Global.tileHeight);

            //layer 0
            for (int i = 0; i < getLayer(0).getH() / Global.tileHeight; i++) {
                for (int j = 0; j < getLayer(0).getW() / Global.tileWidth; j++) {
                    builder.append(getLayer(0).getTile(j, i)).append(" ");
                }
                builder.append("\r\n");
            }
            builder.append(".\r\n");

            //layer 1
            for (int i = 0; i < getLayer(1).getH() / Global.tileHeight; i++) {
                for (int j = 0; j < getLayer(1).getW() / Global.tileWidth; j++) {
                    builder.append(getLayer(1).getTile(j, i)).append(" ");
                }
                builder.append("\r\n");
            }

            bw.write(builder.toString());
            bw.flush();
            bw.close();
        }

    }

    public static void saveToFileVersion2(String fileName) throws IOException {
        File f = new File(fileName);
        if (!f.exists()) {
            f.createNewFile();
        }

        StringBuilder builder = new StringBuilder();

        //todo test
        long start = System.currentTimeMillis();
        System.out.println("Saving map...");

        builder.append(openTag("map", ""));
        builder.append(putProperty("author", "Remi :3"));
        builder.append(putProperty("name", "Test Map"));
        builder.append(putProperty("width", "" + Global.horizontalTiles));
        builder.append(putProperty("height", "" + Global.verticalTiles));

        try {
            ArrayList<Integer> map = new ArrayList<>();
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"))) {
                for (int i = 0; i < 3; i++) {
                    builder.append(openTag("layer", "" + i)).append(openTag("data", "" + i));
                    for (int y = 0; y < Global.verticalTiles; y++) {
                        for (int x = 0; x < Global.horizontalTiles; x++) {
                            //builder.append(MapHash.encode(String.valueOf(getLayer(i).getTile(x, y)) + " "));
                            map.add(getLayer(i).getTile(x, y));
                        }
                    }

                    Object[] obs = map.toArray();
                    int[] obs2 = new int[map.size()];
                    map.clear();

                    for (int k = 0; k < obs.length; k++) {
                        obs2[k] = (int) obs[k];
                    }

                    builder.append(RLE.compress(obs2));

                    builder.append(closeTag("data", "" + i)).append(closeTag("layer", "" + i));
                }
                System.out.println("Saved 2 layers.");

                //writing teleports to the file
                ArrayList<Teleport> teleports = Core.getTeleports();
                for (Teleport t : teleports) {
                    String sourceX = String.valueOf(t.getSourceX());
                    String sourceY = String.valueOf(t.getSourceY());
                    String destinationX = String.valueOf(t.getDestinationX());
                    String destinationY = String.valueOf(t.getDestinationY());

                    builder.append(openTag("teleport", String.valueOf(teleports.indexOf(t))));
                    builder.append(putProperty("source-x", sourceX));
                    builder.append(putProperty("source-y", sourceY));
                    builder.append(putProperty("destination-x", destinationX));
                    builder.append(putProperty("destination-y", destinationY));
                    builder.append(closeTag("teleport", String.valueOf(teleports.indexOf(t))));
                }
                System.out.println("Saved " + teleports.size() + " teleports.");

                //writing messages to file
                ArrayList<Message> messages = Core.getMessages();
                for (Message msg : messages) {
                    String sourceX = String.valueOf(msg.getSourceX());
                    String sourceY = String.valueOf(msg.getSourceY());
                    String text = msg.getText();

                    builder.append(openTag("message", String.valueOf(messages.indexOf(msg))));
                    builder.append(putProperty("source-x", sourceX));
                    builder.append(putProperty("source-y", sourceY));
                    builder.append(putProperty("text", text));
                    builder.append(closeTag("message", String.valueOf(messages.indexOf(msg))));
                }
                System.out.println("Saved " + messages.size() + " messages.");

                //closing map
                builder.append("</map>");

                //compressing a whole string
                //String source = "<my little test map>";
                //String source = builder.toString();
                //String compressed = MapHash.compress(source);
                //String compressed = MapHash.compress(builder.toString());
                //write final string to the file
                bw.write(builder.toString());
                //bw.write(compressed);                
                bw.flush();
                bw.close();

                //String compressed = MapHash.compress(MapHash.getFileData(fileName));
                //PrintWriter writer = new PrintWriter(f);
                //writer.print("");
                //writer.print(compressed);
                //writer.close();
                //bw.write(compressed);
                System.out.println("Done!");
                long end = System.currentTimeMillis();
                System.out.println("Elapsed: " + String.valueOf(end - start) + " millis\r\n");
                JOptionPane.showMessageDialog(MapEditor.getFrames()[0], "Map succesfully saved!");
            }
        } catch (IOException | HeadlessException ex) {
            System.out.println("Failed to save map: " + ex.toString());
        }
    }

    public static String putProperty(String tag, String content) {
        return openTag(tag, "") + content + closeTag(tag, "");
    }

    public static String openTag(String tag, String number) {
        String prefix = "";
        if (!number.isEmpty()) {
            prefix = "-";
        }
        return "<" + tag + prefix + number + ">";
    }

    public static String closeTag(String tag, String number) {
        String prefix = "";
        if (!number.isEmpty()) {
            prefix = "-";
        }
        return "</" + tag + prefix + number + ">";
    }

    public static String getProperty(String tag, String text) {
        String start = "<" + tag + ">";
        String end = "</" + tag + ">";

        int startIndex = text.indexOf(start);

        text = text.substring(start.length(), text.length());
        text = text.substring(startIndex);

        int endIndex = text.indexOf(end);
        String result = text.substring(0, endIndex);

        return result;
    }

    //возвращает слой с номером
    public static TiledLayer getLayer(int i) {
        switch (i) {
            case 0:
                return Core.getInstance().getWorld().getNullLayer();
            case 1:
                return Core.getInstance().getWorld().getWorldLayer();
            case 2:
                return Core.getInstance().getWorld().getObjectsLayer();
            default:
                return null;
        }
    }
}
