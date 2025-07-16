package org.com.moodbook.websocket.handler;

import java.security.Principal;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

  /** Websocket에서 사용자의 식별자를 추출하기 위한 핸드쉐이크 핸들러 클래스 **/

  @Override
  protected Principal determineUser(ServerHttpRequest request,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes) {

    // 쿼리에서 memberId, memberName 모두 추출
    String query = request.getURI().getQuery();
    String memberName = getQueryParam(query, "memberName");
    String memberId = getQueryParam(query, "memberId");

    // 예시: "1|홍길동"
    String principalName = memberId + "|" + memberName;

    return new StompPrincipal(principalName);
  }

  private String getQueryParam(String query, String key) {
    if (query == null || !query.contains(key + "=")) {
      return "";
    }
    for (String param : query.split("&")) {
      if (param.startsWith(key + "=")) {
        return param.substring(key.length() + 1);
      }
    }
    return "";
  }


}
