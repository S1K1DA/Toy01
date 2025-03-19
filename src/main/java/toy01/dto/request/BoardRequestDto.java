package toy01.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import toy01.entity.Board;

@Getter
@NoArgsConstructor
public class BoardRequestDto {
    private String title;
    private String content;
    private String category;

    public Board toEntity(Long userId) {
        return Board.builder()
                .title(title)
                .content(content)
                .category(category)
                .userId(userId)
                .views(0)   // 기본 조회수 0
                .likes(0)   // 기본 좋아요 0
                .build();
    }
}
