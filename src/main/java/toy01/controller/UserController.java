package toy01.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import toy01.dto.UserRequestDto;
import toy01.dto.UserResponseDto;
import toy01.service.UserService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    // 회원가입 API
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto responseDto = userService.registerUser(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequestDto requestDto, HttpServletRequest request) {
        boolean isAuthenticated = userService.authenticateUser(requestDto);

        if (isAuthenticated) {
            // 세션에 이메일 저장
            HttpSession session = request.getSession();
            session.setAttribute("email", requestDto.getEmail());
            return ResponseEntity.ok("로그인 성공!");
        } else {
            return ResponseEntity.status(401).body("이메일 또는 비밀번호가 잘못되었습니다.");
        }
    }

    // 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();  // 세션 무효화
            return ResponseEntity.ok("로그아웃 성공");
        } else {
            return ResponseEntity.status(400).body("세션이 존재하지 않습니다.");
        }
    }

}
