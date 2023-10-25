package com.care4u.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.care4u.hr.membership.Membership;
import com.care4u.hr.membership.MembershipFormDto;
import com.care4u.hr.membership.MembershipService;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.validation.BindingResult;
import javax.validation.Valid;

@RequestMapping("/membership")
@Controller
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService service;

    @GetMapping(value = "/new")
    public String newForm(Model model){
        model.addAttribute("membershipFormDto", new MembershipFormDto());
        return "membership/newForm";
    }

    @PostMapping(value = "/new")
    public String addNew(@Valid MembershipFormDto membershipFormDto, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            return "membership/newForm";
        }

        try {
            Membership item = Membership.builder()
            							.memberFormDto(membershipFormDto)
            							.build();
            service.addNew(item);
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "membership/newForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String login(){
        return "/membership/loginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "/membership/loginForm";
    }

}