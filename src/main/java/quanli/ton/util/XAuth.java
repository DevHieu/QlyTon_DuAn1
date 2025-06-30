package quanli.ton.util;

import quanli.ton.entity.User;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author hieud
 */
public class XAuth {
     public static User user = User.builder()
            .username("user1@gmail.com")
            .password("123")
            .enabled(true)
            .manager(true)
            .fullname("Nguyễn Văn Tèo")
            .build(); // biến user này sẽ được thay thế sau khi đăng nhập

}
