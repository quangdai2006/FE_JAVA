
document.addEventListener('DOMContentLoaded', function () {
    initializeHomePage();
    initializeWeatherData();

    // Click to weather item - Chuyển trang khi click vào bất kỳ thành phố nào
    $(document).on('click', '.weather-item', function () {
        const city = $(this).data('city');
        const cityKey = Object.keys(cityNameMap).find(key => cityNameMap[key] === city) || city;
        window.location.href = `${window.contextPath}/weather/static-weather-details?city=${encodeURIComponent(cityKey)}`;
    });

    // Xử lý click vào "View more"
    const viewMoreLink = document.querySelector('.view-more-link');
    if (viewMoreLink) {
        viewMoreLink.addEventListener('click', function (e) {
            e.preventDefault();
            console.log('View more clicked');
        });
    }

    // Service card modals
    const serviceCards = document.querySelectorAll('.service-card .btn');
    serviceCards.forEach(btn => {
        btn.addEventListener('click', function () {
            const service = this.closest('.service-card').querySelector('.card-title').textContent;
            showServiceModal(service);
        });
    });
});

// Hàm ánh xạ WeatherText sang Font Awesome icon, lớp màu và animation
function getWeatherIcon(weatherText) {
    const text = weatherText ? weatherText.toLowerCase() : '';
    if (!text) return { icon: '', colorClass: 'weather-error', animation: '' };

    if (text === 'sunny' || text === 'clear') {
        return { icon: 'fa-sun', colorClass: 'weather-sunny-bright', animation: 'float-shine' };
    }
    if (text === 'mostly sunny' || text === 'partly sunny' || text === 'clouds and sun' || text === 'mostly clear') {
        return { icon: 'fa-cloud-sun', colorClass: 'weather-partly-sunny', animation: 'float-shine' };
    }
    if (text === 'mostly cloudy' || text === 'partly cloudy') {
        return { icon: 'fa-cloud', colorClass: 'weather-mostly-cloudy', animation: 'float-fade' };
    }
    if (text === 'cloudy') {
        return { icon: 'fa-cloud', colorClass: 'weather-cloudy', animation: 'float-sway' };
    }
    if (text.includes('rain') || text.includes('light rain')) {
        return { icon: 'fa-cloud-showers-heavy', colorClass: 'weather-rainy', animation: 'float-rain' };
    }
    return { icon: 'fa-exclamation-circle', colorClass: 'weather-error', animation: '' };
}

// Hàm cập nhật biểu tượng thời tiết trong DOM
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

// Hàm hiển thị biểu tượng lỗi
function showErrorIcon($item) {
    const $iconContainer = $item.find('.weather-icon');
    $iconContainer.empty();
    $iconContainer.append('<i class="fas fa-exclamation-circle weather-error"></i>');
}

// Hàm khôi phục placeholder nếu không có dữ liệu
function restorePlaceholder($item) {
    const $iconContainer = $item.find('.weather-icon');
    $iconContainer.empty();
    $iconContainer.append('<div class="icon-placeholder"></div>');
}

// Khởi tạo các chức năng trang chủ
function initializeHomePage() {
    animateCounters();
    initializeMapControls();
    initializeWeatherAnimations();

    // Tooltip
    if (typeof bootstrap !== 'undefined') {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (el) {
            return new bootstrap.Tooltip(el);
        });
    }
}

// Hàm animate counters
function animateCounters() {
    const counters = document.querySelectorAll('[data-target]');
    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                animateCounter(entry.target);
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.5, rootMargin: '0px 0px -100px 0px' });

    counters.forEach(counter => observer.observe(counter));
}

function animateCounter(element) {
    const target = parseInt(element.getAttribute('data-target'));
    const duration = 2000;
    const increment = target / (duration / 16);
    let current = 0;
    const timer = setInterval(() => {
        current += increment;
        if (current >= target) {
            current = target;
            clearInterval(timer);
        }
        element.textContent = Math.floor(current);
    }, 16);
}

// Hàm initialize map controls
function initializeMapControls() {
    const mapControls = document.querySelectorAll('input[name="mapType"]');
    const mapPlaceholder = document.querySelector('.map-placeholder .map-content h4');
    const loadingSpinner = document.querySelector('.loading-spinner');

    mapControls.forEach(control => {
        control.addEventListener('change', function () {
            if (this.checked) {
                loadingSpinner.style.display = 'block';
                setTimeout(() => {
                    loadingSpinner.style.display = 'none';
                    const titles = {
                        'current': 'Bản đồ Radar Thời tiết Việt Nam - Hiện tại',
                        'forecast': 'Bản đồ Radar Thời tiết Việt Nam - Dự báo',
                        'satellite': 'Bản đồ Vệ tinh Thời tiết Việt Nam'
                    };
                    mapPlaceholder.textContent = titles[this.id];
                    console.log(`Loading ${this.id} map view`);
                }, 1500);
            }
        });
    });
}

// Hàm initialize weather data
function initializeWeatherData() {
    // Tạo động danh sách các thành phố
    function populateWeatherItems() {
        const column1 = document.getElementById('weather-column-1');
        const column2 = document.getElementById('weather-column-2');
        column1.innerHTML = '';
        column2.innerHTML = '';

        // Chia danh sách thành phố thành hai cột
        const column1Cities = cities.slice(0, 6);
        const column2Cities = cities.slice(6);

        // Thêm các mục thời tiết cho cột 1
        column1Cities.forEach(city => {
            const displayCity = cityNameMap[city] || city;
            const $newItem = $(`
                <div class="weather-item" data-city="${displayCity}">
                    <div class="city-info">
                        <span class="city-name">${displayCity}</span>
                    </div>
                    <div class="weather-icon">
                        <span class="icon-placeholder"></span>
                    </div>
                    <div class="temperature">
                        <span class="temp-value">Loading...</span>
                    </div>
                </div>
            `);
            $(column1).append($newItem);
            console.log(`Added weather item for ${displayCity} to column 1`);
        });

        // Thêm các mục thời tiết cho cột 2
        column2Cities.forEach(city => {
            const displayCity = cityNameMap[city] || city;
            const $newItem = $(`
                <div class="weather-item" data-city="${displayCity}">
                    <div class="city-info">
                        <span class="city-name">${displayCity}</span>
                    </div>
                    <div class="weather-icon">
                        <span class="icon-placeholder"></span>
                    </div>
                    <div class="temperature">
                        <span class="temp-value">Loading...</span>
                    </div>
                </div>
            `);
            $(column2).append($newItem);
            console.log(`Added weather item for ${displayCity} to column 2`);
        });
    }

    populateWeatherItems();

    // Chu trình cho mỗi thành phố khi reload trang
    cities.forEach(city => {
        const displayCity = cityNameMap[city] || city;
        let $item = $(`.weather-item[data-city="${displayCity}"]`);
        $.ajax({
            url: window.contextPath + '/weather/fetch-and-save-current?city=' + encodeURIComponent(city),
            method: 'GET',
            success: function (weatherData) {
                if ($item.length && weatherData && weatherData.weatherText) {
                    const { icon, colorClass, animation } = getWeatherIcon(weatherData.weatherText);
                    $item.find('.temp-value').text(weatherData.temperature !== undefined ? weatherData.temperature + '°C' : 'N/A');
                    $item.find('.real-feel').text(weatherData.realFeelTemperature !== undefined ? `RealFeel ${weatherData.realFeelTemperature}°C` : 'RealFeel N/A');
                    updateWeatherIcon($item, icon, colorClass, animation);

                    // Lưu dữ liệu mới
                    $.ajax({
                        url: window.contextPath + '/weather/save',
                        method: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(weatherData),
                        success: function () {
                            console.log(`Data saved for ${displayCity}`);
                            queueNotification(() => showUpdateNotification(displayCity, 'Dữ liệu đã được lưu thành công'));
                        },
                        error: function (xhr) {
                            console.error(`Error saving data for ${displayCity}: ${xhr.responseJSON ? xhr.responseJSON.error : 'Unknown error'}`);
                            queueNotification(() => showErrorNotification(`Lỗi khi lưu dữ liệu cho ${displayCity}: ${xhr.responseJSON ? xhr.responseJSON.error : 'Unknown error'}`));
                        }
                    });
                } else {
                    showErrorIcon($item);
                    queueNotification(() => showErrorNotification(`Không có dữ liệu thời tiết cho ${displayCity}`));
                }
            },
            error: function (xhr) {
                console.error(`Error fetching and saving data for ${city}: ${xhr.responseJSON ? xhr.responseJSON.error : 'Unknown error'}`);
                if ($item.length) restorePlaceholder($item);
                queueNotification(() => showErrorNotification(`Lỗi khi fetch dữ liệu cho ${displayCity}: ${xhr.responseJSON ? xhr.responseJSON.error : 'Unknown error'}`));
            }
        });
    });
}

// WebSocket: Kết nối để nhận cập nhật thời gian thực
const socket = new WebSocket((window.location.protocol === 'https:' ? 'wss://' : 'ws://') + window.location.host + window.contextPath + '/weather-updates');
socket.onmessage = function (event) {
    const data = JSON.parse(event.data);
    console.log('WebSocket data:', data);
    if (data.error) {
        console.error('WebSocket error:', data.error);
        queueNotification(() => showErrorNotification(data.error));
    } else {
        const displayCity = cityNameMap[data.city.name] || data.city.name;
        let $item = $(`.weather-item[data-city="${displayCity}"]`);
        if ($item.length && data.weatherText) {
            const { icon, colorClass, animation } = getWeatherIcon(data.weatherText);
            $item.find('.temp-value').text(data.temperature !== undefined ? data.temperature + '°C' : 'N/A');
            $item.find('.real-feel').text(data.realFeelTemperature !== undefined ? `RealFeel ${data.realFeelTemperature}°C` : 'RealFeel N/A');
            updateWeatherIcon($item, icon, colorClass, animation);

            // Lưu dữ liệu mới từ WebSocket
            $.ajax({
                url: window.contextPath + '/weather/save',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function () {
                    console.log(`Saved WebSocket update for ${displayCity}`);
                    queueNotification(() => showUpdateNotification(displayCity, data.weatherText));
                },
                error: function (xhr) {
                    console.error(`Error saving WebSocket data for ${displayCity}: ${xhr.responseJSON ? xhr.responseJSON.error : 'Unknown error'}`);
                    queueNotification(() => showErrorNotification(`Lỗi khi lưu dữ liệu WebSocket cho ${displayCity}: ${xhr.responseJSON ? xhr.responseJSON.error : 'Unknown error'}`));
                }
            });
        }
    }
};
socket.onopen = function () {
    console.log('WebSocket connection opened');
    socket.send(JSON.stringify(cities));
};
socket.onclose = function (event) {
    console.log('WebSocket connection closed:', event);
    queueNotification(() => showErrorNotification('Kết nối WebSocket đã bị đóng'));
    // Fallback: Fetch current data for all cities
    cities.forEach(city => {
        $.ajax({
            url: window.contextPath + '/weather/fetch-and-save-current?city=' + encodeURIComponent(city),
            method: 'GET',
            error: function (xhr) {
                console.error(`Fallback failed for ${city}: ${xhr.responseJSON ? xhr.responseJSON.error : 'Unknown error'}`);
            }
        });
    });
};
socket.onerror = function (error) {
    console.error('WebSocket error:', error);
    queueNotification(() => showErrorNotification('Kết nối WebSocket thất bại'));
};

// Quản lý hàng đợi thông báo
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

function showUpdateNotification(city, weatherText) {
    if (notificationCount >= maxNotifications) {
        const oldest = document.querySelector('.notification');
        if (oldest) {
            clearTimeout(oldest.dataset.timeoutId);
            oldest.remove();
            notificationCount--;
        }
    }

    const { colorClass } = getWeatherIcon(weatherText || '');
    const indicator = document.createElement('div');
    indicator.className = `notification position-fixed end-0 m-3 alert alert-info alert-dismissible fade show ${colorClass}`;
    indicator.style.bottom = `${notificationCount * 60 + 16}px`;
    indicator.setAttribute('data-index', notificationCount);
    indicator.innerHTML = `
        <i class="bx bx-info-circle me-2"></i>
        Cập nhật thời tiết ${city} lúc ${new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(indicator);
    notificationCount++;
    console.log('Displayed update notification for', city);

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
    indicator.className = 'notification position-fixed end-0 m-3 alert alert-danger alert-dismissible fade show weather-error';
    indicator.style.bottom = `${notificationCount * 60 + 16}px`;
    indicator.setAttribute('data-index', notificationCount);
    indicator.innerHTML = `
        <i class="bx bx-error me-2"></i>
        ${message} lúc ${new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(indicator);
    notificationCount++;
    console.log('Displayed error notification:', message);

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

// Khởi tạo các animation thời tiết
function initializeWeatherAnimations() {
    const observer = new MutationObserver((mutations) => {
        mutations.forEach(mutation => {
            if (mutation.addedNodes.length) {
                mutation.addedNodes.forEach(node => {
                    if (node.nodeType === 1 && node.matches('.weather-icon i')) {
                        const animation = node.classList.contains('float-shine') ? 'float-shine' :
                            node.classList.contains('float-rain') ? 'float-rain' :
                                node.classList.contains('float-fade') ? 'float-fade' :
                                    node.classList.contains('float-sway') ? 'float-sway' : 'float';
                        node.style.animation = `${animation} 3s ease-in-out infinite`;
                    }
                });
            }
        });
    });
    document.querySelectorAll('.weather-icon').forEach(container => {
        observer.observe(container, { childList: true });
    });
}

function showServiceModal(serviceName) {
    const modal = document.createElement('div');
    modal.className = 'modal fade';
    modal.innerHTML = `
        <div class="modal-dialog modal-lg">
            <div class="modal-content" style="background: var(--glass-bg); backdrop-filter: blur(15px); border: 1px solid var(--glass-border);">
                <div class="modal-header border-0">
                    <h5 class="modal-title text-white">${serviceName}</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p class="text-white">Thông tin chi tiết về dịch vụ ${serviceName} sẽ được cập nhật sớm.</p>
                    <div class="text-center">
                        <i class="bx bx-construction display-1 text-warning"></i>
                        <p class="text-muted mt-3">Tính năng đang được phát triển</p>
                    </div>
                </div>
                <div class="modal-footer border-0">
                    <button type="button" class="btn btn-outline-warning" data-bs-dismiss="modal">Đóng</button>
                </div>
            </div>
        </div>
    `;
    document.body.appendChild(modal);
    const bsModal = new bootstrap.Modal(modal);
    bsModal.show();
    modal.addEventListener('hidden.bs.modal', () => modal.remove());
}