package toy01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy01.entity.BoardLike;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    Optional<BoardLike> findByUserIdAndBoard_BoardNo(Long userId, Long boardNo);

    boolean existsByUserIdAndBoard_BoardNo(Long userId, Long boardNo);

    void deleteByUserIdAndBoard_BoardNo(Long userId, Long boardNo);
}
