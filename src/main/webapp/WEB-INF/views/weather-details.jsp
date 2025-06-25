<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%-- Include header --%>
<%@ include file="/WEB-INF/views/partials/header.jsp" %>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/boxicons@2.1.4/css/boxicons.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/Style/commonstyle.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/Style/maindetails.css">

<!-- jQuery and Chart.js -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<body>
<div class="container main-container">
    <div class="row g-4">
        <!-- Left Column: Main Weather Info -->
        <div class="col-lg-8">
            <!-- Current Weather -->
            <h2 class="section-title">
                <i class="fas fa-cloud-sun"></i>
                Thời tiết hiện tại
            </h2>
            <div class="card weather-card current-weather mb-4">
                <div class="card-body p-4">
                    <div class="location-header">
                        <h1 class="location-title">
                            <i class="bx bx-map"></i>
                            <span id="city-name">Đang tải...</span>
                        </h1>
                        <div class="current-datetime" id="liveDateTime">
                            <i class="bx bx-time"></i>
                            <span id="dateTimeDisplay">Đang tải...</span>
                        </div>
                    </div>

                    <div class="main-weather-display">
                        <div class="weather-icon-main"></div>
                        <div class="temperature-display">
                            <div class="current-temp">Đang tải...</div>
                            <div class="feels-like">
                                <i class="bx bx-thermometer"></i>
                                RealFeel® Đang tải...
                            </div>
                        </div>
                    </div>

                    <div class="weather-condition">Đang tải...</div>

                    <div class="weather-metrics">
                        <div class="metric-item">
                            <i class="bx bx-wind metric-icon text-info"></i>
                            <div class="metric-value">Đang tải...</div>
                            <div class="metric-label">Gió</div>
                        </div>
                        <div class="metric-item">
                            <i class="bx bx-droplet metric-icon text-primary"></i>
                            <div class="metric-value">Đang tải...</div>
                            <div class="metric-label">Độ ẩm</div>
                        </div>
                        <div class="metric-item">
                            <i class="fas fa-eye metric-icon text-success"></i>
                            <div class="metric-value">Đang tải...</div>
                            <div class="metric-label">Tầm nhìn</div>
                        </div>
                        <div class="metric-item">
                            <i class="bx bx-cloud-lightning metric-icon text-danger"></i>
                            <div class="metric-value">Đang tải...</div>
                            <div class="metric-label">Trần mây</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Hourly Forecast -->
            <h2 class="section-title">
                <i class="bx bx-time-five"></i>
                Dự báo theo giờ
            </h2>
            <div class="card weather-card hourly-forecast mb-4" id="hourly-forecast-container">
                <div class="card-body p-4">
                    <!-- Hourly items will be populated dynamically -->
                </div>
            </div>

            <!-- 5-Day Forecast -->
            <h2 class="section-title">
                <i class="bx bx-calendar"></i>
                Dự báo theo ngày
            </h2>
            <div class="card weather-card daily-forecast mb-4" id="daily-forecast-container">
                <div class="card-body p-4">
                    <!-- Daily items will be populated dynamically -->
                </div>
            </div>

            <!-- Weather Statistics -->
            <h2 class="section-title">
                <i class="bx bx-bar-chart-alt-2"></i>
                Thống kê thời tiết theo ngày
            </h2>
            <div class="card weather-card mb-4">
                <div class="card-body p-4">
                    <div id="weather-stats"></div>
                </div>
            </div>
        </div>

        <!-- Right Column: Additional Info -->
        <div class="col-lg-4">
            <!-- Astronomical Information -->
            <h2 class="section-title">
                <i class="bx bx-sun"></i>
                Thiên văn
            </h2>
            <div class="card weather-card info-card mb-4">
                <div class="card-body p-4">
                    <div class="info-item">
                        <span class="info-label">
                            <i class="fas fa-sun text-warning"></i>
                            Mặt trời mọc
                        </span>
                        <span class="info-value">Đang tải...</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">
                            <i class="fas fa-sun text-warning"></i>
                            Mặt trời lặn
                        </span>
                        <span class="info-value">Đang tải...</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">
                            <i class="fas fa-moon text-info"></i>
                            Mặt trăng mọc
                        </span>
                        <span class="info-value">Đang tải...</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">
                            <i class="fas fa-moon text-info"></i>
                            Mặt trăng lặn
                        </span>
                        <span class="info-value">Đang tải...</span>
                    </div>
                </div>
            </div>

            <!-- Extended Weather Information -->
            <h2 class="section-title">
                <i class="bx bx-info-circle"></i>
                Thông tin mở rộng
            </h2>
            <div class="card weather-card info-card mb-4">
                <div class="card-body p-4">
                    <div class="info-item">
                        <span class="info-label">
                            <i class="bx bx-sun text-warning"></i>
                            Chỉ số UV
                        </span>
                        <span class="info-value">Đang tải...</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">
                            <i class="bx bx-wind text-info"></i>
                            Gió giật mạnh
                        </span>
                        <span class="info-value">Đang tải...</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">
                            <i class="bx bx-cloud-rain text-primary"></i>
                            Khả năng mưa
                        </span>
                        <span class="info-value">Đang tải...</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/partials/footer.jsp" %>
<script>
    window.contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/JavaScript/weather-details.js"></script>
</body>
</html>