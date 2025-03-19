package toy01.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toy01.dto.request.BoardRequestDto;
import toy01.service.BoardService;

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

        // 🔥 userId 설정 후 저장
        Long boardNo = boardService.createBoard(requestDto, userId);
        return ResponseEntity.ok(boardNo);
    }
}
