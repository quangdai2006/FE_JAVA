<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="vi">
<footer class="footer">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <div class="footer-main">
        <div class="container">
            <div class="row g-4">
                <!-- Brand Section -->
                <div class="col-lg-3 col-md-6">
                    <div class="footer-brand text-center text-md-start">
                        <h3><i class="fas fa-sun me-2"></i>VietWeather</h3>
                        <p>Cung cấp thông tin thời tiết chính xác và cập nhật cho toàn bộ Việt Nam</p>
                        <div class="social-links">
                            <a href="#" title="Facebook"><i class="fab fa-facebook-f"></i></a>
                            <a href="#" title="Twitter"><i class="fab fa-twitter"></i></a>
                            <a href="#" title="Instagram"><i class="fab fa-instagram"></i></a>
                            <a href="#" title="YouTube"><i class="fab fa-youtube"></i></a>
                        </div>
                    </div>
                </div>

                <!-- Services Section -->
                <div class="col-lg-2 col-md-6">
                    <div class="footer-section">
                        <h5><i class="bx bx-cloud me-2"></i>Dịch vụ</h5>
                        <ul>
                            <li><a href="#">Dự báo thời tiết</a></li>
                            <li><a href="#">Bản đồ thời tiết</a></li>
                            <li><a href="#">Cảnh báo thời tiết</a></li>
                            <li><a href="#">Thống kê khí hậu</a></li>
                            <li><a href="#">Radar thời tiết</a></li>
                        </ul>
                    </div>
                </div>

                <!-- Information Section -->
                <div class="col-lg-2 col-md-6">
                    <div class="footer-section">
                        <h5><i class="bx bx-info-circle me-2"></i>Thông tin</h5>
                        <ul>
                            <li><a href="#">Về chúng tôi</a></li>
                            <li><a href="#">Liên hệ</a></li>
                            <li><a href="#">Điều khoản sử dụng</a></li>
                            <li><a href="#">Chính sách bảo mật</a></li>
                            <li><a href="#">Hỗ trợ</a></li>
                        </ul>
                    </div>
                </div>

                <!-- Regions Section -->
                <div class="col-lg-2 col-md-6">
                    <div class="footer-section">
                        <h5><i class="bx bx-map me-2"></i>Khu vực</h5>
                        <ul>
                            <li><a href="#">Hà Nội</a></li>
                            <li><a href="#">TP. Hồ Chí Minh</a></li>
                            <li><a href="#">Đà Nẵng</a></li>
                            <li><a href="#">Nha Trang</a></li>
                            <li><a href="#">Đà Lạt</a></li>
                        </ul>
                    </div>
                </div>

                <!-- Mobile App Section -->
                <div class="col-lg-3 col-md-6">
                    <div class="footer-section app-download">
                        <h5><i class="bx bx-mobile me-2"></i>Ứng dụng di động</h5>
                        <p>Tải ứng dụng để nhận thông báo thời tiết</p>
                        <div class="app-buttons">
                            <a href="#" class="app-button">
                                <i class="fab fa-apple"></i>
                                <div class="app-info">
                                    <small>Tải xuống từ</small>
                                    <div class="app-name">App Store</div>
                                </div>
                            </a>
                            <a href="#" class="app-button">
                                <i class="fab fa-google-play"></i>
                                <div class="app-info">
                                    <small>Tải xuống từ</small>
                                    <div class="app-name">Google Play</div>
                                </div>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer Bottom -->
    <div class="footer-bottom">
        <div class="container">
            <div class="text-center">
                <div class="copyright">
                    <i class="fas fa-copyright me-1"></i>
                    2025 VietWeather, Inc. "VietWeather" và thiết kế mặt trời là các nhãn hiệu được đăng ký của VietWeather, Inc. Bảo lưu mọi quyền.
                </div>

                <div class="footer-links">
                    <a href="#">Điều khoản sử dụng</a>
                    <span class="separator">|</span>
                    <a href="#">Chính sách về quyền riêng tư</a>
                    <span class="separator">|</span>
                    <a href="#">Chính sách về cookie</a>
                    <span class="separator">|</span>
                    <a href="#">Sitemap</a>
                </div>
            </div>
        </div>
    </div>
</footer>


<script>
    // Thêm hiệu ứng smooth scroll cho các liên kết hợp lệ
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            const href = this.getAttribute('href');
            const isValidId = href && href !== '#' && document.querySelector(href);

            if (isValidId) {
                e.preventDefault();
                const target = document.querySelector(href);
                target.scrollIntoView({
                    behavior: 'smooth'
                });
            }
        });
    });

    // Thêm hiệu ứng search box khi focus
    const searchBox = document.querySelector('.search-box');
    if (searchBox && searchBox.parentElement) {
        searchBox.addEventListener('focus', function() {
            this.parentElement.style.transform = 'scale(1.02)';
        });

        searchBox.addEventListener('blur', function() {
            this.parentElement.style.transform = 'scale(1)';
        });
    }
</script>