package quanli.ton.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp tiện ích hỗ trợ làm việc với CSDL quan hệ
 *
 * @author NghiemN
 * @version 1.0
 */
public class XJdbc {

    private static Connection connection;

    /**
     * Mở kết nối nếu chưa mở hoặc đã đóng
     *
     * @return Kết nối đã sẵn sàng
     */
    public static Connection openConnection() {

        var driver = "com.mysql.cj.jdbc.Driver";
        var dburl = "jdbc:mysql://localhost:3306/QLyTon?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=True"; // <--- SỬA LẠI CHỖ NÀY
        var username = "huynhtrunghieu";
        var password = "123";

        try {
            if (!XJdbc.isReady()) {
                Class.forName(driver);
                connection = DriverManager.getConnection(dburl, username, password);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    /**
     * Đóng kết nối
     */
    public static void closeConnection() {
        try {
            if (XJdbc.isReady()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Kiểm tra kết nối đã sẵn sàng hay chưa
     * @return true nếu kết nối đã được mở
     */
    public static boolean isReady() {
        try {
            return (connection != null && !connection.isClosed());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Thao tác dữ liệu
     *
     * @param sql câu lệnh SQL (INSERT, UPDATE, DELETE)
     * @param values các giá trị cung cấp cho các tham số trong SQL
     * @return số lượng bản ghi đã thực hiện
     * @throws RuntimeException không thực thi được câu lệnh SQL
     */
    public static int executeUpdate(String sql, Object... values) {
        
        try {
            var stmt = XJdbc.getStmt(sql, values);
            return stmt.executeUpdate();
        } 
        catch (SQLIntegrityConstraintViolationException ex) {
            if (ex.getMessage().contains("foreign key")) {
                System.out.println(sql);
                XDialog.alert("Không thể xóa vì dữ liệu đang được dùng", "Lỗi khóa ngoại");
            } else {
                XDialog.alert("Id đã tồn tại. Vui lòng chọn id khác", "Lỗi trùng khóa");
            }
            throw new RuntimeException(ex.getMessage());
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Truy vấn dữ liệu
     *
     * @param sql câu lệnh SQL (SELECT)
     * @param values các giá trị cung cấp cho các tham số trong SQL ( không truyền values vẫn hoạt động được )
     * @return tập kết quả truy vấn
     * @throws RuntimeException không thực thi được câu lệnh SQL
     */
    public static ResultSet executeQuery(String sql, Object... values) {
        try {
            var stmt = XJdbc.getStmt(sql, values);
            return stmt.executeQuery();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Truy vấn một giá trị
     *
     * @param <T> kiểu dữ liệu kết quả
     * @param sql câu lệnh SQL (SELECT)
     * @param values các giá trị cung cấp cho các tham số trong SQL
     * @return giá trị truy vấn hoặc null
     * @throws RuntimeException không thực thi được câu lệnh SQL
     */
    public static <T> T getValue(String sql, Object... values) {
        try {
            var resultSet = XJdbc.executeQuery(sql, values);
            if (resultSet.next()) {
                return (T) resultSet.getObject(1);
            }
            return null;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Tạo PreparedStatement từ câu lệnh SQL/PROC
     *
     * @param sql câu lệnh SQL/PROC
     * @param values các giá trị cung cấp cho các tham số trong SQL/PROC
     * @return đối tượng đã tạo
     * @throws SQLException không tạo được PreparedStatement
     */
    private static PreparedStatement getStmt(String sql, Object... values) throws SQLException {
        var conn = XJdbc.openConnection();
        var stmt = sql.trim().startsWith("{") ? conn.prepareCall(sql) : conn.prepareStatement(sql);
        for (int i = 0; i < values.length; i++) {
            stmt.setObject(i + 1, values[i]);
        }
        return stmt;
    }

    public static void main(String[] args) {
        demo1();
        demo2();
        demo3();
    }

//   
    private static void demo1() {
    String sql = "SELECT * FROM Products WHERE UnitPrice BETWEEN ? AND ?";
    try (ResultSet rs = XJdbc.executeQuery(sql, 1.5, 5.0)) {
        while (rs.next()) {
            System.out.println(rs.getString("Name"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private static void demo2() {
    String sql = "SELECT MAX(UnitPrice) FROM Products WHERE UnitPrice > ?";
    Number maxPrice = XJdbc.getValue(sql, 1.5);
    System.out.println("Max Price: " + maxPrice.doubleValue());
}

private static void demo3() {
    String sql = "DELETE FROM Products WHERE UnitPrice < ?";
    int count = XJdbc.executeUpdate(sql, 0.0);
    System.out.println("Deleted rows: " + count);
}
}
