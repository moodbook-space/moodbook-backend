package org.com.moodbook.websocket.chatmessage.repository;

import java.util.List;
import org.com.moodbook.websocket.chatmessage.entity.ChatMessageDocument;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageMongoRepository extends MongoRepository<ChatMessageDocument, String> {
  List<ChatMessageDocument> findByRoomIdOrderByTimeAsc(Long roomId, Pageable pageable);
}
