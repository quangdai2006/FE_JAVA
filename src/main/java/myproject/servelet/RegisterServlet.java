package myproject.servelet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import myproject.dao.UserDAO;
import myproject.model.User;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        String email = request.getParameter("email");

        User newUser = new User();
        newUser.setUsername(user);
        newUser.setPassword(pass); // 🔒 Bạn nên mã hóa bằng BCrypt ở đây
        newUser.setEmail(email);

        boolean success = new UserDAO().register(newUser);

        if (success)
            response.sendRedirect("login.jsp");
        else
            response.sendRedirect("register.jsp?error=true");
    }
}

