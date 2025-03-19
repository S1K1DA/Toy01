package toy01.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import toy01.entity.Board;

import java.util.List;

@Getter
@NoArgsConstructor
public class BoardRequestDto {
    private String title;
    private String content;
    private String category;
    private List<String> tags;

    public Board toEntity(Long userId) {
        return Board.builder()
                .title(title)
                .content(content)
                .category(category)
                .userId(userId)
                .tags(String.join(",",tags))
                .views(0)
                .likes(0)
                .build();
    }
}
