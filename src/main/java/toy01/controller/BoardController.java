package toy01.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toy01.dto.request.BoardRequestDto;
import toy01.dto.response.BoardResponseDto;
import toy01.service.BoardService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    // 게시글 작성 API (세션 기반 사용자 인증 추가)
    @PostMapping("/create")
    public ResponseEntity<?> createBoard(@RequestBody BoardRequestDto requestDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("email") == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 세션에서 이메일 가져오기
        String email = (String) session.getAttribute("email");
        Long userId = boardService.getUserIdByEmail(email);

        if (userId == null) {
            return ResponseEntity.status(400).body("유저 정보가 올바르지 않습니다.");
        }

        // userId 설정 후 저장
        Long boardNo = boardService.createBoard(requestDto, userId);
        return ResponseEntity.ok(boardNo);
    }

    // 게시글 조회(리스트)
    @GetMapping("/list")
    public ResponseEntity<List<BoardResponseDto>> getBoardList(
            @RequestParam(required = false, name = "category") String category,
            @RequestParam(required = false, name = "search") String search,
            @RequestParam(required = false, name = "tag") String tag,
            @RequestParam(defaultValue = "1", name = "page") int page
    ) {
        List<BoardResponseDto> boardList = boardService.getBoardList(category, search, tag, page);
        return ResponseEntity.ok(boardList);
    }

    // 게시글 상세
    @GetMapping("/detail/{boardNo}")
    public ResponseEntity<BoardResponseDto> getBoardDetail(@PathVariable("boardNo") Long boardNo) {
        BoardResponseDto boardDetail = boardService.getBoardDetail(boardNo);
        return ResponseEntity.ok(boardDetail);
    }

    // 게시글 삭제
    @DeleteMapping("/delete/{boardNo}")
    public ResponseEntity<String> deleteBoard(@PathVariable("boardNo") Long boardNo, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("email") == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 세션에서 이메일 가져오기
        String email = (String) session.getAttribute("email");

        try {
            boardService.deleteBoard(boardNo, email);
            return ResponseEntity.ok("게시글이 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // 권한이 없는 경우 403 응답
        }
    }

    @PutMapping("/edit/{boardNo}")
    public ResponseEntity<?> updateBoard(
            @PathVariable("boardNo") Long boardNo,
            @RequestBody BoardRequestDto requestDto,
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        String email = (String) session.getAttribute("email");

        boolean isUpdated = boardService.updateBoard(boardNo, requestDto, email);
        if (isUpdated) {
            return ResponseEntity.ok("게시글이 수정되었습니다.");
        } else {
            return ResponseEntity.status(403).body("게시글 수정 권한이 없습니다.");
        }
    }

    // 게시글 좋아요 토글
    @PostMapping("/like/{boardNo}")
    public ResponseEntity<?> toggleLike(@PathVariable("boardNo") Long boardNo, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("email") == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 세션에서 이메일 가져오기
        String email = (String) session.getAttribute("email");
        Long userId = boardService.getUserIdByEmail(email);

        if (userId == null) {
            return ResponseEntity.status(400).body("유저 정보가 올바르지 않습니다.");
        }

        // 좋아요 토글
        boolean isLiked = boardService.toggleLike(userId, boardNo);

        if (isLiked) {
            return ResponseEntity.ok(Map.of("liked", true));
        } else {
            return ResponseEntity.ok(Map.of("liked", false));
        }
    }
}
