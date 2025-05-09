package org.BSB.com.repository;

import org.BSB.com.entity.Goal;
import org.BSB.com.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUser(User user);

    void deleteByIdAndUser(Long id, User user);

    Optional<Goal> findByUserAndCategory(User user, String category);
}
