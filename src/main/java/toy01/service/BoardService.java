package toy01.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toy01.dto.request.BoardRequestDto;
import toy01.entity.Board;
import toy01.repository.BoardRepository;
import toy01.repository.UserRepository;
import toy01.entity.User;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;  // 🔥 유저 정보 조회를 위해 추가

    // 이메일로 userId 가져오기
    public Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return user.getId();
    }


    // 게시글 작성 (userId 포함)
    public Long createBoard(BoardRequestDto requestDto, Long userId) {
        Board board = Board.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .category(requestDto.getCategory())
                .userId(userId)
                .views(0)
                .likes(0)
                .build();

        Board savedBoard = boardRepository.save(board);
        return savedBoard.getBoardNo();
    }
}
