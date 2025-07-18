package org.com.moodbook.common.constants;

import lombok.Getter;

@Getter
public enum NotifyType {

    // 가입 신청
    CHAT_APPLY("CHAT_APPLY"),

    // 가입 거절
    CHAT_REJECTED("CHAT_REJECTED"),

    // 가입 승인,
    CHAT_APPROVAL("CHAT_APPROVAL");

    final String notifyType;

    NotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

}
