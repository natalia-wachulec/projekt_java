package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages the color palette for the coloring application.
 * Provides a set of predefined colors and functionality for custom colors.
 */
public class ColorPalette {
    // Basic predefined colors
    private final List<Color> predefinedColors = Arrays.asList(
            Color.BLACK,
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW,
            Color.MAGENTA,
            Color.CYAN,
            Color.ORANGE,
            Color.PINK,
            Color.DARK_GRAY,
            Color.LIGHT_GRAY,
            Color.WHITE
    );

    // User's custom colors
    private final List<Color> customColors = new ArrayList<>();

    // Currently selected color
    private Color currentColor = Color.BLACK;

    // Maximum number of custom colors to remember
    private static final int MAX_CUSTOM_COLORS = 12;

    /**
     * Gets the list of predefined colors.
     */
    public List<Color> getPredefinedColors() {
        return predefinedColors;
    }

    /**
     * Gets the list of custom colors.
     */
    public List<Color> getCustomColors() {
        return new ArrayList<>(customColors);
    }

    /**
     * Gets the currently selected color.
     */
    public Color getCurrentColor() {
        return currentColor;
    }

    /**
     * Sets the currently selected color.
     */
    public void setCurrentColor(Color color) {
        this.currentColor = color;

        // If this is a custom color not in the lists, add it
        if (!predefinedColors.contains(color) && !customColors.contains(color)) {
            addCustomColor(color);
        }
    }

    /**
     * Adds a new custom color to the palette.
     * If the maximum number of custom colors is reached, the oldest one is removed.
     */
    public void addCustomColor(Color color) {
        // Don't add null colors
        if (color == null) {
            return;
        }

        // If the color is already in the custom list, remove it (we'll add it again at the end)
        customColors.remove(color);

        // Add the new color to the end
        customColors.add(color);

        // If we have too many custom colors, remove the oldest
        if (customColors.size() > MAX_CUSTOM_COLORS) {
            customColors.remove(0);
        }
    }

    /**
     * Creates a new color with the specified RGB values.
     * @param red Red component (0-255)
     * @param green Green component (0-255)
     * @param blue Blue component (0-255)
     * @return The created color
     */
    public Color createColor(int red, int green, int blue) {
        // Ensure values are in valid range
        red = Math.min(255, Math.max(0, red));
        green = Math.min(255, Math.max(0, green));
        blue = Math.min(255, Math.max(0, blue));

        Color newColor = new Color(red, green, blue);
        addCustomColor(newColor);
        return newColor;
    }

    /**
     * Gets a lighter version of the current color.
     */
    public Color getLighterColor() {
        return currentColor.brighter();
    }

    /**
     * Gets a darker version of the current color.
     */
    public Color getDarkerColor() {
        return currentColor.darker();
    }

    /**
     * Clears all custom colors from the palette.
     */
    public void clearCustomColors() {
        customColors.clear();
    }
}