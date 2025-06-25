package myproject.service;

import myproject.model.User;
import myproject.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticateUser(String username, String password) {
        logger.debug("Bắt đầu xác thực người dùng - Username: {}", username);
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            logger.info("Xác thực thành công - Username: {}", username);
            return true;
        }
        logger.warn("Xác thực thất bại - Username: {}", username);
        return false;
    }

    public User saveUser(User user) {
        logger.debug("Lưu người dùng - Username: {}", user.getUsername());
        User savedUser = userRepository.save(user);
        logger.info("Lưu người dùng thành công - Username: {}", savedUser.getUsername());
        return savedUser;
    }

    public boolean existsByUsername(String username) {
        logger.debug("Kiểm tra username tồn tại - Username: {}", username);
        boolean exists = userRepository.existsByUsername(username);
        if (exists) {
            logger.info("Username đã tồn tại - Username: {}", username);
        } else {
            logger.info("Username chưa tồn tại - Username: {}", username);
        }
        return exists;
    }
}