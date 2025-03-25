package toy01.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole = UserRole.NORMAL;

    public void updateProfile(String nickname, String email,String name, String profileImage) {
        this.nickname = nickname;
        this.email = email;
        this.name = name;
        this.profileImage = profileImage;
    }

    public enum UserRole {
        NORMAL,   // 일반
        PREMIUM,  // 1개월권
        FREE      // 무제한권
    }

}
