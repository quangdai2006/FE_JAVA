package myproject.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import myproject.model.CurrentWeather;
import myproject.service.WeatherService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class WeatherWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LogManager.getLogger(WeatherWebSocketHandler.class);

    @Autowired
    private WeatherService weatherService;

    private final ObjectMapper mapper = new ObjectMapper();

    private final ConcurrentHashMap<WebSocketSession, Set<String>> sessionCities = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("WebSocket connection established: {}", session.getId());
        sessionCities.put(session, new CopyOnWriteArraySet<>());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            List<String> cities = mapper.readValue(message.getPayload(), new TypeReference<List<String>>() {});
            logger.debug("Received cities for update: {}", cities);

            Set<String> citySet = sessionCities.get(session);
            citySet.clear();
            citySet.addAll(cities);

            for (String city : cities) {
                sendWeatherData(session, city);
            }
        } catch (Exception e) {
            logger.error("Error processing WebSocket message: {}", e.getMessage(), e);
            session.sendMessage(new TextMessage("{\"error\":\"Server error processing request\"}"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("WebSocket connection closed: {}", session.getId());
        sessionCities.remove(session);
    }

    /**
     * Gửi dữ liệu thời tiết hiện tại cho một thành phố qua WebSocket.
     */
    private void sendWeatherData(WebSocketSession session, String city) throws IOException {
        try {
            // Mặc định countryId là "VN" như trong WeatherController
            CurrentWeather data = weatherService.fetchCurrentWeather(city, "VN");
            if (data == null) {
                logger.warn("No weather data available for city: {}", city);
                session.sendMessage(new TextMessage("{\"error\":\"No weather data available for " + city + "\"}"));
                return;
            }
            String jsonResponse = mapper.writeValueAsString(data);
            session.sendMessage(new TextMessage(jsonResponse));
            logger.info("Sent current weather data for {} to client", city);
        } catch (IllegalArgumentException e) {
            logger.warn("City not found: {}", city);
            session.sendMessage(new TextMessage("{\"error\":\"City not found: " + city + "\"}"));
        } catch (IOException e) {
            logger.error("Error fetching data for {}: {}", city, e.getMessage(), e);
            session.sendMessage(new TextMessage("{\"error\":\"Error fetching data for " + city + ": " + e.getMessage() + "\"}"));
        }
    }
}