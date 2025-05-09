package org.BSB.com.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.BSB.com.dto.GoalDto;
import org.BSB.com.entity.Goal;
import org.BSB.com.entity.User;
import org.BSB.com.repository.GoalRepository;
import org.BSB.com.service.GoalService;
import org.BSB.com.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepo;
    private final UserService userService;

    public GoalServiceImpl(GoalRepository goalRepo,
            UserService userService) {
        this.goalRepo = goalRepo;
        this.userService = userService;
    }

    @Override
    public List<Goal> findByUser(User user) {
        return goalRepo.findByUser(user);
    }

    @Override
    public Optional<Goal> findByUserAndCategory(User user, String category) {
        return goalRepo.findByUserAndCategory(user, category);
    }

    @Override
    public Goal save(Goal goal) {
        return goalRepo.save(goal);
    }

    @Override
    public Optional<Goal> findById(Long id) {
        return goalRepo.findById(id);
    }

    @Override
    public List<Goal> findGoalsByUser(User user) {
        return goalRepo.findByUser(user);
    }

    @Override
    public Goal createGoal(User user, GoalDto dto) {
        throw new UnsupportedOperationException("Use AI-driven createGoal or this method");
    }

    @Override
    @Transactional
    public void createGoal(String userEmail, String category, BigDecimal limitAmount) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Goal g = new Goal();
        g.setUser(user);
        g.setCategory(category);
        g.setLimitAmount(limitAmount);
        g.setSpent(BigDecimal.ZERO);
        goalRepo.save(g);
    }

    @Override
    @Transactional
    public void deleteGoal(String userEmail, Long goalId) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        goalRepo.deleteByIdAndUser(goalId, user);
    }

    @Override
    public void deleteGoalByIdAndUser(Long goalId, User user) {
        goalRepo.deleteByIdAndUser(goalId, user);
    }
}
