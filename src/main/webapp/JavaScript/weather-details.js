document.addEventListener('DOMContentLoaded', function () {
    // Định nghĩa cities ở cấp độ toàn cục


    // Get city from URL parameter or default to 'Đà Nẵng'
    const urlParams = new URLSearchParams(window.location.search);
    let city = urlParams.get('city') || 'Da Nang';
    const displayCity = cityNameMap[city] || city;

    initializeWeatherData(city, displayCity);
    initializeDateTime();

    // Add click interactions for metric cards
    document.querySelectorAll('.metric-item').forEach(item => {
        item.addEventListener('click', function() {
            this.style.transform = 'scale(0.95)';
            setTimeout(() => {
                this.style.transform = 'scale(1)';
            }, 150);
        });
    });

    // Add intersection observer for animations
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.animationPlayState = 'running';
            }
        });
    }, observerOptions);
    document.querySelectorAll('.weather-card').forEach(card => {
        observer.observe(card);
    });
});

// Update live date and time
function initializeDateTime() {
    function updateLiveDateTime() {
        const now = new Date();
        const options = {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        };
        const dateTimeString = now.toLocaleDateString('vi-VN', options);
        document.getElementById('dateTimeDisplay').textContent = dateTimeString;
    }
    updateLiveDateTime();
    setInterval(updateLiveDateTime, 60000);
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
function updateWeatherIcon(container, icon, colorClass, animation) {
    container.empty();
    if (icon && colorClass) {
        const $icon = $(`<i class="fas ${icon} ${colorClass} ${animation}"></i>`);
        container.append($icon);
    } else {
        container.append('<div class="icon-placeholder"></div>');
    }
}

// Initialize weather data for the city
function initializeWeatherData(city, displayCity) {
    $('#city-name').text(`${displayCity}, Việt Nam`);

    // Fetch and save all weather data
    $.ajax({
        url: `${window.contextPath}/weather/fetch-and-save?city=${encodeURIComponent(city)}&countryId=VN`,
        method: 'GET',
        success: function (response) {
            console.log(`Successfully fetched and saved data for ${displayCity}`);
            // After saving, fetch and display current weather
            fetchCurrentWeather(city, displayCity);
            // Fetch and display hourly forecast
            fetchHourlyForecast(city, displayCity);
            // Fetch and display daily forecast
            fetchDailyForecast(city, displayCity);
            // Fetch and display astronomy data
            fetchAstronomyData(city, displayCity);
            // Fetch and display weather stats
            fetchWeatherStats(city, displayCity);
        },
        error: function (xhr) {
            console.error(`Error fetching and saving data for ${city}: ${xhr.responseText}`);
            showErrorNotification(`Lỗi khi lấy dữ liệu cho ${displayCity}: ${xhr.responseText}`);
            // Display placeholders
            $('.current-temp').text('N/A');
            $('.feels-like').text('RealFeel® N/A');
            $('.weather-condition').text('Không có dữ liệu');
            $('.weather-metrics .metric-value').text('N/A');
            $('.info-card .info-value').text('N/A');
            updateWeatherIcon($('.weather-icon-main'), null, null, null);
        }
    });
}

// Fetch and display current weather
function fetchCurrentWeather(city, displayCity) {
    $.ajax({
        url: `${window.contextPath}/weather/current?city=${encodeURIComponent(city)}&countryId=VN`,
        method: 'GET',
        success: function (data) {
            if (data && data.weatherText) {
                const { icon, colorClass, animation } = getWeatherIcon(data.weatherText);
                $('.current-temp').text(`${data.temperature}°C`);
                $('.feels-like').text(`RealFeel® ${data.realFeelTemperature}°C`);
                $('.weather-condition').text(data.weatherText);
                updateWeatherIcon($('.weather-icon-main'), icon, colorClass, animation);
                $('.weather-metrics .metric-item').eq(0).find('.metric-value').text(`${data.windSpeed} km/h`);
                $('.weather-metrics .metric-item').eq(1).find('.metric-value').text(`${data.humidity}%`);
                $('.weather-metrics .metric-item').eq(2).find('.metric-value').text(`${data.visibility} km`);
                $('.weather-metrics .metric-item').eq(3).find('.metric-value').text(`${data.ceiling} m`);

                // Update extended weather information
                $('.info-card').eq(1).find('.info-item').eq(0).find('.info-value').text(`${data.uvIndex} (${getUvIndexText(data.uvIndex)})`);
                $('.info-card').eq(1).find('.info-item').eq(1).find('.info-value').text(`${data.windGustSpeed} km/h`);
                $('.info-card').eq(1).find('.info-item').eq(2).find('.info-value').text(`${data.precipitationProbability}%`);

                // Save the data
                saveWeatherData(data, displayCity);
            } else {
                showErrorNotification(`Không có dữ liệu thời tiết hiện tại cho ${displayCity}`);
            }
        },
        error: function (xhr) {
            console.error(`Error fetching current weather for ${city}: ${xhr.responseText}`);
            showErrorNotification(`Lỗi khi lấy thời tiết hiện tại cho ${displayCity}`);
        }
    });
}

// Fetch and display hourly forecast for the next 5 hours
function fetchHourlyForecast(city, displayCity) {
    const now = new Date();
    const startTime = new Date(now.getTime() - 0); // Current time
    const endTime = new Date(now.getTime() + 5 * 60 * 60 * 1000); // 5 hours from now
    $.ajax({
        url: `${window.contextPath}/weather/hourly-forecast?city=${encodeURIComponent(city)}&countryId=VN&startTime=${startTime.toISOString()}&endTime=${endTime.toISOString()}`,
        method: 'GET',
        success: function (data) {
            if (data && data.length > 0) {
                const $container = $('#hourly-forecast-container').empty();
                data.forEach(forecast => {
                    const time = new Date(forecast.forecastTime);
                    const hours = time.getHours().toString().padStart(2, '0');
                    const minutes = time.getMinutes().toString().padStart(2, '0');
                    const { icon, colorClass, animation } = getWeatherIcon(forecast.weatherText);
                    const $item = $(`
                        <div class="hourly-item">
                            <span class="hourly-time">${hours}:${minutes}</span>
                            <div class="hourly-icon-container"></div>
                            <span class="hourly-temp">${forecast.temperature}°</span>
                        </div>
                    `);
                    updateWeatherIcon($item.find('.hourly-icon-container'), icon, colorClass, animation);
                    $container.append($item);
                });
            } else {
                $('#hourly-forecast-container').html('<div class="text-center text-white">Không có dữ liệu dự báo theo giờ</div>');
            }
        },
        error: function (xhr) {
            console.error(`Error fetching hourly forecast for ${city}: ${xhr.responseText}`);
            showErrorNotification(`Lỗi khi lấy dự báo theo giờ cho ${displayCity}`);
        }
    });
}

// Fetch and display 5-day forecast
function fetchDailyForecast(city, displayCity) {
    const now = new Date();
    const startDate = now;
    const endDate = new Date(now.getTime() + 5 * 24 * 60 * 60 * 1000);
    $.ajax({
        url: `${window.contextPath}/weather/daily-forecast?city=${encodeURIComponent(city)}&countryId=VN&startDate=${startDate.toISOString()}&endDate=${endDate.toISOString()}`,
        method: 'GET',
        success: function (data) {
            if (data && data.length > 0) {
                const $container = $('#daily-forecast-container').empty();
                data.forEach((forecast, index) => {
                    const date = new Date(forecast.forecastDate);
                    const dayName = index === 0 ? 'Hôm nay' : date.toLocaleDateString('vi-VN', { weekday: 'long' });
                    const dateStr = date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' });
                    const { icon, colorClass, animation } = getWeatherIcon(forecast.dayWeatherText);
                    const $item = $(`
                        <div class="daily-item">
                            <div class="day-info">
                                <div class="day-date">
                                    <div class="day-name">${dayName}</div>
                                    <div class="date-value">${dateStr}</div>
                                </div>
                                <div class="day-icon-container"></div>
                                <div class="condition-info">
                                    <div class="condition-name">${forecast.dayWeatherText}</div>
                                    <div class="condition-detail">Khả năng mưa ${forecast.dayPrecipitationProbability}%</div>
                                </div>
                            </div>
                            <div class="temp-range">
                                <span class="temp-high">${forecast.maxTemperature}°</span>
                                <span class="temp-low">${forecast.minTemperature}°</span>
                            </div>
                            <div class="precipitation"></div>
                        </div>
                    `);
                    updateWeatherIcon($item.find('.day-icon-container'), icon, colorClass, animation);
                    $container.append($item);
                });
            } else {
                $('#daily-forecast-container').html('<div class="text-center text-white">Không có dữ liệu dự báo 5 ngày</div>');
            }
        },
        error: function (xhr) {
            console.error(`Error fetching daily forecast for ${city}: ${xhr.responseText}`);
            showErrorNotification(`Lỗi khi lấy dự báo 5 ngày cho ${displayCity}`);
        }
    });
}

// Fetch and display astronomy data
function fetchAstronomyData(city, displayCity) {
    const now = new Date();
    const startDate = now;
    const endDate = new Date(now.getTime() + 5 * 24 * 60 * 60 * 1000); // Extended to 5 days
    $.ajax({
        url: `${window.contextPath}/weather/astronomy?city=${encodeURIComponent(city)}&countryId=VN&startDate=${startDate.toISOString()}&endDate=${endDate.toISOString()}`,
        method: 'GET',
        success: function (data) {
            if (data && data.length > 0) {
                const todayData = data[0];
                const formatTime = (date) => date ? new Date(date).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }) : 'N/A';
                $('.info-card').eq(0).find('.info-item').eq(0).find('.info-value').text(formatTime(todayData.sunRise));
                $('.info-card').eq(0).find('.info-item').eq(1).find('.info-value').text(formatTime(todayData.sunSet));
                $('.info-card').eq(0).find('.info-item').eq(2).find('.info-value').text(formatTime(todayData.moonRise));
                $('.info-card').eq(0).find('.info-item').eq(3).find('.info-value').text(formatTime(todayData.moonSet));
            } else {
                $('.info-card').eq(0).find('.info-value').text('N/A');
            }
        },
        error: function (xhr) {
            console.error(`Error fetching astronomy data for ${city}: ${xhr.responseText}`);
            showErrorNotification(`Lỗi khi lấy dữ liệu thiên văn cho ${displayCity}`);
        }
    });
}

// Save weather data
function saveWeatherData(data, displayCity) {
    $.ajax({
        url: `${window.contextPath}/weather/save`,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function () {
            console.log(`Data saved for ${displayCity}`);
            showUpdateNotification(displayCity, 'Dữ liệu đã được lưu thành công');
        },
        error: function (xhr) {
            console.error(`Error saving data for ${displayCity}: ${xhr.responseText}`);
            showErrorNotification(`Lỗi khi lưu dữ liệu cho ${displayCity}`);
        }
    });
}

// Get UV index description
function getUvIndexText(index) {
    if (index <= 2) return 'Thấp';
    if (index <= 5) return 'Trung bình';
    if (index <= 7) return 'Cao';
    if (index <= 10) return 'Rất cao';
    return 'Cực kỳ cao';
}

// Notification queue management
let notificationCount = 0;
const maxNotifications = 5;
const baseDismissTime = 5000;
const notificationQueue = [];
let isProcessingQueue = false;

function queueNotification(callback) {
    notificationQueue.push(callback);
    if (!isProcessingQueue) {
        processNotificationQueue();
    }
}

function processNotificationQueue() {
    if (isProcessingQueue || notificationQueue.length === 0) return;
    isProcessingQueue = true;
    const callback = notificationQueue.shift();
    if (callback) {
        callback();
        setTimeout(() => {
            isProcessingQueue = false;
            processNotificationQueue();
        }, 1500);
    }
}

function showUpdateNotification(city, message) {
    if (notificationCount >= maxNotifications) {
        const oldest = document.querySelector('.notification');
        if (oldest) {
            clearTimeout(oldest.dataset.timeoutId);
            oldest.remove();
            notificationCount--;
        }
    }
    const indicator = document.createElement('div');
    indicator.className = 'notification position-fixed end-0 m-3 alert alert-info alert-dismissible fade show';
    indicator.style.bottom = `${notificationCount * 60 + 16}px`;
    indicator.setAttribute('data-index', notificationCount);
    indicator.innerHTML = `
        <i class="bx bx-info-circle me-2"></i>
        Cập nhật thời tiết ${city}: ${message} lúc ${new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(indicator);
    notificationCount++;
    const dismissTime = baseDismissTime + (maxNotifications - notificationCount) * 1000;
    const timeoutId = setTimeout(() => {
        indicator.remove();
        notificationCount--;
        updateNotificationPositions();
    }, dismissTime);
    indicator.dataset.timeoutId = timeoutId;
    indicator.querySelector('.btn-close').addEventListener('click', () => {
        clearTimeout(indicator.dataset.timeoutId);
        indicator.remove();
        notificationCount--;
        updateNotificationPositions();
    });
}

function showErrorNotification(message) {
    if (notificationCount >= maxNotifications) {
        const oldest = document.querySelector('.notification');
        if (oldest) {
            clearTimeout(oldest.dataset.timeoutId);
            oldest.remove();
            notificationCount--;
        }
    }
    const indicator = document.createElement('div');
    indicator.className = 'notification position-fixed end-0 m-3 alert alert-danger alert-dismissible fade show';
    indicator.style.bottom = `${notificationCount * 60 + 16}px`;
    indicator.setAttribute('data-index', notificationCount);
    indicator.innerHTML = `
        <i class="bx bx-error me-2"></i>
        ${message} lúc ${new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(indicator);
    notificationCount++;
    const dismissTime = baseDismissTime + (maxNotifications - notificationCount) * 1000;
    const timeoutId = setTimeout(() => {
        indicator.remove();
        notificationCount--;
        updateNotificationPositions();
    }, dismissTime);
    indicator.dataset.timeoutId = timeoutId;
    indicator.querySelector('.btn-close').addEventListener('click', () => {
        clearTimeout(indicator.dataset.timeoutId);
        indicator.remove();
        notificationCount--;
        updateNotificationPositions();
    });
}

function updateNotificationPositions() {
    const notifications = document.querySelectorAll('.notification');
    notifications.forEach((notif, index) => {
        notif.style.bottom = `${index * 60 + 16}px`;
        notif.setAttribute('data-index', index);
        clearTimeout(notif.dataset.timeoutId);
        const newDismissTime = baseDismissTime + (maxNotifications - index - 1) * 1000;
        const timeoutId = setTimeout(() => {
            notif.remove();
            notificationCount--;
            updateNotificationPositions();
        }, newDismissTime);
        notif.dataset.timeoutId = timeoutId;
    });
}

// WebSocket for real-time updates
const socket = new WebSocket((window.location.protocol === 'https:' ? 'wss://' : 'ws://') + window.location.host + window.contextPath + '/weather-updates');
socket.onmessage = function (event) {
    const data = JSON.parse(event.data);
    console.log('WebSocket data:', data);
    if (data.error) {
        showErrorNotification(data.error);
    } else {
        const displayCity = cityNameMap[data.city.name] || data.city.name;
        if ($('#city-name').text().includes(displayCity)) {
            const { icon, colorClass, animation } = getWeatherIcon(data.weatherText);
            $('.current-temp').text(`${data.temperature}°C`);
            $('.feels-like').text(`RealFeel® ${data.realFeelTemperature}°C`);
            $('.weather-condition').text(data.weatherText);
            updateWeatherIcon($('.weather-icon-main'), icon, colorClass, animation);
            $('.weather-metrics .metric-item').eq(0).find('.metric-value').text(`${data.windSpeed} km/h`);
            $('.weather-metrics .metric-item').eq(1).find('.metric-value').text(`${data.humidity}%`);
            $('.weather-metrics .metric-item').eq(2).find('.metric-value').text(`${data.visibility} km`);
            $('.weather-metrics .metric-item').eq(3).find('.metric-value').text(`${data.ceiling} m`);
            $('.info-card').eq(1).find('.info-item').eq(0).find('.info-value').text(`${data.uvIndex} (${getUvIndexText(data.uvIndex)})`);
            $('.info-card').eq(1).find('.info-item').eq(1).find('.info-value').text(`${data.windGustSpeed} km/h`);
            $('.info-card').eq(1).find('.info-item').eq(2).find('.info-value').text(`${data.precipitationProbability}%`);
            saveWeatherData(data, displayCity);
        }
    }
};
socket.onopen = function () {
    console.log('WebSocket connection opened');
    socket.send(JSON.stringify([city]));
};
socket.onclose = function (event) {
    console.log('WebSocket connection closed:', event);
    showErrorNotification('Kết nối WebSocket đã bị đóng');
    // Fallback: Fetch current weather
    fetchCurrentWeather(city, displayCity);
};
socket.onerror = function (error) {
    console.error('WebSocket error:', error);
    showErrorNotification('Kết nối WebSocket thất bại');
};

// Calculate weather statistics
function calculateWeatherStats(dailyData, astronomyData) {
    if (!dailyData || dailyData.length < 5 || !astronomyData || astronomyData.length < 5) {
        return { error: 'Không đủ dữ liệu dự báo 5 ngày' };
    }

    // Calculate average max and min temperature
    const avgMaxTemp = dailyData.reduce((sum, day) => sum + day.maxTemperature, 0) / 5;
    const avgMinTemp = dailyData.reduce((sum, day) => sum + day.minTemperature, 0) / 5;

    // Calculate sunshine hours per day
    const sunshineHours = astronomyData.map(day => {
        if (!day.sunRise || !day.sunSet) return 0;
        const sunRise = new Date(day.sunRise);
        const sunSet = new Date(day.sunSet);
        const hours = (sunSet - sunRise) / (1000 * 60 * 60); // Convert ms to hours
        return hours > 0 ? hours : 0;
    });

    // Calculate average sunshine hours
    const avgSunshineHours = sunshineHours.reduce((sum, hours) => sum + hours, 0) / 5;

    return {
        avgMaxTemp: avgMaxTemp.toFixed(1),
        avgMinTemp: avgMinTemp.toFixed(1),
        avgSunshineHours: avgSunshineHours.toFixed(1),
        dailyData,
        sunshineHours
    };
}

// Display weather statistics
function displayWeatherStats(stats, displayCity) {
    const $statsContainer = $('#weather-stats');
    if (!$statsContainer.length) return;

    if (stats.error) {
        $statsContainer.html(`<p class="weather-error">${stats.error}</p>`);
        showErrorNotification(`Lỗi khi hiển thị thống kê cho ${displayCity}: ${stats.error}`);
        return;
    }

    $statsContainer.html(`
        <div class="stats-item animate-stats">
            <i class="fas fa-thermometer-full"></i>
            <span>Nhiệt độ cao nhất TB: ${stats.avgMaxTemp}°C</span>
        </div>
        <div class="stats-item animate-stats">
            <i class="fas fa-thermometer-empty"></i>
            <span>Nhiệt độ thấp nhất TB: ${stats.avgMinTemp}°C</span>
        </div>
        <div class="stats-item animate-stats">
            <i class="fas fa-sun"></i>
            <span>Số giờ nắng TB: ${stats.avgSunshineHours} giờ</span>
        </div>
        <canvas id="weatherChart" width="400" height="200"></canvas>
    `);

    // Draw chart with Chart.js
    const ctx = document.getElementById('weatherChart').getContext('2d');
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['Ngày 1', 'Ngày 2', 'Ngày 3', 'Ngày 4', 'Ngày 5'],
            datasets: [
                {
                    label: 'Nhiệt độ cao nhất (°C)',
                    data: stats.dailyData.map(day => day.maxTemperature),
                    borderColor: '#ff4500',
                    fill: false
                },
                {
                    label: 'Nhiệt độ thấp nhất (°C)',
                    data: stats.dailyData.map(day => day.minTemperature),
                    borderColor: '#4682b4',
                    fill: false
                },
                {
                    label: 'Số giờ nắng (giờ)',
                    data: stats.sunshineHours.map(hours => hours.toFixed(1)),
                    borderColor: '#ffd700',
                    fill: false
                }
            ]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            },
            plugins: {
                legend: {
                    position: 'top'
                }
            }
        }
    });
}

// Fetch and display weather statistics
function fetchWeatherStats(city, displayCity) {
    const now = new Date();
    const startDate = now;
    const endDate = new Date(now.getTime() + 5 * 24 * 60 * 60 * 1000);

    $.ajax({
        url: `${window.contextPath}/weather/daily-forecast?city=${encodeURIComponent(city)}&countryId=VN&startDate=${startDate.toISOString()}&endDate=${endDate.toISOString()}`,
        method: 'GET',
        success: function (dailyData) {
            if (dailyData && dailyData.length > 0) {
                $.ajax({
                    url: `${window.contextPath}/weather/astronomy?city=${encodeURIComponent(city)}&countryId=VN&startDate=${startDate.toISOString()}&endDate=${endDate.toISOString()}`,
                    method: 'GET',
                    success: function (astronomyData) {
                        if (astronomyData && astronomyData.length > 0) {
                            const stats = calculateWeatherStats(dailyData, astronomyData);
                            displayWeatherStats(stats, displayCity);
                        } else {
                            $('#weather-stats').html('<div class="text-center text-white">Không có dữ liệu thiên văn</div>');
                            showErrorNotification(`Không có dữ liệu thiên văn cho ${displayCity}`);
                        }
                    },
                    error: function (xhr) {
                        console.error(`Error fetching astronomy data for stats: ${xhr.responseText}`);
                        showErrorNotification(`Lỗi khi lấy dữ liệu thiên văn cho thống kê ${displayCity}`);
                    }
                });
            } else {
                $('#weather-stats').html('<div class="text-center text-white">Không có dữ liệu dự báo 5 ngày</div>');
                showErrorNotification(`Không có dữ liệu dự báo 5 ngày cho ${displayCity}`);
            }
        },
        error: function (xhr) {
            console.error(`Error fetching daily forecast for stats: ${xhr.responseText}`);
            showErrorNotification(`Lỗi khi lấy dự báo 5 ngày cho thống kê ${displayCity}`);
        }
    });
}