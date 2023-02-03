package cum.jesus.jesusclient.gui.externalconsole;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.gui.externalconsole.cmd.*;
import org.apache.commons.lang3.math.NumberUtils;

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

    public static Color mainColor = new Color(40, 40, 40);
    public static Color errColor = new Color(255, 85, 85);

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

                    // Math impl
                    if (NumberUtils.isNumber(String.valueOf(text.toCharArray()[0]))) {
                        try {
                            String math;
                            double tmp = eval(text);
                            if (tmp == (int)tmp) {
                                math = String.valueOf((int)tmp);
                            } else {
                                math = String.valueOf(tmp);
                            }

                            println(math);
                        } catch (RuntimeException ex) {
                            println("Error while attempting to evaluate: \"" + text + "\"", false, errColor);
                            println("You must provide a valid number for the expression", false, errColor);
                        }
                    } else
                        doCommand(text);

                    println(JesusClient.username + "> " + text, false);

                    scrollBottom();
                    input.setText("");
                }
            }
        });

        // check for arrow keys and switch to previous commands
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

    public void println(String s) {
        println(s, false);
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

    private static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            //        | functionName `(` expression `)` | functionName factor
            //        | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}
