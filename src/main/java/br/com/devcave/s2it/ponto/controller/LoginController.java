package br.com.devcave.s2it.ponto.controller;

import br.com.devcave.s2it.ponto.domain.User;
import br.com.devcave.s2it.ponto.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@Slf4j
@Controller
public class LoginController extends BaseController{

    @Autowired
    private UserRepository userRepository;

    @GetMapping("login")
    public ModelAndView index(){
        log.info("M=index");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User principal = (User)authentication.getPrincipal();
            return new ModelAndView("redirect:/period/week?userId="+principal.getId());
        }
        return new ModelAndView("index");
    }

    @GetMapping("register")
    public ModelAndView register(){
        return new ModelAndView("register").addObject("user", new User());
    }

    @PostMapping("register")
    public ModelAndView register(User user, @RequestParam String confirmpassword, HttpServletRequest request){
        if (!user.getPassword().equals(confirmpassword)){
            request.setAttribute(ERROR_MESSAGES, Collections.singletonList("O password não coincide"));
            return new ModelAndView("register");
        }
        if (userRepository.existsByUsername(user.getUsername())){
            request.setAttribute(ERROR_MESSAGES, Collections.singletonList("O usuário já está cadastrado"));
            return new ModelAndView("register");
        }

        userRepository.save(user);
        request.setAttribute(SUCCESS_MESSAGE, "Usuário cadastrado com sucesso");

        return new ModelAndView("index");
    }

}
