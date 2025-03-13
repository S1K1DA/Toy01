package toy01.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import toy01.dto.request.PasswordUpdateRequest;
import toy01.dto.request.UserRequestDto;
import toy01.dto.response.UserResponseDto;
import toy01.service.UserService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
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

    // 마이페이지 조회 API
    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
        }

        // 세션에서 저장된 이메일 가져오기
        String email = (String) session.getAttribute("email");
        System.out.println("✅ [DEBUG] 로그인한 사용자 이메일: " + email);

        // 세션에서 가져온 이메일을 기반으로 유저 조회
        UserResponseDto userProfile = userService.getUserProfile(email);
        return ResponseEntity.ok(userProfile);
    }

    @PostMapping("/update-profile")
    public ResponseEntity<String> updateProfile(
            HttpServletRequest request,
            @RequestParam("nickname") String nickname,
            @RequestParam("email") String newEmail,
            @RequestParam("name") String name,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
        }

        // 세션에서 이메일 가져오기
        String currentEmail = (String) session.getAttribute("email");

        // 유저 정보 업데이트
        userService.updateUserProfile(currentEmail, newEmail, nickname, name, profileImage);

        // 세션 이메일 변경
        session.removeAttribute("email");
        session.setAttribute("email", newEmail);

        return ResponseEntity.ok("프로필 정보가 성공적으로 수정되었습니다.");
    }


        @PostMapping("/update-password")
        public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequest request, HttpSession session) {

        String email = (String) session.getAttribute("email"); // 현재 로그인한 사용자의 이메일
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            boolean isUpdated = userService.updatePassword(email, request.getCurrentPassword(), request.getNewPassword());
            if (isUpdated) {
                return ResponseEntity.ok("비밀번호 변경 성공");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("현재 비밀번호가 올바르지 않습니다.");
            }
        }
    }
