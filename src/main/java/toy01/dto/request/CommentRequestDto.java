package toy01.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {
    private Long boardNo;
    private String comment;
    private Boolean isSecret;
    private Long parentId; // 대댓글인 경우 부모 댓글 ID
}
