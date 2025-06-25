
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%-- Include header --%>
<%@ include file="/WEB-INF/views/partials/header.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Style/commonstyle.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/Style/mainhome.css">
<main class="main-content">
    <!-- Nearby Locations -->
    <section class="nearby-section">
        <div class="container">
            <h2 class="section-title">ĐỊA ĐIỂM GẦN ĐÂY</h2>
            <div class="location-cards-container">
                <div class="row g-3">
                    <div class="col-lg-6 col-md-12">
                        <div class="location-card weather-item" data-city="Hà Nội">
                            <div class="row align-items-center">
                                <div class="col-8">
                                    <div class="location-info">
                                        <h5 class="mb-1 city-name">Hà Nội</h5>
                                        <small class="text-white">Việt Nam</small>
                                    </div>
                                </div>
                                <div class="col-4">
                                    <div class="weather-info">
                                        <div class="weather-icon"><div class="icon-placeholder"></div></div>
                                        <p class="temperature temp-value">Loading...</p>
                                        <p class="real-feel">RealFeel Loading...</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6 col-md-12">
                        <div class="location-card weather-item" data-city="Thành phố Hồ Chí Minh">
                            <div class="row align-items-center">
                                <div class="col-8">
                                    <div class="location-info">
                                        <h5 class="mb-1 city-name">TP. Hồ Chí Minh</h5>
                                        <small class="text-white">Việt Nam</small>
                                    </div>
                                </div>
                                <div class="col-4">
                                    <div class="weather-info">
                                        <div class="weather-icon"><div class="icon-placeholder"></div></div>
                                        <p class="temperature temp-value">Loading...</p>
                                        <p class="real-feel">RealFeel Loading...</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Weather Details Section -->
    <section class="weather-details-section py-5">
        <div class="container">
            <div class="weather-header mb-4">
                <h2 class="section-title text-white text-center mb-3">
                    <i class="bx bx-sun me-2"></i>
                    VIỆT NAM ĐIỀU KIỆN THỜI TIẾT
                </h2>
                <div class="view-more-container">
                    <a href="${pageContext.request.contextPath}/weather/static-weather-details" class="view-more-link">
                        Xem thêm
                        <i class="bx bx-right-arrow-alt ms-1"></i>
                    </a>
                </div>
            </div>
            <div class="weather-list-container">
                <div class="card border-0 shadow-lg">
                    <div class="card-body p-0">
                        <div class="weather-list">
                            <!-- Column 1 -->
                            <div class="weather-column" id="weather-column-1">
                                <!-- Các mục thời tiết sẽ được thêm động bởi JavaScript -->
                            </div>
                            <!-- Column 2 -->
                            <div class="weather-column" id="weather-column-2">
                                <!-- Các mục thời tiết sẽ được thêm động bởi JavaScript -->
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Additional Services Section -->
    <section class="services-section py-5">
        <div class="container">
            <h2 class="section-title text-center mb-5">
                <i class="bx bx-cog me-2"></i>
                DỊCH VỤ THỜI TIẾT
            </h2>
            <div class="row g-4">
                <div class="col-lg-3 col-md-6">
                    <div class="service-card h-100">
                        <div class="card border-0 shadow-lg h-100">
                            <div class="card-body text-center p-4">
                                <div class="service-icon mb-3">
                                    <i class="bx bx-wind display-3 text-primary"></i>
                                </div>
                                <h5 class="card-title text-white">GIÓ XOÁY</h5>
                                <p class="card-text text-white">Theo dõi và cảnh báo các hiện tượng gió xoáy trên toàn quốc</p>
                                <button class="btn btn-outline-primary btn-sm">
                                    <i class="bx bx-right-arrow-alt me-1"></i>
                                    Chi tiết
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="service-card h-100">
                        <div class="card border-0 shadow-lg h-100">
                            <div class="card-body text-center p-4">
                                <div class="service-icon mb-3">
                                    <i class="bx bx-error display-3 text-danger"></i>
                                </div>
                                <h5 class="card-title text-white">THỜI TIẾT KHẮC NGHIỆT</h5>
                                <p class="card-text text-white">Cảnh báo sớm về các hiện tượng thời tiết nguy hiểm</p>
                                <button class="btn btn-outline-danger btn-sm">
                                    <i class="bx bx-right-arrow-alt me-1"></i>
                                    Chi tiết
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="service-card h-100">
                        <div class="card border-0 shadow-lg h-100">
                            <div class="card-body text-center p-4">
                                <div class="service-icon mb-3">
                                    <i class="bx bx-radar display-3 text-success"></i>
                                </div>
                                <h5 class="card-title text-white">RADAR & BẢN ĐỒ</h5>
                                <p class="card-text text-white">Bản đồ radar thời gian thực với độ chính xác cao</p>
                                <button class="btn btn-outline-success btn-sm">
                                    <i class="bx bx-right-arrow-alt me-1"></i>
                                    Chi tiết
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="service-card h-100">
                        <div class="card border-0 shadow-lg h-100">
                            <div class="card-body text-center p-4">
                                <div class="service-icon mb-3">
                                    <i class="bx bx-video display-3 text-info"></i>
                                </div>
                                <h5 class="card-title text-white">VIDEO</h5>
                                <p class="card-text text-white">Video dự báo thời tiết hàng ngày từ các chuyên gia</p>
                                <button class="btn btn-outline-info btn-sm">
                                    <i class="bx bx-right-arrow-alt me-1"></i>
                                    Chi tiết
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Weather News Section -->
    <section class="news-section py-5">
        <div class="container">
            <h2 class="section-title text-center mb-5">
                <i class="bx bx-news me-2"></i>
                TIN TỨC THỜI TIẾT
            </h2>
            <div class="row g-4">
                <div class="col-lg-8">
                    <div class="featured-news">
                        <div class="card border-0 shadow-lg h-100">
                            <div class="card-body p-4">
                                <div class="news-image mb-3">
                                    <div class="placeholder-img bg-gradient text-center py-5">
                                        <i class="fas fa-image display-2 text-muted"></i>
                                        <p class="text-muted mt-2">Hình ảnh tin tức</p>
                                    </div>
                                </div>
                                <div class="news-content">
                                    <span class="badge bg-warning text-dark mb-2">Tin nổi bật</span>
                                    <h4 class="text-white mb-3">Dự báo thời tiết tuần tới: Miền Bắc có mưa rào và dông</h4>
                                    <p class="text-white mb-3">
                                        Theo Trung tâm Dự báo Khí tượng Thủy văn Quốc gia, trong tuần tới, khu vực miền Bắc sẽ có mưa rào và dông rải rác,
                                        nhiệt độ dao động từ 24-30°C. Người dân cần chú ý theo dõi thông tin dự báo...
                                    </p>
                                    <div class="news-meta d-flex justify-content-between align-items-center">
                                        <small class="text-white">
                                            <i class="bx bx-time me-1"></i>
                                            2 phút trước
                                        </small>
                                        <button class="btn btn-outline-warning btn-sm">
                                            Đọc thêm
                                            <i class="bx bx-right-arrow-alt ms-1"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="latest-news">
                        <h5 class="text-white mb-3">
                            <i class="bx bx-trending-up me-2"></i>
                            Tin mới nhất
                        </h5>
                        <div class="news-item mb-3">
                            <div class="card border-0 shadow">
                                <div class="card-body p-3">
                                    <h6 class="text-white mb-2">Cảnh báo mưa lớn tại các tỉnh miền Trung</h6>
                                    <p class="text-white small mb-2">
                                        Các tỉnh từ Thanh Hóa đến Quảng Ngãi có khả năng xuất hiện mưa to đến rất to...
                                    </p>
                                    <small class="text-white">
                                        <i class="bx bx-time me-1"></i>
                                        4 giờ trước
                                    </small>
                                </div>
                            </div>
                        </div>
                        <div class="news-item mb-3">
                            <div class="card border-0 shadow">
                                <div class="card-body p-3">
                                    <h6 class="text-white mb-2">Nhiệt độ miền Nam tăng cao trong tuần này</h6>
                                    <p class="text-white small mb-2">
                                        Khu vực Nam Bộ có nắng nóng với nhiệt độ cao nhất có thể lên đến 36-38°C...
                                    </p>
                                    <small class="text-white">
                                        <i class="bx bx-time me-1"></i>
                                        6 giờ trước
                                    </small>
                                </div>
                            </div>
                        </div>
                        <div class="news-item">
                            <div class="card border-0 shadow">
                                <div class="card-body p-3">
                                    <h6 class="text-white mb-2">Chỉ số UV cao, cần phòng chống nắng nóng</h6>
                                    <p class="text-white small mb-2">
                                        Chỉ số tia UV trong ngày hôm nay có thể đạt mức 8-10, người dân cần chú ý...
                                    </p>
                                    <small class="text-white">
                                        <i class="bx bx-time me-1"></i>
                                        1 ngày trước
                                    </small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</main>

<%-- Include footer --%>
<%@ include file="/WEB-INF/views/partials/footer.jsp" %>
<script>
    window.contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/JavaScript/home.js"></script>
