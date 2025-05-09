package org.BSB.com.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.BSB.com.dto.GoalDto;
import org.BSB.com.entity.Goal;
import org.BSB.com.entity.User;

public interface GoalService {
    List<Goal> findByUser(User user);

    Optional<Goal> findByUserAndCategory(User user, String category);

    Goal save(Goal goal);

    Optional<Goal> findById(Long id);

    List<Goal> findGoalsByUser(User user);

    Goal createGoal(User user, GoalDto dto);

    void deleteGoalByIdAndUser(Long goalId, User user);

    /** AI-driven: create a goal for the given user email */
    void createGoal(String userEmail, String category, BigDecimal limitAmount);

    /** AI-driven: delete a goal by id for the given user email */
    void deleteGoal(String userEmail, Long goalId);
}
