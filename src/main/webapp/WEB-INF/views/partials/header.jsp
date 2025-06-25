<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thời tiết hiện tại trên toàn quốc | VietWeather</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.2/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

    <!-- Boxicons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/boxicons/2.1.4/css/boxicons.min.css" rel="stylesheet">

    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Style/commonstyle.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Style/mainhome.css">

    <!-- jQuery -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

    <!-- Bootstrap JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.2/js/bootstrap.bundle.min.js"></script>

    <!-- Search and Nearby Locations JS -->
    <script src="${pageContext.request.contextPath}/JavaScript/header.js"></script>
</head>
<body>
<!-- Header -->
<header class="main-header">
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <a class="navbar-brand" href="/home">
                <i class="fas fa-sun"></i>
                VietWeather
            </a>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link active" href="/home">
                            <i class="bx bx-home me-1"></i>
                            Trang chủ
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#weather">
                            <i class="bx bx-cloud me-1"></i>
                            Thời tiết
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#radar">
                            <i class="bx bx-radar me-1"></i>
                            Radar
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#forecast">
                            <i class="bx bx-calendar me-1"></i>
                            Dự báo
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#news">
                            <i class="bx bx-news me-1"></i>
                            Tin tức
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
</header>

<!-- Search Section -->
<section class="search-section">
    <div class="container">
        <div class="search-container">
            <div class="input-group search-input-group">
                <input type="text" class="form-control search-input" placeholder="Tìm kiếm thành phố, tỉnh thành..." id="searchInput">
                <button class="btn search-btn" type="button" id="searchBtn">
                    <i class="fas fa-search"></i>
                </button>
            </div>
        </div>
    </div>
</section>
</body>
</html>