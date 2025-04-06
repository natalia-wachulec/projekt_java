package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ToolPanel extends JPanel {
    // Komponenty interfejsu
    private JButton saveButton;
    private JPanel currentColorPanel;
    private Color currentColor = Color.BLACK;

    // Interfejs dla przycisku Zapisz
    public interface SaveActionListener {
        void saveActionPerformed();
    }

    // Listy słuchaczy
    private final List<ActionListener> colorListeners = new ArrayList<>();
    private final List<SaveActionListener> saveListeners = new ArrayList<>();

    public ToolPanel() {
        setLayout(new BorderLayout());
        initComponents();
        setupListeners();
    }

    private void initComponents() {
        // Główny układ pionowy
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marginesy dla całego panelu

        // Sekcja Akcje (tylko Zapisz)
        JLabel actionsLabel = new JLabel("Akcje:");
        actionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton = new JButton("Zapisz");
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.setBackground(new Color(240, 240, 240));
         // Ustawienie maksymalnego rozmiaru, aby przycisk nie rozciągał się na całą szerokość
        saveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, saveButton.getPreferredSize().height));

        mainPanel.add(actionsLabel);
        mainPanel.add(Box.createVerticalStrut(5)); // Mały odstęp
        mainPanel.add(saveButton);
        mainPanel.add(Box.createVerticalStrut(20)); // Większy odstęp przed następną sekcją


        // Sekcja Wyboru Koloru
        JLabel chooseColorLabel = new JLabel("Wybierz kolor:");
        chooseColorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton colorButton = new JButton("Wybierz z palety");
        colorButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        colorButton.setBackground(new Color(240, 240, 240));
        colorButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, colorButton.getPreferredSize().height));

        currentColorPanel = new JPanel();
        currentColorPanel.setBackground(currentColor);
        currentColorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        currentColorPanel.setPreferredSize(new Dimension(50, 50));
        currentColorPanel.setMaximumSize(new Dimension(50, 50)); // Stały rozmiar kwadratu
        currentColorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(chooseColorLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(currentColorPanel); // Najpierw kwadrat
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(colorButton); // Potem przycisk
        mainPanel.add(Box.createVerticalStrut(20));


        // Sekcja Kolory Predefiniowane
        JPanel predefColorPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        predefColorPanel.setBorder(BorderFactory.createTitledBorder("Kolory"));
        predefColorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addColorButtons(predefColorPanel);
         // Ograniczenie maksymalnej szerokości panelu siatki
        predefColorPanel.setMaximumSize(new Dimension(predefColorPanel.getPreferredSize().width, predefColorPanel.getPreferredSize().height));


        mainPanel.add(predefColorPanel);

        // Dodanie głównego panelu do ToolPanel
        // Używamy PAGE_AXIS, aby elementy zajmowały dostępną szerokość
        // Zamieniamy wrapperPanel na bezpośrednie dodanie mainPanel
        //add(mainPanel, BorderLayout.NORTH); // Ustawienie na górze w BorderLayout
         // Zmieniamy główny layout ToolPanel na BoxLayout dla lepszej kontroli
         setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
         add(mainPanel);
         add(Box.createVerticalGlue()); // Wypełnienie pustej przestrzeni na dole
    }

    private void addColorButtons(JPanel panel) {
        // Klasa zagnieżdżona do tworzenia przycisków kolorów
        class ColorButton extends JButton {
            private final Color color;

            public ColorButton(Color color) {
                this.color = color;
                setPreferredSize(new Dimension(30, 30));
                setBorder(BorderFactory.createLineBorder(Color.BLACK));
                setOpaque(true);
                setContentAreaFilled(false);
                setFocusPainted(false);

                addActionListener(e -> SwingUtilities.invokeLater(() -> setCurrentColor(color)));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(this.color);
                g2.fillRect(0, 0, getWidth(), getHeight());

                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                if (getBorder() != null) {
                    getBorder().paintBorder(this, g2, 0, 0, getWidth(), getHeight());
                }
                g2.dispose();
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
        saveButton.addActionListener(e -> notifySaveListeners());

        // Nasłuch przycisku wyboru koloru ("Wybierz z palety")
        Component[] components = ((JPanel)saveButton.getParent()).getComponents();
        JButton chooseColorButton = null;
        for(Component comp : components) {
            if(comp instanceof JButton && ((JButton)comp).getText().equals("Wybierz z palety")) {
                chooseColorButton = (JButton) comp;
                break;
            }
        }
        if (chooseColorButton != null) {
            chooseColorButton.addActionListener(e -> {
                Color chosenColor = JColorChooser.showDialog(ToolPanel.this, "Wybierz kolor", currentColor);
                if (chosenColor != null) {
                    setCurrentColor(chosenColor);
                }
            });
        } else {
            System.err.println("Nie znaleziono przycisku 'Wybierz z palety' do dodania listenera.");
        }
    }

    public void addColorChangeListener(ActionListener listener) {
        colorListeners.add(listener);
    }

    public void addSaveActionListener(SaveActionListener listener) {
        saveListeners.add(listener);
    }

    private void notifySaveListeners() {
        SwingUtilities.invokeLater(() -> {
            for (SaveActionListener listener : saveListeners) {
                listener.saveActionPerformed();
            }
        });
    }

    private void notifyColorListeners() {
        SwingUtilities.invokeLater(() -> {
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "colorChanged");
            for (ActionListener listener : colorListeners) {
                listener.actionPerformed(event);
            }
        });
    }

    public void setCurrentColor(Color color) {
        currentColor = color;
        SwingUtilities.invokeLater(() -> {
            currentColorPanel.setBackground(color);
            currentColorPanel.repaint();
            notifyColorListeners();
        });
    }

    public Color getCurrentColor() {
        return currentColor;
    }
}