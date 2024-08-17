package cum.jesus.jesusclient.util;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final Color backgroundColor = new Color(40, 40, 40);
    private static final Color titleColor = new Color(255, 85, 75);

    private static final Font consolas = new Font("Consolas", Font.PLAIN, 12);
    private static final Font consolasTitle = new Font("Consolas", Font.BOLD, 18);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        JFrame frame = new JFrame();
        frame.setTitle("JesusClient");
        frame.setUndecorated(true);
        frame.setSize(700, 880);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(backgroundColor);
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("An uncaught exception was caught by JesusClient!", SwingConstants.CENTER);
        titleLabel.setForeground(titleColor);
        titleLabel.setFont(consolasTitle);

        JLabel exceptionClassLabel = new JLabel("Exception class: " + e.getClass().getCanonicalName(), SwingConstants.LEFT);
        exceptionClassLabel.setForeground(Color.WHITE);
        exceptionClassLabel.setFont(consolas);

        JLabel exceptionMessageLabel = new JLabel("Exception message: " + e.getLocalizedMessage(), SwingConstants.LEFT);
        exceptionMessageLabel.setForeground(Color.WHITE);
        exceptionMessageLabel.setFont(consolas);

        JLabel exceptionCauseLabel = null;
        if (e.getCause() != null) {
            exceptionCauseLabel = new JLabel("Exception cause: " + e.getCause().getClass(), SwingConstants.LEFT);
            exceptionCauseLabel.setForeground(Color.WHITE);
            exceptionCauseLabel.setFont(consolas);
        }

        StackTraceElement[] stackTraceElements = e.getStackTrace();
        StackTraceElement cause = stackTraceElements[0];
        String causeDetails = String.format("At %s.%s:%d",
                cause.getClassName(),
                cause.getMethodName(),
                cause.getLineNumber());

        JLabel causeLabel = new JLabel(causeDetails);
        causeLabel.setForeground(Color.WHITE);
        causeLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setBackground(backgroundColor);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        detailsPanel.add(exceptionClassLabel);
        detailsPanel.add(exceptionMessageLabel);
        if (exceptionCauseLabel != null) detailsPanel.add(exceptionCauseLabel);
        detailsPanel.add(causeLabel);

        JPanel buttonPanel = getButtons(frame);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(detailsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);

        frame.setVisible(true);

        e.printStackTrace();
    }

    private static JPanel getButtons(JFrame frame) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setLayout(new FlowLayout());

        JButton continueButton = new JButton("Continue");
        continueButton.setForeground(Color.WHITE);
        continueButton.setBackground(backgroundColor);
        continueButton.setFont(consolas);
        continueButton.setFocusPainted(false);
        continueButton.setBorderPainted(false);

        JButton exitButton = new JButton("Exit");
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(backgroundColor);
        exitButton.setFont(consolas);
        exitButton.setFocusPainted(false);
        exitButton.setBorderPainted(false);

        continueButton.addActionListener(event -> {
            frame.dispose();
        });

        exitButton.addActionListener(event -> {
            System.exit(0);
        });

        buttonPanel.add(continueButton);
        buttonPanel.add(exitButton);
        return buttonPanel;
    }
}
