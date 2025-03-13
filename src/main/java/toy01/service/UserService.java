package toy01.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import toy01.dto.request.UserRequestDto;
import toy01.dto.response.UserResponseDto;
import toy01.entity.User;
import toy01.repository.UserRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @Transactional
    public UserResponseDto registerUser(UserRequestDto requestDto) {
        // 비밀번호 확인
        if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        // 이메일 중복 확인
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        // DTO → Entity 변환 후 저장
        User savedUser = userRepository.save(requestDto.toEntity(encodedPassword));
        // Entity → DTO 변환 후 반환
        return new UserResponseDto(savedUser);
    }

    // 로그인 인증 처리
    public boolean authenticateUser(UserRequestDto requestDto) {
        // 이메일로 유저 검색
        User user = userRepository.findByEmail(requestDto.getEmail()).orElse(null);

        // 사용자 존재 여부 및 비밀번호 일치 확인
        if (user == null || !passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            return false;
        }

        return true;
    }

    // 마이페이지 정보 조회
    public UserResponseDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        return new UserResponseDto(user);
    }

    @Transactional
    public void updateUserProfile(String currentEmail,String newEmail, String nickname, String name, MultipartFile profileImage) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        String newFileName = user.getProfileImage(); // 기존 파일 유지

        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                // 기존 이미지 삭제
                if (newFileName != null) {
                    Files.deleteIfExists(Paths.get(UPLOAD_DIR + newFileName));
                }

                // 새 이미지 저장
                newFileName = System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();
                Path targetPath = Paths.get(UPLOAD_DIR + newFileName);
                Files.copy(profileImage.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                throw new RuntimeException("프로필 이미지 저장 실패: " + e.getMessage());
            }
        }

        user.updateProfile(nickname, newEmail, name, newFileName);
        userRepository.save(user);
    }
    @Transactional
    public boolean updatePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

}
