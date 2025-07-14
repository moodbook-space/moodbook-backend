package org.com.moodbook.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.moodbook.common.constants.Gender;

@Entity
@Table(name = "Member_Profile")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfile {

  @Id
  private Long id;

  @OneToOne
  @MapsId
  @JoinColumn(name = "id")
  private Member member;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false)
  // Default Value로 설정된 링크는 S3버킷 내의 "기본 프로필 이미지"가 저장된 링크입니다.
  // 모든 회원은 프로필사진이 없는 상태로 회원가입되기에, "기본 프로필 이미지"의 링크를 Default Value로 적용하였습니다.
  private String myImage = "https://moodbook-bucket.s3.ap-northeast-2.amazonaws.com/131a3a40-191c-4a7a-83ee-af254efaf0fb.png";

  @Column(length = 100)
  private String nickname;
}
