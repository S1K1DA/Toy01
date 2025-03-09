package toy01.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
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
    private String profileImagePath;

    public void updateProfile(String nickname, String email, String profileImage, String profileImagePath) {
        this.nickname = nickname;
        this.email = email;
        this.profileImage = profileImage;
        this.profileImagePath = profileImagePath;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

}
