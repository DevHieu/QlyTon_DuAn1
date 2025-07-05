package quanli.ton.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import quanli.ton.entity.User;

/**
 * Lớp tiện ích hỗ trợ truy vấn và chuyển đổi sang đối tượng
 *
 * @author NghiemN
 * @version 1.0
 */
public class XQuery {
    /**
     * Truy vấn 1 đối tượng
     *
     * @param <B> kiểu của đối tượng cần chuyển đổi
     * @param beanClass lớp của đối tượng kết quả
     * @param sql câu lệnh truy vấn
     * @param values các giá trị cung cấp cho các tham số của SQL
     * @return kết quả truy vấn
     * @throws RuntimeException lỗi truy vấn
     */
    public static <B> B getSingleBean(Class<B> beanClass, String sql, Object... values) {
        List<B> list = XQuery.getBeanList(beanClass, sql, values);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * Truy vấn nhiều đối tượng
     *
     * @param <B> kiểu của đối tượng cần chuyển đổi
     * @param beanClass lớp của đối tượng kết quả
     * @param sql câu lệnh truy vấn
     * @param values các giá trị cung cấp cho các tham số của SQL
     * @return kết quả truy vấn
     * @throws RuntimeException lỗi truy vấn
     */
    public static <B> List<B> getBeanList(Class<B> beanClass, String sql, Object... values) {
        List<B> list = new ArrayList<>();
        try {
            ResultSet resultSet = XJdbc.executeQuery(sql, values);
            while (resultSet.next()) {
                list.add(XQuery.readBean(resultSet, beanClass));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return list;
    }

    /**
     * Tạo bean với dữ liệu đọc từ bản ghi hiện tại
     *
     * @param <B> kiểu của đối tượng cần chuyển đổi
     * @param resultSet tập bản ghi cung cấp dữ liệu
     * @param beanClass lớp của đối tượng kết quả
     * @return kết quả truy vấn
     * @throws RuntimeException lỗi truy vấn
     */
    private static <B> B readBean(ResultSet resultSet, Class<B> beanClass) throws Exception {
        B bean = beanClass.getDeclaredConstructor().newInstance();
        Method[] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("set") && method.getParameterCount() == 1) {
                try {
                    Object value = null;
                    //Nếu type là Date thì phải get data bằng getTimestamp
                    if (method.getParameterTypes()[0] == Date.class) {
                        java.sql.Timestamp checkinTimestamp = resultSet.getTimestamp(name.substring(3));  // Dùng getTimestamp để lấy
                        if (checkinTimestamp != null) {
                            value = new Date(checkinTimestamp.getTime()); // Đổi từ Timestamp sang Date
                        }
                    } else {
                        value = resultSet.getObject(name.substring(3));
                    }
//                    System.out.printf("→ Field: %s, Value: %s, Type: %s%n", name.substring(3), value, (value == null ? "null" : value.getClass().getName()));
                    method.invoke(bean, value);
                } catch (IllegalAccessException e) {
                    System.out.printf("+ IllegalAccessException: Cannot access the field '%s'\r\n", name.substring(3));
                } catch (IllegalArgumentException e) {
                    System.out.printf("+ IllegalArgumentException: Invalid argument for the field '%s'\r\n", name.substring(3));
                } catch (InvocationTargetException e) {
                    System.out.printf("+ InvocationTargetException: Error invoking method for field '%s'\r\n", name.substring(3));
                } catch (SQLException e) {
                    System.out.printf("+ SQLException: Column '%s' not found or other SQL error\r\n", name.substring(3));
                }
            }
        }
        return bean;
    }

    public static <A> List<A> getEntityList(Class<A> aClass, String sql) {
        List<A> list = new ArrayList<>();
        try {
            ResultSet resultSet = XJdbc.executeQuery(sql);
            while (resultSet.next()) {
                list.add(XQuery.readBean(resultSet, aClass));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return list;
    }
//
//    public static void main(String[] args) {
//        demo1();
//        demo2();
//    }
//
//    private static void demo1() {
//        String sql = "SELECT * FROM Users WHERE Username=? AND Password=?";
//        User user = XQuery.getSingleBean(User.class, sql, "NghiemN", "123456");
//    }
//
//    private static void demo2() {
//        String sql = "SELECT * FROM Users WHERE Fullname LIKE ?";
//        List<User> list = XQuery.getBeanList(User.class, sql, "%Nguyễn %");
//    }
}
