package toy01.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toy01.dto.request.CommentRequestDto;
import toy01.dto.response.CommentResponseDto;
import toy01.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    public ResponseEntity<String> createComment(@RequestBody CommentRequestDto dto, HttpServletRequest request) {
        String email = (String) request.getSession().getAttribute("email");
        if (email == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        commentService.saveComment(dto, email);
        return ResponseEntity.ok("댓글이 등록되었습니다.");
    }

    @GetMapping("/comments/{boardNo}")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable("boardNo") Long boardNo) {
        return ResponseEntity.ok(commentService.getComments(boardNo));
    }

}
