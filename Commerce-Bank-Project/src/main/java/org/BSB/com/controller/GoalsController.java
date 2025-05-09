package org.BSB.com.controller;

import jakarta.validation.Valid;
import org.BSB.com.dto.GoalDto;
import org.BSB.com.entity.Goal;
import org.BSB.com.entity.User;
import org.BSB.com.repository.TransactionRepository;
import org.BSB.com.service.GoalService;
import org.BSB.com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/goals")
public class GoalsController {

    private final GoalService goalService;
    private final UserService userService;
    @Autowired
    private TransactionRepository txRepo;

    public GoalsController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    @ModelAttribute("goalDto")
    public GoalDto goalDto() {
        return new GoalDto();
    }

    @GetMapping
    public String showGoals(Model m, Principal p) {
        User u = userService.findByEmail(p.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Goal> goals = goalService.findGoalsByUser(u);
        // for each goal, fetch the sum (or 0) and set it
        for (Goal g : goals) {
            BigDecimal spent = txRepo.sumAmountByUserAndCategory(u, g.getCategory());
            g.setSpent(spent != null ? spent : BigDecimal.ZERO);
        }
        m.addAttribute("goals", goalService.findGoalsByUser(u));
        m.addAttribute("categories",
                txRepo.findDistinctCategoriesByUser(u));
        m.addAttribute("goalDto", new GoalDto());
        return "goals";
    }

    @PostMapping
    public String addGoal(@Valid @ModelAttribute("goalDto") GoalDto dto,
            BindingResult br,
            Model m,
            Principal p) {
        User u = userService.findByEmail(p.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<String> cats = txRepo.findDistinctCategoriesByUser(u);

        // if they chose “Other”, require & promote the custom
        if ("Other".equals(dto.getCategory())) {
            if (dto.getCustomCategory() == null ||
                    dto.getCustomCategory().isBlank()) {
                br.rejectValue("customCategory",
                        "NotEmpty",
                        "Please enter a custom category");
            } else {
                dto.setCategory(dto.getCustomCategory());
            }
        }

        if (dto.getLimitAmount() == null) {
            br.rejectValue("limitAmount", "NotNull", "Limit amount is required");
        }

        if (br.hasErrors()) {
            u = userService.findByEmail(p.getName()).orElseThrow();
            m.addAttribute("goals", goalService.findGoalsByUser(u));
            m.addAttribute("categories", cats);
            return "goals";
        }

        goalService.createGoal(u, dto);
        return "redirect:/goals";
    }

    @PostMapping("/{id}/delete")
    public String deleteGoal(@PathVariable Long id, Principal p) {
        User u = userService.findByEmail(p.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        goalService.deleteGoalByIdAndUser(id, u);
        return "redirect:/goals";
    }
}