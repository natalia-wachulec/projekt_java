import app.ColoringApp;

public class Main {
    public static void main(String[] args) {
        // Uruchomienie aplikacji w wątku dystrybucji zdarzeń (EDT)
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                // Opcjonalnie - ustawienie wyglądu aplikacji
                javax.swing.UIManager.setLookAndFeel(
                        javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Błąd ustawienia wyglądu: " + e.getMessage());
            }

            new ColoringApp();
        });
    }
}