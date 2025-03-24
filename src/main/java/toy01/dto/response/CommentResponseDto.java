package toy01.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import toy01.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponseDto {
    private Long commentNo;
    private String nickname;
    private String comment;
    private Boolean isSecret;
    private String email;
    private LocalDateTime createdAt;
    private List<CommentResponseDto> replies;
    private Long parentId;

    public static CommentResponseDto fromEntity(Comment comment) {
        return CommentResponseDto.builder()
                .commentNo(comment.getCommentNo())
                .nickname(comment.getUser().getNickname())
                .comment(comment.getComment())
                .isSecret(comment.getIsSecret())
                .createdAt(comment.getCreatedAt())
                .email(comment.getUser().getEmail())
                .parentId(comment.getParent() != null ? comment.getParent().getCommentNo() : null)
                .replies(comment.getReplies() != null
                        ? comment.getReplies().stream().map(CommentResponseDto::fromEntity).collect(Collectors.toList())
                        : List.of())
                .build();
    }
}
