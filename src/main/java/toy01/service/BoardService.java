package toy01.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy01.dto.request.BoardRequestDto;
import toy01.dto.response.BoardResponseDto;
import toy01.entity.Board;
import toy01.repository.BoardRepository;
import toy01.repository.UserRepository;
import toy01.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Transactional
    public Long createBoard(BoardRequestDto requestDto, Long userId) {
        Board board = Board.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .category(requestDto.getCategory())
                .userId(userId)
                .tags(requestDto.getTags() != null ? String.join(",", requestDto.getTags()) : "")
                .views(0)
                .likes(0)
                .build();

        Board savedBoard = boardRepository.save(board);
        return savedBoard.getBoardNo();
    }

    // 게시판 리스트
    public List<BoardResponseDto> getBoardList(String category, String search, String tag, int page) {
        int pageSize = 8;
        int offset = (page - 1) * pageSize;

        List<Board> boardList = boardRepository.findAll().stream()
                .filter(board -> category == null || board.getCategory().equals(category))
                .filter(board -> search == null || board.getTitle().contains(search))
                .filter(board -> tag == null || (board.getTags() != null && board.getTags().contains(tag)))
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt())) // 최신순 정렬
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());

        return boardList.stream()
                .map(board -> {
                    User user = userRepository.findById(board.getUserId())
                            .orElseThrow(() -> new RuntimeException("작성자 정보를 찾을 수 없습니다."));
                    return new BoardResponseDto(board, user);
                })
                .collect(Collectors.toList());
    }

    // 게시글 상세보기
    public BoardResponseDto getBoardDetail(Long boardNo) {
        Board board = boardRepository.findBoardWithUser(boardNo)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findById(board.getUserId())
                .orElseThrow(() -> new RuntimeException("작성자를 찾을 수 없습니다."));

        board.setViews(board.getViews() + 1);
        boardRepository.save(board);

        return new BoardResponseDto(board, user);
    }

    // 게시글 삭제
    @Transactional
    public void deleteBoard(Long boardNo, String email) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        // 게시글 작성자의 이메일 가져오기
        User user = userRepository.findById(board.getUserId())
                .orElseThrow(() -> new RuntimeException("작성자를 찾을 수 없습니다."));

        // 현재 로그인한 유저의 이메일과 비교
        if (!user.getEmail().equals(email)) {
            throw new RuntimeException("게시글을 삭제할 권한이 없습니다.");
        }

        boardRepository.delete(board);
    }

    // 게시글 수정
    @Transactional
    public boolean updateBoard(Long boardNo, BoardRequestDto requestDto, String email) {
        Optional<Board> boardOptional = boardRepository.findById(boardNo);
        if (boardOptional.isEmpty()) {
            return false;
        }

        Board board = boardOptional.get();

        // 게시글 작성자와 로그인된 사용자의 이메일 비교
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty() || !userOptional.get().getId().equals(board.getUserId())) {
            return false;
        }

        String tags = (requestDto.getTags() != null) ? String.join(",", requestDto.getTags()) : "";

        // 게시글 수정 적용
        board.setTitle(requestDto.getTitle());
        board.setContent(requestDto.getContent());
        board.setTags(tags);

        return true;
    }

}
