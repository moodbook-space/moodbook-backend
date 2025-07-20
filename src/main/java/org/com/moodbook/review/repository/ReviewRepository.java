package org.com.moodbook.review.repository;

import org.com.moodbook.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

//    Page<Review> findByBookId(Long id, Pageable pageable);

    /* N + 1 문제 처리 */
    @EntityGraph(attributePaths = {"member", "member.memberProfile"})
    Page<Review> findByBookId(Long bookId, Pageable pageable);

}
