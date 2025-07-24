package org.com.moodbook.websocket.chatmessage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.websocket.chatmessage.dto.ChatMessageDocumentResponse;
import org.com.moodbook.websocket.chatmessage.entity.ChatMessageDocument;
import org.com.moodbook.websocket.chatmessage.repository.ChatMessageMongoRepository;
import org.com.moodbook.websocket.chatmessage.service.ChatMessageService;
import org.com.moodbook.websocket.dto.ChatMessage;
import org.com.moodbook.websocket.chatmessage.redis.ChatMessageRedisRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class ChatMessageController {
  private final SimpMessagingTemplate messagingTemplate;
  private final ChatMessageService chatMessageService;
  private final ChatMessageMongoRepository mongoRepository;
  private final ChatMessageRedisRepository redisRepository;

  @Operation(
    summary = "채팅 메시지 전송",
    description = "채팅방에 실시간 메시지를 브로드캐스트하고, Redis에 임시 저장합니다."
  )
  @MessageMapping("/chat.sendMessage")
  public void sendMessage(ChatMessage message) {
    // 1. 실시간 브로드캐스트
    String destination = "/topic/room." + message.getRoomId();
    messagingTemplate.convertAndSend(destination, message);

    // 2. Redis 임시 저장
    chatMessageService.saveMessage(message.getRoomId(), message);
  }

  @Operation(
      summary = "채팅방의 최근 메시지 조회 (Redis)",
      description = "Redis에서 채팅방의 최근 N개 메시지를 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "최근 메시지 조회 성공",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatMessage.class))),
      @ApiResponse(responseCode = "404", description = "해당 방 없음")
  })
  @GetMapping("/recent/{roomId}")
  public List<ChatMessage> getRecentFromRedis(@PathVariable Long roomId, @RequestParam(defaultValue = "30") int count) {
    return chatMessageService.getRecentMessagesFromRedis(roomId, (long) count);
  }

  // MongoDB 기준(영구 저장소에서 페이징)
  @Operation(
      summary = "채팅방 전체 메시지 페이징 조회 (MongoDB + Redis)",
      description = """
      page=0이면 Redis의 최신 메시지, page>=1이면 MongoDB에서 페이징 조회.<br>
      과거→최신 순서로 반환.<br>
      - page: 0(기본값, Redis), 1이상(MongoDB)
      - size: 한 페이지당 메시지 수(기본값 30)
      """
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "채팅 내역 조회 성공",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatMessageDocumentResponse.class))),
      @ApiResponse(responseCode = "404", description = "해당 방 없음")
  })
  @GetMapping("/api/chat/{roomId}/history")
  public List<ChatMessageDocumentResponse> getChatHistory(
      @PathVariable Long roomId,
      @RequestParam(defaultValue = "0") int page,     // 0이면 Redis, 1부터 MongoDB
      @RequestParam(defaultValue = "30") int size
  ) {
    if (page == 0) {
      // Redis에서 최신 size개
      Long total = redisRepository.countMessagesInRoom(roomId);
      // -size ~ -1 (리스트 끝부터 size개)
      long start = Math.max(0, (total != null ? total : 0) - size);
      long end = (total != null ? total : 0) - 1;
      List<ChatMessage> redisMsgs = redisRepository.getMessagesInRoom(roomId, start, end);
      // 시간 오름차순(과거→최신) 정렬
      redisMsgs.sort(Comparator.comparing(ChatMessage::getTime));
      // 엔티티→DTO 변환
      return redisMsgs.stream().map(ChatMessageDocumentResponse::from).collect(Collectors.toList());
    } else {
      // MongoDB에서 page-1 페이지 가져오기(최신부터 내림차순 → 뒤집어서 오름차순 반환)
      PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("time").descending());
      List<ChatMessageDocument> mongoMsgs = mongoRepository.findByRoomIdOrderByTimeAsc(roomId, pageRequest);
      // 시간 오름차순으로 바꿔주기
      mongoMsgs.sort(Comparator.comparing(ChatMessageDocument::getTime));
      return mongoMsgs.stream().map(ChatMessageDocumentResponse::from).collect(Collectors.toList());
    }
  }
}
