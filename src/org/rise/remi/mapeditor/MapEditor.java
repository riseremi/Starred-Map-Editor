package org.rise.remi.mapeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import lombok.Getter;
import org.rise.remi.blocks.Message;
import org.rise.remi.blocks.Teleport;
import org.rise.remi.components.ExtFileFilter;
import org.rise.remi.components.RMenuBar;
import org.rise.remi.components.Sidebar;
import org.rise.remi.controllers.Controller;
import org.rise.remi.controllers.MyMouseController;
import org.rise.remi.layer.LayerLoader;

/**
 * @author remi
 */
public final class MapEditor extends JFrame implements ActionListener {

    //private static @Getter final Core core = new Core();
    private static final Controller c = new Controller();
    private static @Getter Sidebar sidebar;
    private @Getter static JTabbedPane tab;
    private static JPanel contentPanel = new JPanel(new BorderLayout());
    //
    private static final DefaultListModel listModel = new DefaultListModel();
    private static final DefaultListModel listModel2 = new DefaultListModel();
    private static JList<Teleport> teleportList = new JList<>(listModel);
    private static JList<Message> messageList = new JList<>(listModel2);
    private static JPopupMenu teleportsPopupMenu = new JPopupMenu("Which?");
    public static Teleport selectedTeleport;
    public static Message selectedMessage;
    private final JFileChooser fc = new JFileChooser();

    //TODO сохранение скриншота карты
    public MapEditor() {
        RMenuBar bar = new RMenuBar();
        setJMenuBar(bar);

        //прочее
        sidebar = new Sidebar();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Map Editor \"" + "new_map.m" + "\"");
        setResizable(false);
        setPreferredSize(new Dimension(Global.windowWidth, Global.windowHeight));

        setLayout(new BorderLayout());
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(Global.tileWidth * Global.sidebarWidth, 0));
        sidebar.setSize(new Dimension(Global.tileWidth * Global.sidebarWidth, 0));
        System.out.println(Global.tileWidth * Global.sidebarWidth);
        System.out.println(sidebar.getWidth());

        //выбранный телепорт
        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                JList theList = (JList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        Teleport t = teleportList.getSelectedValue();
                        //12 и 8 - смещение от краев экрана, чтобы телепорт был в центре
                        Core.getInstance().getWorld().getWorldLayer().setBlocksX(-t.getSourceX() + 12);
                        Core.getInstance().getWorld().getWorldLayer().setBlocksY(-t.getSourceY() + 10);
                        selectedTeleport = t;
                    }
                } else if (mouseEvent.getClickCount() == 1) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        Teleport t = teleportList.getSelectedValue();
                        selectedTeleport = t;
                    }
                }
            }
        };

        teleportList.addMouseListener(mouseListener);

        //some absurd mumbo-jumbo
        MouseListener mouseListener2 = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                JList theList = (JList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        Message msg = messageList.getSelectedValue();
                        //12 и 8 - смещение от краев экрана, чтобы телепорт был в центре
                        Core.getInstance().getWorld().getWorldLayer().setBlocksX(-msg.getSourceX() + 12);
                        Core.getInstance().getWorld().getWorldLayer().setBlocksY(-msg.getSourceY() + 10);
                        selectedMessage = msg;
                    }
                } else if (mouseEvent.getClickCount() == 1) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        Message msg = messageList.getSelectedValue();
                        selectedMessage = msg;
                    }
                }
            }
        };
        messageList.addMouseListener(mouseListener2);

        teleportList.setAutoscrolls(true);
        messageList.setAutoscrolls(true);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(teleportList);

        JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setViewportView(messageList);

        tab = new JTabbedPane();
        tab.setPreferredSize(new Dimension(Global.tileWidth * Global.sidebarWidth + Global.tileWidth, 0));

        teleportList.setFocusable(false);
        messageList.setFocusable(false);

        tab.addTab("Tiles", sidebar);
        tab.addTab("Warps", scrollPane);
        tab.addTab("Msg", scrollPane2);

        tab.setFocusable(false);

        contentPanel.add(tab, BorderLayout.EAST);
        contentPanel.add(Core.getInstance());

        JToolBar toolBar = new JToolBar("Still draggable");
        toolBar.setFocusable(false);
        toolBar.add(getNavButton("newFile", "NEW", "Create new file", "New"));
        toolBar.add(getNavButton("openFile", "OPEN", "Open a file", "Open"));
        toolBar.add(getNavButton("save", "SAVE", "Save current file", "Save"));
        toolBar.add(getNavButton("saveAs", "SAVEAS", "Save current file with specific name", "Save As"));
        toolBar.add(getNavButton("layer0", "LAYER0", "Save current file with specific name", "Layer 0"));
        toolBar.add(getNavButton("layer1", "LAYER1", "Save current file with specific name", "Layer 1"));
        toolBar.add(getNavButton("layer2", "LAYER2", "Save current file with specific name", "Layer 2"));
        toolBar.add(getNavButton("newFile", "FILL", "Save current file with specific name", "Layer 2"));

        getContentPane().add(toolBar, BorderLayout.PAGE_START);
        //contentPanel.requestFocus();

        add(contentPanel);

        sidebar.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                if (notches < 0) {
                    sidebar.moveUp();
                } else {
                    sidebar.moveDown();
                }
            }
        });

        addKeyListener(c);

        MyMouseController mainMouseController = new MyMouseController(true, this);
        MyMouseController sidebarMouseController = new MyMouseController(false, this);

        contentPanel.addMouseMotionListener(mainMouseController);
        contentPanel.addMouseListener(mainMouseController);

        sidebar.addMouseListener(sidebarMouseController);
        sidebar.addMouseMotionListener(sidebarMouseController);

        fc.setFileFilter(new ExtFileFilter("rsng", "*.m Map files"));
    }

    protected JButton getNavButton(String imageName, String actionCommand, String toolTipText, String altText) {
        //Look for the image.
        String imgLocation = "/res/main/buttons/" + imageName + ".gif";
        URL imageURL = MapEditor.class.getResource(imgLocation);

        //Create and initialize the button.
        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);

        if (imageURL != null) {                      //image found
            button.setIcon(new ImageIcon(imageURL, altText));
        } else {                                     //no image found
            button.setText(altText);
            System.err.println("Resource not found: " + imgLocation);
        }
        button.setFocusable(false);

        return button;
    }

    public static void initTeleportsList(Teleport[] teleports) {
        listModel.clear();
        for (Teleport teleport : teleports) {
            listModel.addElement(teleport);
        }
    }

    public static void initMessagesList(Message[] messages) {
        listModel2.clear();
        for (Message message : messages) {
            listModel2.addElement(message);
        }
    }

    public static void deleteCurrentTeleport() {
        Teleport t = teleportList.getSelectedValue();
        Core.getTeleports().remove(t);
        MapEditor.initTeleportsList(LayerLoader.getTeleportsArray());
    }

    public static void deleteCurrentMessage() {
        Message msg = messageList.getSelectedValue();
        Core.getMessages().remove(msg);
        MapEditor.initMessagesList(LayerLoader.getMessagesArray());
    }

    public static void main(String[] args) {
        //System.setProperty("sun.java2d.opengl", "True");

        MapEditor m = new MapEditor();
        m.setVisible(true);
        m.pack();
        m.setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("NEW".equals(e.getActionCommand())) {
            Core.getInstance().recreate();
        }
        if ("OPEN".equals(e.getActionCommand())) {
            try {
                //ret - 0 если нажато "Открыть", 1 если "Отмена" и -1 если произошла ошибка
                int ret = fc.showOpenDialog(this);
                if (ret == 0) {
                    File fileToOpen = fc.getSelectedFile();
                    LayerLoader.newLoadFromFileToVersion2(fileToOpen.getAbsolutePath(), Core.getInstance().getWorld());
                    MapEditor.getFrames()[0].setTitle("Map Editor \"" + fileToOpen.getName() + "\"");
                }
            } catch (HeadlessException | IOException ex) {
            }
        }

        if ("LAYER0".equals(e.getActionCommand())) {
            Core.getInstance().getWorld().setLayer(0);
        }
        if ("LAYER1".equals(e.getActionCommand())) {
            Core.getInstance().getWorld().setLayer(1);
        }
        if ("LAYER2".equals(e.getActionCommand())) {
            Core.getInstance().getWorld().setLayer(2);
        }
        if ("FILL".equals(e.getActionCommand())) {
            MyMouseController.setState(MyMouseController.STATE_FILL);
        }
    }
}
