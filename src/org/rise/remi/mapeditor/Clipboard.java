/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rise.remi.mapeditor;

import java.util.ArrayList;
import org.rise.remi.blocks.Teleport;

/**
 *
 * @author Riseremi
 */
public final class Clipboard {

    private static Clipboard instance;
    //private int[][] buffer;
    private ArrayList<int[][]> buffers;
    private ArrayList<Teleport> teleportPoints;
    public static final int MODE_CUT = 1, MODE_COPY = 2;

    private Clipboard() {
        buffers = new ArrayList<>();
        teleportPoints = new ArrayList<>();
    }

    public static Clipboard getInstance() {
        if (instance == null) {
            instance = new Clipboard();
            return instance;
        } else {
            return instance;
        }
    }

    public void add(int[][] region) {
//        buffer = new int[region[0].length][region.length];
//
//        for (int i = 0; i < region[0].length; i++) {
//            for (int j = 0; j < region.length; j++) {
//                buffer[i][j] = region[i][j];
//            }
//        }
        buffers.add(region);
    }

    public void clear() {
        buffers.clear();
    }

    public int[][] get(int layerNum) {
        return buffers.get(layerNum);
    }
}
