package cum.jesus.jesusclient.gui.externalconsole;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.gui.externalconsole.cmd.*;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Console {
    public static final double VERSION = 1.0;

    public static Console INSTANCE;

    public JFrame frame;
    public JTextPane console;
    public JTextField input;
    public JScrollPane scrollPane;

    public StyledDocument document;

    public Color mainColor = new Color(40, 40, 40);

    ArrayList<String> recent = new ArrayList<String>();
    int recentId = 0;
    int maxRecent = 25;

    public List<Cmd> commandList = new ArrayList<>();
    private void addCommand(Cmd command) {
        commandList.add(command);
    }

    public List<Cmd> getCommandList() {
        return commandList;
    }

    public Cmd getCommandByName(String cmdName) {
        for (Cmd cmd : this.commandList) {
            if (cmd.getName().equalsIgnoreCase(cmdName)) {
                return cmd;
            }
        }

        return null;
    }

    File currentDir;

    public static void start() {
        INSTANCE = new Console();
    }

    public Console() {
        addCommand(new Echo());
        addCommand(new Exit());
        addCommand(new Toggle());
        addCommand(new ChatLogger());
        addCommand(new Exec());

        addCommand(new Help());

        if (JesusClient.INSTANCE != null)
            currentDir = JesusClient.INSTANCE.fileManager.clientDir;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame();
        frame.setTitle("Jesus Client Console v" + VERSION);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/jesusclient/jesus.png")));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        console = new JTextPane();
        console.setEditable(false);
        console.setFont(new Font("Consolas", Font.PLAIN, 14));
        console.setOpaque(false);

        document = console.getStyledDocument();

        println(JesusClient.CLIENT_NAME + " Console " + VERSION, false);
        println("", false);

        input = new JTextField();
        input.setEditable(true);
        input.setFont(new Font("Consolas", Font.PLAIN, 14));
        input.setForeground(Color.WHITE);
        input.setCaretColor(Color.WHITE);
        input.setOpaque(false);

        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = input.getText();
                if (text.length() >= 1) {
                    recent.add(text);
                    recentId = 0;

                    doCommand(text);
                    scrollBottom();
                    input.setText("");
                }
            }
        });

        input.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (recentId < maxRecent - 1 && recentId < (recent.size() - 1)) {
                        recentId++;
                    }

                    input.setText(recent.get(recent.size() - 1 - recentId));
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (recentId > 0) {
                        recentId--;
                    }

                    input.setText(recent.get(recent.size() - 1 - recentId));
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

            @Override
            public void keyTyped(KeyEvent e) {

            }
        });

        scrollPane = new JScrollPane(console);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        frame.add(input, BorderLayout.SOUTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.getContentPane().setBackground(mainColor);

        frame.setSize(840, 500);
        frame.setLocationRelativeTo(null);

        frame.setResizable(true);
        frame.setVisible(true);
    }

    public void doCommand(String cmd) {
        String[] split = cmd.split(" ");

        String command = split[0];
        String[] args = Arrays.copyOfRange(split, 1, split.length);

        try {
            println(JesusClient.username + "> " + cmd, false);
            boolean success = false;

            for (Cmd c : commandList) {
                if (c.getName().equalsIgnoreCase(command)) {
                    c.run(args);
                    success = true;
                }
            }

            if (!success) {
                println("Could not find command: '" + command + "'", false);
            }
        } catch (Exception e) {
            println(Arrays.toString(e.getStackTrace()), false);
        }
    }

    public void scrollTop() {
        console.setCaretPosition(0);
    }

    public void scrollBottom() {
        console.setCaretPosition(console.getDocument().getLength());
    }

    public void print(String s, boolean trace) {
        print(s, trace, Color.WHITE);
    }

    public void print(String s, boolean trace, Color c) {
        Style style = console.addStyle("Style", null);
        StyleConstants.setForeground(style, c);

        if (trace) {
            Throwable t = new Throwable();
            StackTraceElement[] elements = t.getStackTrace();
            String caller = elements[0].getClassName();

            s = "[" + caller + "] " + s;
        }

        try {
            document.insertString(document.getLength(), s, style);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void println(String s, boolean trace) {
        println(s, trace, Color.WHITE);
    }

    public void println(String s, boolean trace, Color c) {
        print(s + "\n", trace, c);
    }

    public void clear() {
        try {
            document.remove(0, document.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        if (INSTANCE == null) return;
        if (INSTANCE.frame == null) return;

        INSTANCE.frame.dispose();
        INSTANCE.commandList.clear();
    }
}
