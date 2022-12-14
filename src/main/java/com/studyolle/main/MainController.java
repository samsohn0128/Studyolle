package com.studyolle.main;

import com.studyolle.account.CurrentAccount;
import com.studyolle.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        } else {
            log.error("account is null");
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
