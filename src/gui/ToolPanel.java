package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ToolPanel extends JPanel {
    // Komponenty interfejsu
    private JButton fillButton;
    private JPanel currentColorPanel;
    private Color currentColor = Color.BLACK;

    // Interfejsy nasłuchów
    public interface ToolChangeListener {
        void toolChanged(String newTool);
    }

    // Listy słuchaczy
    private final List<ToolChangeListener> toolListeners = new ArrayList<>();
    private final List<ActionListener> colorListeners = new ArrayList<>();

    public ToolPanel() {
        setLayout(new BorderLayout());
        initComponents();
        setupListeners();
    }

    private void initComponents() {
        // Panel narzędzi
        JPanel toolButtonsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        fillButton = new JButton("Wypełnienie");

        // Style przycisków
        fillButton.setBackground(new Color(240, 240, 240));

        toolButtonsPanel.add(fillButton);

        // Panel koloru
        JPanel colorPanel = new JPanel(new BorderLayout(5, 5));
        JButton colorButton = new JButton("Wybierz kolor");
        currentColorPanel = new JPanel();
        currentColorPanel.setBackground(currentColor);
        currentColorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        currentColorPanel.setPreferredSize(new Dimension(50, 50));
        colorPanel.add(colorButton, BorderLayout.NORTH);
        colorPanel.add(currentColorPanel, BorderLayout.CENTER);

        // Główny układ
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(new JLabel("Narzędzia:", JLabel.LEFT));
        mainPanel.add(toolButtonsPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(colorPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Dodanie kolorów predefiniowanych
        JPanel predefColorPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        predefColorPanel.setBorder(BorderFactory.createTitledBorder("Kolory"));
        addColorButtons(predefColorPanel);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(predefColorPanel);

        // Dodanie marginesów
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        wrapperPanel.add(mainPanel, BorderLayout.NORTH);

        add(wrapperPanel, BorderLayout.CENTER);
    }

    private void addColorButtons(JPanel panel) {
        // Klasa zagnieżdżona do tworzenia przycisków kolorów
        class ColorButton extends JButton {
            private final Color color;

            public ColorButton(Color color) {
                this.color = color;
                setBackground(color);
                setPreferredSize(new Dimension(30, 30));
                setBorder(BorderFactory.createLineBorder(Color.BLACK));

                // Wykorzystanie EventDispatchThread dla obsługi zdarzeń
                addActionListener(e -> SwingUtilities.invokeLater(() -> setCurrentColor(color)));
            }
        }

        panel.add(new ColorButton(Color.BLACK));
        panel.add(new ColorButton(Color.RED));
        panel.add(new ColorButton(Color.GREEN));
        panel.add(new ColorButton(Color.BLUE));
        panel.add(new ColorButton(Color.YELLOW));
        panel.add(new ColorButton(Color.MAGENTA));
        panel.add(new ColorButton(Color.CYAN));
        panel.add(new ColorButton(Color.WHITE));
    }

    private void setupListeners() {
        // Nasłuchy przycisków narzędzi
        fillButton.addActionListener(e -> notifyToolListeners("fill"));

        // Nasłuch przycisku wyboru koloru
        JButton chooseColorButton = (JButton)((JPanel)currentColorPanel.getParent()).getComponent(0);
        chooseColorButton.addActionListener(e -> {
            Color chosenColor = JColorChooser.showDialog(ToolPanel.this, "Wybierz kolor", currentColor);
            if (chosenColor != null) {
                setCurrentColor(chosenColor);
            }
        });
    }

    public void addToolChangeListener(ToolChangeListener listener) {
        toolListeners.add(listener);
    }

    public void addColorChangeListener(ActionListener listener) {
        colorListeners.add(listener);
    }

    private void notifyToolListeners(String tool) {
        // Upewnienie się, że powiadomienia są wysyłane w EDT
        if (SwingUtilities.isEventDispatchThread()) {
            for (ToolChangeListener listener : toolListeners) {
                listener.toolChanged(tool);
            }
        } else {
            SwingUtilities.invokeLater(() -> {
                for (ToolChangeListener listener : toolListeners) {
                    listener.toolChanged(tool);
                }
            });
        }
    }

    private void notifyColorListeners() {
        // Upewnienie się, że powiadomienia są wysyłane w EDT
        if (SwingUtilities.isEventDispatchThread()) {
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "colorChanged");
            for (ActionListener listener : colorListeners) {
                listener.actionPerformed(event);
            }
        } else {
            SwingUtilities.invokeLater(() -> {
                ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "colorChanged");
                for (ActionListener listener : colorListeners) {
                    listener.actionPerformed(event);
                }
            });
        }
    }

    public void setCurrentColor(Color color) {
        currentColor = color;

        // Aktualizacja UI w EDT
        if (SwingUtilities.isEventDispatchThread()) {
            currentColorPanel.setBackground(color);
            currentColorPanel.repaint();
            notifyColorListeners();
        } else {
            SwingUtilities.invokeLater(() -> {
                currentColorPanel.setBackground(color);
                currentColorPanel.repaint();
                notifyColorListeners();
            });
        }
    }

    public Color getCurrentColor() {
        return currentColor;
    }
}