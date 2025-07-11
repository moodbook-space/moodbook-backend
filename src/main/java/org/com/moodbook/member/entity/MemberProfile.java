package org.com.moodbook.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.moodbook.common.constants.Gender;

@Entity
@Table(name = "User_Profile")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfile {


  @Id
  private Long id;


  @OneToOne
  @MapsId
  @JoinColumn(name = "id")
  private Member member;

  private Gender gender;

  private String address;

  private String myImage;

  @Column(length = 100)
  private String nickname;

}
