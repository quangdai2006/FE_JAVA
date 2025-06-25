<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Đăng nhập - VietWeather</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', -apple-system, BlinkMacSystemFont, sans-serif;
            background: linear-gradient(135deg, #1e3c72 0%, #2a5298 25%, #3d6cb9 50%, #4a73c1 75%, #5a7bc7 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            position: relative;
            overflow: hidden;
        }

        /* Animated Weather Elements */
        .weather-elements {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            pointer-events: none;
            z-index: 1;
        }

        .raindrop {
            position: absolute;
            width: 2px;
            height: 20px;
            background: linear-gradient(to bottom, rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.1));
            border-radius: 50px;
            animation: rain 1.5s linear infinite;
        }

        .raindrop:nth-child(1) { left: 10%; animation-delay: 0s; animation-duration: 1.2s; }
        .raindrop:nth-child(2) { left: 20%; animation-delay: 0.2s; animation-duration: 1.4s; }
        .raindrop:nth-child(3) { left: 30%; animation-delay: 0.4s; animation-duration: 1.1s; }
        .raindrop:nth-child(4) { left: 40%; animation-delay: 0.6s; animation-duration: 1.3s; }
        .raindrop:nth-child(5) { left: 50%; animation-delay: 0.8s; animation-duration: 1.5s; }
        .raindrop:nth-child(6) { left: 60%; animation-delay: 0.3s; animation-duration: 1.2s; }
        .raindrop:nth-child(7) { left: 70%; animation-delay: 0.5s; animation-duration: 1.4s; }
        .raindrop:nth-child(8) { left: 80%; animation-delay: 0.7s; animation-duration: 1.1s; }
        .raindrop:nth-child(9) { left: 90%; animation-delay: 0.1s; animation-duration: 1.3s; }

        @keyframes rain {
            0% { top: -20px; opacity: 1; }
            100% { top: 100vh; opacity: 0.3; }
        }

        /* Navigation */
        .nav-header {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            padding: 20px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            z-index: 10;
        }

        .logo-section {
            display: flex;
            align-items: center;
            color: white;
            font-size: 24px;
            font-weight: 600;
        }

        .logo-icon {
            width: 40px;
            height: 40px;
            background: linear-gradient(135deg, #ff9500, #ffb84d);
            border-radius: 50%;
            margin-right: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
        }

        .nav-menu {
            display: flex;
            gap: 20px;
        }

        .nav-item {
            color: rgba(255, 255, 255, 0.8);
            text-decoration: none;
            padding: 8px 16px;
            border-radius: 8px;
            transition: all 0.3s ease;
            font-size: 14px;
        }

        .nav-item:hover {
            background: rgba(255, 255, 255, 0.1);
            color: white;
        }

        /* Login Card */
        .login-card {
            background: rgba(255, 255, 255, 0.15);
            backdrop-filter: blur(25px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 24px;
            padding: 45px 40px;
            box-shadow: 0 25px 70px rgba(0, 0, 0, 0.3);
            width: 100%;
            max-width: 420px;
            text-align: center;
            position: relative;
            z-index: 5;
        }

        .login-header {
            margin-bottom: 35px;
        }

        .weather-emoji {
            font-size: 48px;
            margin-bottom: 15px;
            display: inline-block;
            animation: weather-rotate 4s ease-in-out infinite;
        }

        @keyframes weather-rotate {
            0%, 100% { transform: rotate(0deg) scale(1); }
            25% { transform: rotate(-5deg) scale(1.1); }
            75% { transform: rotate(5deg) scale(1.1); }
        }

        h2 {
            color: white;
            font-size: 28px;
            font-weight: 600;
            margin: 0;
        }

        .subtitle {
            color: rgba(255, 255, 255, 0.7);
            font-size: 14px;
            margin-top: 8px;
        }

        .input-group {
            position: relative;
            margin-bottom: 25px;
        }

        .input-icon {
            position: absolute;
            left: 18px;
            top: 50%;
            transform: translateY(-50%);
            font-size: 16px;
            color: rgba(255, 255, 255, 0.6);
            z-index: 2;
        }

        input[type="text"],
        input[type="password"] {
            width: 100%;
            padding: 18px 25px 18px 50px;
            border: 2px solid rgba(255, 255, 255, 0.2);
            border-radius: 16px;
            font-size: 16px;
            background: rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
            color: white;
            outline: none;
            transition: all 0.3s ease;
        }

        input[type="text"]::placeholder,
        input[type="password"]::placeholder {
            color: rgba(255, 255, 255, 0.5);
        }

        input[type="text"]:focus,
        input[type="password"]:focus {
            border-color: rgba(255, 149, 0, 0.8);
            box-shadow: 0 0 0 4px rgba(255, 149, 0, 0.15);
            background: rgba(255, 255, 255, 0.2);
            transform: translateY(-2px);
        }

        input[type="text"]:focus + .input-icon,
        input[type="password"]:focus + .input-icon {
            color: #ff9500;
        }

        .login-btn {
            width: 100%;
            padding: 18px;
            background: linear-gradient(135deg, #ff9500 0%, #ffb84d 100%);
            color: white;
            border: none;
            border-radius: 16px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            margin: 20px 0;
            text-transform: uppercase;
            letter-spacing: 1px;
            position: relative;
            overflow: hidden;
        }

        .login-btn::after {
            content: '';
            position: absolute;
            top: 50%;
            left: 50%;
            width: 0;
            height: 0;
            background: rgba(255, 255, 255, 0.2);
            border-radius: 50%;
            transform: translate(-50%, -50%);
            transition: width 0.6s, height 0.6s;
        }

        .login-btn:hover::after {
            width: 300px;
            height: 300px;
        }

        .login-btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 12px 35px rgba(255, 149, 0, 0.4);
        }

        .register-link {
            color: rgba(255, 255, 255, 0.8);
            text-decoration: none;
            font-size: 14px;
            transition: all 0.3s ease;
            display: inline-block;
            padding: 12px 20px;
            border-radius: 12px;
            border: 1px solid rgba(255, 255, 255, 0.2);
            background: rgba(255, 255, 255, 0.05);
        }

        .register-link:hover {
            background: rgba(255, 149, 0, 0.2);
            border-color: rgba(255, 149, 0, 0.5);
            color: white;
            transform: translateY(-2px);
        }

        .weather-status {
            margin-top: 25px;
            padding: 15px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 12px;
            border: 1px solid rgba(255, 255, 255, 0.15);
        }

        .weather-status p {
            color: rgba(255, 255, 255, 0.8);
            font-size: 13px;
            margin: 0;
            line-height: 1.4;
        }

        @media (max-width: 768px) {
            .nav-menu {
                display: none;
            }
        }

        @media (max-width: 480px) {
            .login-card {
                padding: 35px 30px;
                margin: 20px;
            }

            h2 {
                font-size: 24px;
            }

            .logo-section {
                font-size: 20px;
            }

            .weather-emoji {
                font-size: 40px;
            }
        }
    </style>
</head>
<body>
<div class="weather-elements">
    <div class="raindrop"></div>
    <div class="raindrop"></div>
    <div class="raindrop"></div>
    <div class="raindrop"></div>
    <div class="raindrop"></div>
    <div class="raindrop"></div>
    <div class="raindrop"></div>
    <div class="raindrop"></div>
    <div class="raindrop"></div>
</div>

<div class="nav-header">
    <div class="logo-section">
        <div class="logo-icon">☀️</div>
        VietWeather
    </div>
    <div class="nav-menu">
        <a href="/home" class="nav-item">🏠 Trang chủ</a>
        <a href="/weather" class="nav-item">🌤️ Thời tiết</a>
        <a href="/radar" class="nav-item">📡 Radar</a>
        <a href="/forecast" class="nav-item">📅 Dự báo</a>
    </div>
</div>

<div class="login-card">
    <div class="login-header">
        <div class="weather-emoji">⛈️</div>
        <h2>Đăng Nhập</h2>
        <p class="subtitle">Truy cập thông tin thời tiết cá nhân hóa</p>
    </div>

    <form action="/login" method="post">
        <div class="input-group">
            <input type="text" name="username" placeholder="Tên đăng nhập" required />
            <span class="input-icon">👤</span>
        </div>
        <div class="input-group">
            <input type="password" name="password" placeholder="Mật khẩu" required />
            <span class="input-icon">🔒</span>
        </div>
        <button type="submit" class="login-btn">Đăng nhập</button>
    </form>

    <a href="/register" class="register-link">Chưa có tài khoản? Đăng ký ngay</a>

    <div class="weather-status">
        <p>🌍 Cập nhật thời tiết realtime từ khắp Việt Nam</p>
    </div>
</div>
</body>
</html>