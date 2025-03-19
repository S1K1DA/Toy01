package toy01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toy01.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
