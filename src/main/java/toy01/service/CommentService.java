package toy01.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy01.dto.request.CommentRequestDto;
import toy01.dto.response.CommentResponseDto;
import toy01.entity.Board;
import toy01.entity.Comment;
import toy01.entity.User;
import toy01.repository.BoardRepository;
import toy01.repository.CommentRepository;
import toy01.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void saveComment(CommentRequestDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        Board board = boardRepository.findById(dto.getBoardNo())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Comment parent = null;
        if (dto.getParentId() != null) {
            parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("부모 댓글이 존재하지 않습니다."));
        }

        Comment comment = Comment.builder()
                .comment(dto.getComment())
                .isSecret(dto.getIsSecret())
                .user(user)
                .board(board)
                .parent(parent)
                .build();

        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long boardNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        return commentRepository.findByBoardAndParentIsNullOrderByCreatedAtAsc(board)
                .stream()
                .map(CommentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
