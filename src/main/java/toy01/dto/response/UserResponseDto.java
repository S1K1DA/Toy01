package toy01.dto.response;

import lombok.Getter;
import toy01.entity.User;
import toy01.entity.User.UserRole;


@Getter
public class UserResponseDto {
    private String email;
    private String name;
    private String nickname;
    private String profileImagePath;
    private UserRole userRole;

    // Entity → DTO 변환
    public UserResponseDto(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.userRole = user.getUserRole();


        // 정적 파일 URL 생성 (profileImagePath를 URL로 변환)
        this.profileImagePath = user.getProfileImage() != null
                ? "/uploads/" + user.getProfileImage()
                : "default-profile.jpg";
    }
}
