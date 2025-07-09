package org.com.moodbook.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "User_Profile")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

}
