package main.java.connectDB;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

/**
 * Lớp kết nối Cơ sở dữ liệu.
 * <p>
 * Lưu ý: Copy file 'src/main/resources/env.example.xml' thành 'env.xml'
 * và cấu hình các thông số (url, user, password) phù hợp với SQL Server cục bộ.
 * </p>
 * 
 * @author: Mai Thế Hào
 */
public class ConnectDB {
    private static String url;
    private static String user;
    private static String password;

    static {
        try {
            File xmlFile = new File(ConnectDB.class.getClassLoader().getResource("main/resources/env.xml").toURI());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            url = doc.getElementsByTagName("url").item(0).getTextContent();
            user = doc.getElementsByTagName("user").item(0).getTextContent();
            password = doc.getElementsByTagName("password").item(0).getTextContent();
        } catch (Exception e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
