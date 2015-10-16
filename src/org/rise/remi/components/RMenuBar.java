package org.rise.remi.components;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.rise.remi.controllers.MyMouseController;
import org.rise.remi.layer.LayerLoader;
import org.rise.remi.mapeditor.Core;
import org.rise.remi.mapeditor.MapEditor;

/**
 * Created with IntelliJ IDEA. User: remi Date/time: 31.05.13, 17:18
 */
public class RMenuBar extends JMenuBar implements ActionListener {

    private static JMenu file = new JMenu("File"), edit = new JMenu("Edit"), about = new JMenu("About");
    private static JMenuItem create = new JMenuItem("New"), save = new JMenuItem("Save"), saveAs = new JMenuItem("Save as..."), open = new JMenuItem("Open"), exit = new JMenuItem("Exit");

    private static JMenuItem saveOld = new JMenuItem("Save (old)"), openOld = new JMenuItem("Open (old)");

    private static JMenuItem help = new JMenuItem("Help");
    private static JMenuItem undo = new JMenuItem("Undo (coming soon)");
    private static JMenuItem moveArea = new JMenuItem("Move area");
    //modes
    private static JMenuItem mode_paint = new JMenuItem("Paint mode");
    private static JMenuItem mode_teleports = new JMenuItem("Teleport mode");
    private static JMenuItem mode_message = new JMenuItem("Message mode");
    private static JMenuItem mode_selection = new JMenuItem("Selection mode");
    //
    private static JCheckBoxMenuItem nullLayerVisible = new JCheckBoxMenuItem("Null layer (obstacles)", true);
    private static JCheckBoxMenuItem teleportsVisible = new JCheckBoxMenuItem("Teleports", true);
    private final JFileChooser fc = new JFileChooser();
    private File fileToOpen = new File("");
    private boolean nullLayerChecked = true;
    private boolean teleportsChecked = true;

    public RMenuBar() {
        file.add(create);
        file.add(open);
        file.add(saveAs);

        file.add(saveOld);
        file.add(openOld);

        file.add(save);
        file.add(exit);

        //setting a hotkeys
        create.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK));
        open.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK));
        save.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));
        saveAs.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK | java.awt.Event.SHIFT_MASK));
        undo.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.Event.CTRL_MASK));

        //hotkeys for a modes (primary num keys under the F1-F12)
        mode_paint.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, 0));
        mode_teleports.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, 0));
        mode_message.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, 0));
        mode_selection.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, 0));

        edit.add(nullLayerVisible);
        edit.add(teleportsVisible);
        edit.addSeparator();

        edit.add(undo);
        edit.addSeparator();
        edit.add(moveArea);
        edit.addSeparator();

        edit.add(mode_paint);
        edit.add(mode_teleports);
        edit.add(mode_message);
        edit.add(mode_selection);

        about.add(help);

        this.add(file);
        this.add(edit);
        this.add(about);

        saveAs.addActionListener(this);
        exit.addActionListener(this);
        help.addActionListener(this);
        save.addActionListener(this);
        open.addActionListener(this);
        create.addActionListener(this);
        nullLayerVisible.addActionListener(this);
        teleportsVisible.addActionListener(this);
        undo.addActionListener(this);
        mode_paint.addActionListener(this);
        mode_teleports.addActionListener(this);
        mode_message.addActionListener(this);
        mode_selection.addActionListener(this);
        moveArea.addActionListener(this);

        saveOld.addActionListener(this);
        openOld.addActionListener(this);

        fc.setFileFilter(new ExtFileFilter("rsng", "*.m Map files"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exit) {
            System.exit(0);
        }

        if (e.getSource() == nullLayerVisible) {
            //TODO убрать сраный костыль, хз, почему не работает
            nullLayerChecked = !nullLayerChecked;
            nullLayerVisible.setState(nullLayerChecked);
            Core.getInstance().getWorld().getNullLayer().setVisible(nullLayerChecked);
        }

        if (e.getSource() == teleportsVisible) {
            teleportsChecked = !teleportsChecked;
            teleportsVisible.setState(teleportsChecked);
            Core.setTeleportsVisible(teleportsChecked);
        }

        if (e.getSource() == create) {
            Core.getInstance().recreate();
        }

        if (e.getSource() == open) {
            try {
                //ret - 0 если нажато "Открыть", 1 если "Отмена" и -1 если произошла ошибка
                int ret = fc.showOpenDialog(this);
                if (ret == 0) {
                    fileToOpen = fc.getSelectedFile();
                    LayerLoader.newLoadFromFileToVersion2(fileToOpen.getAbsolutePath(), Core.getInstance().getWorld());
                    MapEditor.getFrames()[0].setTitle("Map Editor \"" + fileToOpen.getName() + "\"");
                }
            } catch (HeadlessException | IOException ex) {
            }
        }
        if (e.getSource() == openOld) {
            try {
                //ret - 0 если нажато "Открыть", 1 если "Отмена" и -1 если произошла ошибка
                int ret = fc.showOpenDialog(this);
                if (ret == 0) {
                    fileToOpen = fc.getSelectedFile();
                    LayerLoader.loadFromFileVersion1(fileToOpen.getAbsolutePath(), Core.getInstance().getWorld());
                    MapEditor.getFrames()[0].setTitle("Map Editor \"" + fileToOpen.getName() + "\"");
                }
            } catch (HeadlessException | IOException ex) {
            }
        }

        if (e.getSource() == save) {
            try {
                if (fileToOpen.exists()) {
                    LayerLoader.saveToFileVersion2(fileToOpen.getAbsolutePath());
                    MapEditor.getFrames()[0].setTitle("Map Editor \"" + fileToOpen.getName() + "\"");
                } else {
                    e.setSource(saveAs);
                    actionPerformed(e);
                }
            } catch (IOException e1) {
            }
        }

        if (e.getSource() == saveAs) {
            try {
                //ret - 0 если нажато "Сохранить", 1 если "Отмена" и -1 если произошла ошибка
                fc.setSelectedFile(new File("new_map.m"));
                int ret = fc.showSaveDialog(this);
                if (ret == 0) {
                    fileToOpen = fc.getSelectedFile();
                    LayerLoader.saveToFileVersion2(fileToOpen.getAbsolutePath());
                    MapEditor.getFrames()[0].setTitle("Map Editor \"" + fileToOpen.getName() + "\"");
                }
                e.setSource(null);
            } catch (IOException e1) {
            }
        }
        if (e.getSource() == saveOld) {
            try {
                //ret - 0 если нажато "Сохранить", 1 если "Отмена" и -1 если произошла ошибка
                fc.setSelectedFile(new File("new_map.m"));
                int ret = fc.showSaveDialog(this);
                if (ret == 0) {
                    fileToOpen = fc.getSelectedFile();
                    LayerLoader.saveToFileVersion1(fileToOpen.getAbsolutePath());
                    MapEditor.getFrames()[0].setTitle("Map Editor \"" + fileToOpen.getName() + "\"");
                }
                e.setSource(null);
            } catch (IOException e1) {
            }
        }

        //mode changing
        if (e.getSource() == mode_paint) {
            MyMouseController.setState(MyMouseController.STATE_PAINT);
            Core.getInstance().setHint("Just draw the map.");
        }
        if (e.getSource() == mode_teleports) {
            MyMouseController.setState(MyMouseController.MODE_TELEPORT);
            Core.getInstance().setHint("First click - start point, second click - destination point.");

        }
        if (e.getSource() == mode_message) {
            MyMouseController.setState(MyMouseController.STATE_MESSAGE);
            Core.getInstance().setHint("Place message block and enter text for it.");

        }
        if (e.getSource() == mode_selection) {
            MyMouseController.setState(MyMouseController.MODE_SELECTION);
            Core.getInstance().setHint("Set the top-left corner of region.");
        }

        if (e.getSource() == help) {
            //default title and icon
            JOptionPane.showMessageDialog(this, "Rise.", "Help", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

