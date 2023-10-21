import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import javax.swing.*;

import data.SystemTrayRichPresenceEntry;

public class SystemTrayApp {

    // https://docs.oracle.com/javase/tutorial/uiswing/examples/misc/TrayIconDemoProject/src/misc/TrayIconDemo.java

    private Collection<SystemTrayRichPresenceEntry> richPresenceMenuEntries;

    private RichPresence currentPresence;

    public static void init(Collection<SystemTrayRichPresenceEntry> richPresenceMenuEntries) {
        //Schedule a job for the event-dispatching thread:
        //adding TrayIcon.
        var app = new SystemTrayApp(richPresenceMenuEntries);
        SwingUtilities.invokeLater(app::buildAppGUI);
    }

    private SystemTrayApp(Collection<SystemTrayRichPresenceEntry> richPresenceMenuEntries) {
        this.richPresenceMenuEntries = richPresenceMenuEntries;
    }

    private void buildAppGUI() {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }

        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(this.createImage(Path.of("..", "assets/icon.gif"), "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();
        trayIcon.setImageAutoSize(true);

        // menu options

        MenuItem nothing = new MenuItem("Nothing");
        nothing.addActionListener(e -> {
            if (this.currentPresence != null)
                this.currentPresence.stop();
        });

        List<MenuItem> presences = this.richPresenceMenuEntries.stream()
                .map(this::createMenuItemFromEntry)
                .toList();

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            tray.remove(trayIcon);
            System.exit(0);
        });

        //Add components to popup menu
        popup.add(nothing);
        presences.forEach(popup::add);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }

    //Obtain the image URL
    private Image createImage(Path path, String description) {
        return new ImageIcon(path.toString(), description).getImage();
    }

    private MenuItem createMenuItemFromEntry(SystemTrayRichPresenceEntry entry) {
        if (entry.subEntries == null) {
            MenuItem item = new MenuItem(entry.menuItemName);
            item.addActionListener(e -> {
                if (this.currentPresence != null)
                    this.currentPresence.stop();
                this.currentPresence = new RichPresence(entry);
            });
            return item;
        } else {
            Menu menu = new Menu(entry.menuItemName);
            entry.subEntries.forEach(sub -> menu.add(this.createMenuItemFromEntry(sub)));
            return menu;
        }
    }

}
