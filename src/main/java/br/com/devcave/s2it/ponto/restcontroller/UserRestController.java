package br.com.devcave.s2it.ponto.restcontroller;

import br.com.devcave.s2it.ponto.domain.User;
import br.com.devcave.s2it.ponto.exception.UserExistsException;
import br.com.devcave.s2it.ponto.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/user")
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{username}")
    public HttpEntity<Long> create(@PathVariable String username){
        if (userRepository.existsByName(username)){
            throw new UserExistsException();
        }
        User user = userRepository.save(User.builder().name(username).build());
        return new HttpEntity<>(user.getId());
    }

    @GetMapping
    public HttpEntity<List> list(){
        List<User> list = userRepository.findAll();
        return new HttpEntity<>(list.stream().map(User::getName).collect(Collectors.toList()));
    }
}
