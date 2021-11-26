package me.rochblondiaux.client.utils;

import lombok.NonNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class ImageUtils {

    public static ImageIcon resizeImageIcon(@NonNull ImageIcon imageIcon, int width, int height) {
        return new ImageIcon(imageIcon.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH));
    }

    public static Image resizeBufferedImage(@NonNull BufferedImage image, int width, int height) {
        return image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
    }

    public static ImageIcon getIconFromResources(String path) {
        return new ImageIcon(getResourcesURL("/icons/" + path));
    }

    public static BufferedImage getBufferedImageFromResource(String path) {
        try {
            return ImageIO.read(getResourcesURL(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL getResourcesURL(String path) {
        return ImageUtils.class.getResource(path);
    }
}
