package app;

import gui.MainFrame;
import templates.*;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class ColoringApp {
    private MainFrame mainFrame;

    public ColoringApp() {
        // Inicjalizacja szablonów obrazków
        initializeTemplates();

        // Utworzenie głównego okna
        mainFrame = new MainFrame("Aplikacja do kolorowania");
        mainFrame.setVisible(true);
    }

    private void initializeTemplates() {
        // Rejestracja dostępnych szablonów
        TemplateRegistry.registerTemplate(new FlowerTemplate());
        TemplateRegistry.registerTemplate(new HouseTemplate());
        TemplateRegistry.registerTemplate(new AnimalTemplate());
    }

    // Metoda do wykonywania intensywnych operacji poza EDT
    // Wykorzystuje SwingWorker, który nie blokuje EDT
    public static <T> void executeInBackground(final BackgroundTask<T> task) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return task.execute();
            }

            @Override
            protected void done() {
                try {
                    T result = get();
                    SwingUtilities.invokeLater(() -> task.done(result));
                } catch (Exception e) {
                    task.onError(e);
                }
            }
        }.execute();
    }

    // Interfejs do zadań w tle
    public interface BackgroundTask<T> {
        T execute() throws Exception;
        void done(T result);
        default void onError(Exception e) {
            System.err.println("Błąd wykonywania zadania w tle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Klasa zagnieżdżona do zarządzania szablonami
    public static class TemplateRegistry {
        private static java.util.List<model.shapes.ImageTemplate> templates = new java.util.ArrayList<>();

        public static void registerTemplate(model.shapes.ImageTemplate template) {
            templates.add(template);
        }

        public static java.util.List<model.shapes.ImageTemplate> getTemplates() {
            return java.util.Collections.unmodifiableList(templates);
        }

        public static model.shapes.ImageTemplate getTemplate(int index) {
            if (index >= 0 && index < templates.size()) {
                return templates.get(index);
            }
            return null;
        }
    }
}