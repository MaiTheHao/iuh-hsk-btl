package main.java.util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class ImageUtil {

    private static final int MAX_CACHE_SIZE = 100;
    private static final Map<String, ImageIcon> cache = new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, ImageIcon> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    public static ImageIcon createIcon(String path, int width, int height) {
        if (path == null || path.isEmpty()) return null;

        String key = String.format("%s_%dx%d", path, width, height);

        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        try {
            Image originalImg = loadImage(path);
            if (originalImg != null) {
                ImageIcon scaledIcon = scaleImage(originalImg, width, height);
                cache.put(key, scaledIcon);
                return scaledIcon;
            }
        } catch (Exception e) {
            System.err.println("Lỗi nạp ảnh: " + path + " - " + e.getMessage());
        }
        return null;
    }

    private static Image loadImage(String path) throws Exception {
        if (path.startsWith("http") || path.contains("://")) {
            return ImageIO.read(new URL(path));
        } 
        
        URL imgURL = ImageUtil.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        }
        
        return new ImageIcon(path).getImage();
    }

    private static ImageIcon scaleImage(Image source, int width, int height) {
        Image scaled = source.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}