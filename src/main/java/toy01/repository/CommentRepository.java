package toy01.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import toy01.entity.Board;
import toy01.entity.Comment;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

//    @EntityGraph(attributePaths = {"user", "replies", "replies.user"})
//    @Query("SELECT c FROM Comment c WHERE c.board = :board AND c.parent IS NULL ORDER BY c.createdAt ASC")
    List<Comment> findByBoardAndParentIsNullOrderByCreatedAtAsc(Board board); // 일반 댓글만 (대댓글 제외)
}
