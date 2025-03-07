package toy01.dto;

import lombok.Getter;
import toy01.entity.User;

@Getter
public class UserResponseDto {
    private String email;
    private String name;
    private String nickname;

    // Entity → DTO 변환
    public UserResponseDto(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.nickname = user.getNickname();
    }
}
