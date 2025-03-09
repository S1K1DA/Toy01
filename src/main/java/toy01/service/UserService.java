package toy01.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy01.dto.request.UserRequestDto;
import toy01.dto.response.UserResponseDto;
import toy01.entity.User;
import toy01.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    // 마이페이지 정보 수정
    @Transactional
    public void updateProfile(String email, UserRequestDto userRequestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        user.updateProfile(userRequestDto.getNickname(), userRequestDto.getEmail(),
                userRequestDto.getProfileImage(), userRequestDto.getProfileImagePath());
    }

}
