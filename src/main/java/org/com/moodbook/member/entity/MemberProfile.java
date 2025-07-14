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

  @Column
  private String myImage;

  @Column(length = 100)
  private String nickname;
}
