package myproject.controller;

import myproject.model.User;
import myproject.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class NavigationController {

    private static final Logger logger = LoggerFactory.getLogger(NavigationController.class);

    private final UserService userService;

    @Autowired
    public NavigationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegister() {
        logger.debug("Hiển thị trang đăng ký");
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String username, @RequestParam String password, @RequestParam String email, Model model) {
        logger.info("Bắt đầu xử lý đăng ký - Username: {}, Email: {}", username, email);

        // Kiểm tra username đã tồn tại
        if (userService.existsByUsername(username)) {
            logger.warn("Đăng ký thất bại - Username đã tồn tại: {}", username);
            model.addAttribute("error", "Username đã tồn tại! Vui lòng chọn username khác.");
            return "register";
        }

        // Kiểm tra độ dài password
        if (password.length() < 8 || password.length() > 30) {
            logger.warn("Đăng ký thất bại - Mật khẩu không hợp lệ, độ dài: {}. Mật khẩu phải từ 8 đến 30 ký tự.", password.length());
            model.addAttribute("error", "Mật khẩu phải có độ dài từ 8 đến 30 ký tự!");
            return "register";
        }

        // Tạo và lưu user
        User user = new User(username, password, email);
        userService.saveUser(user);
        logger.info("Đăng ký thành công - Username: {}", username);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin() {
        logger.debug("Hiển thị trang đăng nhập");
        return "loginUser";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username, @RequestParam String password, Model model) {
        logger.info("Bắt đầu xử lý đăng nhập - Username: {}", username);
        if (userService.authenticateUser(username, password)) {
            logger.info("Đăng nhập thành công - Username: {}", username);
            return "redirect:/home";
        }
        logger.warn("Đăng nhập thất bại - Username: {}", username);
        model.addAttribute("error", "Username hoặc password không đúng!");
        return "loginUser";
    }

    @GetMapping("/home")
    public String showIndex() {
        logger.info("Đăng nhập thành công - Chuyển hướng đến trang index");
        return "home";
    }

    @GetMapping("/test")
    public String showTest() {
        logger.debug("Hiển thị trang test");
        return "test";
    }

    @GetMapping("/hello")
    public String showHello() {
        logger.debug("Hiển thị trang hello");
        return "hello";
    }
    @GetMapping("/weather/static-weather-details")
    public String showWeatherDetails(Model model) {
        logger.info("Handling request for /weather/static-weather-details");
        model.addAttribute("test", "Weather Details Page"); // Thêm để kiểm tra
        return "weather-details"; // Trả về view weather-details.jsp
    }
}
