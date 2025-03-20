package toy01.dto.response;

import lombok.Getter;
import toy01.entity.Board;
import toy01.entity.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
public class BoardResponseDto {
    private Long boardNo;
    private String title;
    private String content;
    private String category;
    private String nickname;
    private int views;
    private int likes;
    private List<String> tags;
    private LocalDateTime createdAt;

    public BoardResponseDto(Board board, User user) {
        this.boardNo = board.getBoardNo();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.category = board.getCategory();
        this.nickname = user.getNickname();
        this.views = board.getViews();
        this.likes = board.getLikes();
        this.createdAt = board.getCreatedAt();
        this.tags = board.getTags() != null ? Arrays.asList(board.getTags().split(",")) : List.of();
    }
}
