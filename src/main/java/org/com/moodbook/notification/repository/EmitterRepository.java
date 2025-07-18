package org.com.moodbook.notification.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    public void saveEventCache(String emitterId, Object event) {
        eventCache.put(emitterId, event);
    }

    public Map<String, SseEmitter> findAllEmitterStartWithByUsername(String username) {
        return emitters.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(username))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Object> findAllEventCacheStartWithByMemberId(String userId) {
        return emitters.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(userId))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void deleteById(String emitterId) {
        emitters.remove(emitterId);
    }

}
