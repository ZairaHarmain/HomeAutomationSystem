package com.home;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeAutomationSystem extends JFrame {

    private boolean lightOn = false;
    private boolean fanOn   = false;

    private JLabel  lightStatusLabel;
    private JLabel  fanStatusLabel;
    private JLabel  acTempLabel;
    private JSlider acSlider;
    private JTextArea eventLog;

    private static final Color BG_DARK    = new Color(15,  17,  23);
    private static final Color BG_CARD    = new Color(30,  37,  51);
    private static final Color COL_TEXT   = new Color(226, 232, 240);
    private static final Color COL_MUTED  = new Color(148, 163, 184);
    private static final Color COL_GREEN  = new Color(52,  211, 153);
    private static final Color COL_RED    = new Color(239, 68,  68);
    private static final Color COL_BLUE   = new Color(96,  165, 250);
    private static final Color COL_BORDER = new Color(51,  65,  85);
    private static final Color BTN_ON_BG  = new Color(5,   150, 105);
    private static final Color BTN_OFF_BG = new Color(55,  65,  81);

    public HomeAutomationSystem() {
        setTitle("Home Automation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG_DARK);
        buildUI();
        logEvent("System started. All devices OFF.");
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BG_DARK);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(root);

        JLabel title = new JLabel("Home Automation Control Panel", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 0, 6, 0));
        root.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG_DARK);

        center.add(buildDeviceRow("Lights", "light"));
        center.add(Box.createVerticalStrut(12));
        center.add(buildDeviceRow("Fan", "fan"));
        center.add(Box.createVerticalStrut(12));
        center.add(buildACPanel());

        root.add(center, BorderLayout.CENTER);
        root.add(buildLogPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildDeviceRow(String name, String id) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COL_BORDER),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(COL_TEXT);
        card.add(nameLabel, BorderLayout.WEST);

        JLabel statusLabel = new JLabel("OFF");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setForeground(COL_MUTED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setPreferredSize(new Dimension(50, 24));

        if (id.equals("light")) lightStatusLabel = statusLabel;
        else                    fanStatusLabel   = statusLabel;

        card.add(statusLabel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(BG_CARD);

        JButton onBtn  = makeToggleBtn("ON",  BTN_ON_BG);
        JButton offBtn = makeToggleBtn("OFF", BTN_OFF_BG);

        onBtn.addActionListener(e  -> toggleDevice(id, true,  statusLabel, onBtn, offBtn));
        offBtn.addActionListener(e -> toggleDevice(id, false, statusLabel, onBtn, offBtn));

        btnPanel.add(onBtn);
        btnPanel.add(offBtn);
        card.add(btnPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel buildACPanel() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COL_BORDER),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);

        JLabel acName = new JLabel("AC Temperature");
        acName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        acName.setForeground(COL_TEXT);

        acTempLabel = new JLabel("18 C");
        acTempLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        acTempLabel.setForeground(COL_BLUE);

        header.add(acName,      BorderLayout.WEST);
        header.add(acTempLabel, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        acSlider = new JSlider(JSlider.HORIZONTAL, 16, 30, 18);
        acSlider.setMajorTickSpacing(2);
        acSlider.setPaintTicks(true);
        acSlider.setPaintLabels(true);
        acSlider.setBackground(BG_CARD);
        acSlider.setForeground(COL_MUTED);
        acSlider.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        acSlider.addChangeListener(e -> {
            int val = acSlider.getValue();
            acTempLabel.setText(val + " C");
            if (!acSlider.getValueIsAdjusting()) {
                logEvent("AC temperature set to " + val + " C");
            }
        });

        card.add(acSlider, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(BG_DARK);

        JLabel logTitle = new JLabel("Event Log");
        logTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        logTitle.setForeground(new Color(107, 114, 128));
        panel.add(logTitle, BorderLayout.NORTH);

        eventLog = new JTextArea(6, 30);
        eventLog.setEditable(false);
        eventLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        eventLog.setBackground(new Color(10, 10, 15));
        eventLog.setForeground(COL_MUTED);
        eventLog.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane scroll = new JScrollPane(eventLog);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(30, 58, 46)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void toggleDevice(String id, boolean turnOn,
                               JLabel statusLabel,
                               JButton onBtn, JButton offBtn) {
        if (id.equals("light")) lightOn = turnOn;
        else                    fanOn   = turnOn;

        if (turnOn) {
            statusLabel.setForeground(COL_GREEN);
            statusLabel.setText("ON");
            onBtn.setBackground(new Color(16, 185, 129));
            offBtn.setBackground(BTN_OFF_BG);
        } else {
            statusLabel.setForeground(COL_RED);
            statusLabel.setText("OFF");
            onBtn.setBackground(BTN_ON_BG);
            offBtn.setBackground(new Color(185, 28, 28));
        }

        String deviceName = id.equals("light") ? "Lights" : "Fan";
        logEvent(deviceName + " turned " + (turnOn ? "ON" : "OFF"));
    }

    private void logEvent(String message) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        eventLog.append("[" + time + "]  " + message + "\n");
        eventLog.setCaretPosition(eventLog.getDocument().getLength());
    }

    private JButton makeToggleBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(60, 32));
        return b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new HomeAutomationSystem().setVisible(true);
        });
    }
}