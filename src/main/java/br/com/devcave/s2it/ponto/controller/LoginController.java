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
        return new ModelAndView("register");
    }

    @PostMapping("register")
    public ModelAndView register(@RequestParam String username, @RequestParam String name,
                                 @RequestParam String password, @RequestParam String confirmpassword){
        if (!password.equals(confirmpassword)){
            return new ModelAndView("register");
        }
        if (userRepository.existsByUsername(username)){
            return new ModelAndView("register");
        }
        User user = User.builder().name(name).username(username).password(password).build();

        userRepository.save(user);

        return new ModelAndView("index");
    }

}
