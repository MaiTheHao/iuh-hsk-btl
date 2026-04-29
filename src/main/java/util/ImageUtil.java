package main.java.util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;

public class ImageUtil {
    
    /**
     * Tạo ImageIcon từ đường dẫn và kích thước mong muốn.
     * 
     * @param path Đường dẫn ảnh (URL http, /path/to/resource, hoặc file path)
     * @param width Chiều rộng mong muốn
     * @param height Chiều cao mong muốn
     * @return ImageIcon
     */
    public static ImageIcon createIcon(String path, int width, int height) {
        if (path == null || path.isEmpty()) return null;
        try {
            Image img;
            if (path.startsWith("http") || path.contains("://")) {
                img = ImageIO.read(new URL(path));
            } else {
                URL imgURL = ImageUtil.class.getResource(path);
                if (imgURL != null) {
                    img = new ImageIcon(imgURL).getImage();
                } else {
                    img = new ImageIcon(path).getImage();
                }
            }
            
            if (img != null) {
                return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {
            System.err.println("Lỗi nạp ảnh từ: " + path + " - " + e.getMessage());
        }
        return null;
    }
}
