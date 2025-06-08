package com.shopClone.controller;

import com.shopClone.dto.EmployeeRegisterForm;
import com.shopClone.entity.Employee;
import com.shopClone.entity.Member;
import com.shopClone.entity.Team;
import com.shopClone.repository.EmployeeRepository;
import com.shopClone.repository.MemberRepository;
import com.shopClone.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/employee")
@RequiredArgsConstructor
public class EmployeeAdminController {

    private final EmployeeRepository employeeRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new EmployeeRegisterForm());
        model.addAttribute("members", memberRepository.findAll());
        model.addAttribute("teams", teamRepository.findAll());
        return "admin/employee_register";
    }

    @PostMapping("/register")
    public String registerEmployee(@ModelAttribute("form") EmployeeRegisterForm form) {
        Employee employee = new Employee();
        employee.setName(form.getName());
        employee.setEmail(form.getEmail());
        employee.setLevel(form.getLevel());

        Team team = teamRepository.findById(form.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));
        employee.setTeam(team);

        Employee saved = employeeRepository.save(employee);

        Member member = memberRepository.findById(form.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));
        member.setEmployee(saved);
        memberRepository.save(member);

        return "redirect:/admin/employee/list"; // 또는 success 페이지
    }
}
