const cities = [
    'Ha Noi', 'Ho Chi Minh', 'Da Nang', 'Hai Phong',
    'Can Tho', 'Bac Giang', 'Cao Bang', 'Ninh Binh',
    'Quang Ngai', 'Thanh Hoa', 'Tay Ninh', 'Ca Mau'
];

const cityNameMap = {
    'Ha Noi': 'Hà Nội',
    'Ho Chi Minh': 'Thành phố Hồ Chí Minh',
    'Da Nang': 'Đà Nẵng',
    'Hai Phong': 'Hải Phòng',
    'Can Tho': 'Cần Thơ',
    'Bac Giang': 'Bắc Giang',
    'Cao Bang': 'Cao Bằng',
    'Ninh Binh': 'Ninh Bình',
    'Quang Ngai': 'Quảng Ngãi',
    'Thanh Hoa': 'Thanh Hóa',
    'Tay Ninh': 'Tây Ninh',
    'Ca Mau': 'Cà Mau'
};

// Initialize search and nearby locations functionality
document.addEventListener('DOMContentLoaded', function () {
    initializeSearch();
    initializeNearbyLocations();
});

// Search functionality
function initializeSearch() {
    // Xử lý click nút search
    $('#searchBtn').on('click', function () {
        const city = $('#searchInput').val().trim();
        if (!city) {
            alert('Vui lòng nhập tên thành phố!');
            return;
        }
        performSearch(city);
    });

    // Xử lý nhấn Enter trong ô nhập liệu
    $('#searchInput').on('keypress', function (e) {
        if (e.which === 13) {
            const city = $(this).val().trim();
            if (city) {
                performSearch(city);
            }
        }
    });

    // Search input focus effects
    $('#searchInput').on('focus', function () {
        $(this).parent().addClass('shadow-lg');
    }).on('blur', function () {
        $(this).parent().removeClass('shadow-lg');
    });

    // Navbar scroll effect
    $(window).scroll(function () {
        if ($(window).scrollTop() > 100) {
            $('.main-header').addClass('shadow-lg');
        } else {
            $('.main-header').removeClass('shadow-lg');
        }
    });
}

// Hàm thực hiện tìm kiếm và lưu vào LocalStorage
function performSearch(city) {
    $('#searchBtn').html('<i class="fas fa-spinner fa-spin"></i>');
    // Lấy location key từ database
    $.ajax({
        url: window.contextPath + '/weather/location?city=' + encodeURIComponent(city) + '&countryId=VN',
        method: 'GET',
        success: function (locationData) {
            $('#searchBtn').html('<i class="fas fa-search"></i>');
            if (locationData && locationData.locationKey) {
                // Lưu thành phố vào LocalStorage
                saveCityToLocalStorage(city);
                // Chuyển hướng đến weather-details.jsp
                window.location.href = `${window.contextPath}/weather/static-weather-details?city=${encodeURIComponent(city)}`;
            } else {
                console.error(`Không tìm thấy location key cho thành phố: ${city}`);
                alert(`Không tìm thấy thành phố: ${city}`);
            }
        },
        error: function (xhr) {
            $('#searchBtn').html('<i class="fas fa-search"></i>');
            console.error(`Lỗi khi lấy location cho ${city}: ${xhr.responseJSON ? xhr.responseJSON.error : 'Lỗi không xác định'}`);
            alert(`Lỗi khi tìm kiếm thành phố: ${city}`);
        }
    });

    // Cập nhật dữ liệu thời tiết cho thành phố đã biết (giữ chức năng hiện tại)
    if (cities.includes(city) || Object.keys(cityNameMap).includes(city)) {
        $.ajax({
            url: window.contextPath + '/weather/fetch-and-save-current?city=' + encodeURIComponent(city),
            method: 'GET',
            success: function (weatherData) {
                if (weatherData && weatherData.weatherText) {
                    let $item = $(`.weather-item[data-city="${cityNameMap[city] || city}"]`);
                    if ($item.length) {
                        const { icon, colorClass, animation } = getWeatherIcon(weatherData.weatherText);
                        $item.find('.temp-value').text(weatherData.temperature !== undefined ? weatherData.temperature + '°C' : 'N/A');
                        $item.find('.real-feel').text(weatherData.realFeelTemperature !== undefined ? `RealFeel ${weatherData.realFeelTemperature}°C` : 'RealFeel N/A');
                        updateWeatherIcon($item, icon, colorClass, animation);
                    }
                }
            },
            error: function (xhr) {
                console.error(`Lỗi khi lấy dữ liệu thời tiết cho ${city}: ${xhr.responseJSON ? xhr.responseJSON.error : 'Lỗi không xác định'}`);
            }
        });
    }
}

// Lưu thành phố vào LocalStorage
function saveCityToLocalStorage(city) {
    let recentCities = JSON.parse(localStorage.getItem('recentCities')) || [];
    const cityKey = Object.keys(cityNameMap).find(key => cityNameMap[key] === city) || city;
    const cityDisplay = cityNameMap[cityKey] || city;

    // Xóa thành phố nếu đã tồn tại để đưa lên đầu
    recentCities = recentCities.filter(c => c.key !== cityKey);
    // Thêm thành phố mới vào đầu
    recentCities.unshift({ key: cityKey, display: cityDisplay });
    // Giới hạn tối đa 4 thành phố
    if (recentCities.length > 4) {
        recentCities = recentCities.slice(0, 4);
    }
    localStorage.setItem('recentCities', JSON.stringify(recentCities));
}

// Nearby locations functionality
function initializeNearbyLocations() {
    // Tải danh sách thành phố từ LocalStorage
    const recentCities = JSON.parse(localStorage.getItem('recentCities')) || [];
    const $container = $('.location-cards-container .row');
    $container.empty(); // Xóa toàn bộ nội dung hiện tại

    if (recentCities.length === 0) {
        $container.html('<div class="col-12 text-center text-white">Chưa có địa điểm gần đây</div>');
        return;
    }

    // Tạo thẻ cho từng thành phố
    recentCities.forEach((cityObj, index) => {
        const city = cityObj.key;
        const displayCity = cityObj.display;
        const $item = $(`
            <div class="col-lg-6 col-md-12">
                <div class="location-card weather-item" data-city="${displayCity}">
                    <div class="row align-items-center">
                        <div class="col-8">
                            <div class="location-info">
                                <h5 class="mb-1 city-name">${displayCity}</h5>
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
        `);
        $container.append($item);

        // Tải dữ liệu thời tiết cho thành phố
        $.ajax({
            url: window.contextPath + '/weather/fetch-and-save-current?city=' + encodeURIComponent(city),
            method: 'GET',
            success: function (weatherData) {
                if (weatherData && weatherData.weatherText) {
                    const { icon, colorClass, animation } = getWeatherIcon(weatherData.weatherText);
                    $item.find('.temp-value').text(weatherData.temperature !== undefined ? weatherData.temperature + '°C' : 'N/A');
                    $item.find('.real-feel').text(weatherData.realFeelTemperature !== undefined ? `RealFeel ${weatherData.realFeelTemperature}°C` : 'RealFeel N/A');
                    updateWeatherIcon($item.find('.weather-icon'), icon, colorClass, animation);
                } else {
                    showErrorIcon($item);
                }
            },
            error: function (xhr) {
                console.error(`Lỗi khi lấy dữ liệu cho ${city}: ${xhr.responseJSON ? xhr.responseJSON.error : 'Lỗi không xác định'}`);
                restorePlaceholder($item);
            }
        });
    });

    // Location card click effects
    $('.location-card').on('click', function () {
        const cityName = $(this).find('.city-name').text();
        console.log('Selected city:', cityName);
        $(this).addClass('loading-pulse');
        setTimeout(() => {
            $(this).removeClass('loading-pulse');
        }, 1000);
        const cityKey = Object.keys(cityNameMap).find(key => cityNameMap[key] === cityName) || cityName;
        window.location.href = `${window.contextPath}/weather/static-weather-details?city=${encodeURIComponent(cityKey)}`;
    });
}

// Map WeatherText to Font Awesome icon, color class, and animation
function getWeatherIcon(weatherText) {
    const text = weatherText ? weatherText.toLowerCase() : '';
    if (!text) return { icon: 'fa-exclamation-circle', colorClass: 'weather-error', animation: '' };

    // Sunny and Clear
    if (text === 'sunny' || text === 'clear') {
        return { icon: 'fa-sun', colorClass: 'weather-sunny-bright', animation: 'float-shine' };
    }
    // Partly Sunny or Clear
    if (text.includes('sunny') || text.includes('clear') || text === 'clouds and sun') {
        return { icon: 'fa-cloud-sun', colorClass: 'weather-partly-sunny', animation: 'float-shine' };
    }
    // Intermittent Clouds
    if (text === 'intermittent clouds') {
        return { icon: 'fa-cloud-sun', colorClass: 'weather-intermittent-clouds', animation: 'float-fade' };
    }
    // Mostly Cloudy
    if (text === 'mostly cloudy' || text === 'partly cloudy') {
        return { icon: 'fa-cloud', colorClass: 'weather-mostly-cloudy', animation: 'float-sway' };
    }
    // Cloudy
    if (text === 'cloudy') {
        return { icon: 'fa-cloud', colorClass: 'weather-cloudy', animation: 'float-sway' };
    }
    // Mostly Cloudy with Showers
    if (text === 'mostly cloudy w/ showers') {
        return { icon: 'fa-cloud-showers-heavy', colorClass: 'weather-mostly-cloudy-showers', animation: 'float-rain' };
    }
    // Showers
    if (text === 'showers') {
        return { icon: 'fa-cloud-showers-heavy', colorClass: 'weather-showers', animation: 'float-rain' };
    }
    // Rain
    if (text.includes('rain')) {
        return { icon: 'fa-cloud-showers-heavy', colorClass: 'weather-rainy', animation: 'float-rain' };
    }

    return { icon: 'fa-exclamation-circle', colorClass: 'weather-error', animation: '' };
}

// Update weather icon in DOM
function updateWeatherIcon($item, icon, colorClass, animation) {
    const $iconContainer = $item.find('.weather-icon');
    $iconContainer.empty();
    if (icon && colorClass) {
        const $icon = $(`<i class="fas ${icon} ${colorClass} ${animation}"></i>`);
        $iconContainer.append($icon);
        console.log(`Applied icon: ${icon}, colorClass: ${colorClass}, animation: ${animation}`);
    } else {
        $iconContainer.append('<div class="icon-placeholder"></div>');
        console.log('No icon or colorClass, using placeholder');
    }
}

// Display error icon
function showErrorIcon($item) {
    const $iconContainer = $item.find('.weather-icon');
    $iconContainer.empty();
    $iconContainer.append('<i class="fas fa-exclamation-circle weather-error"></i>');
}

// Restore placeholder if no data
function restorePlaceholder($item) {
    const $iconContainer = $item.find('.weather-icon');
    $iconContainer.empty();
    $iconContainer.append('<div class="icon-placeholder"></div>');
}