package br.com.devcave.s2it.ponto.restcontroller;

import br.com.devcave.s2it.ponto.domain.Period;
import br.com.devcave.s2it.ponto.domain.User;
import br.com.devcave.s2it.ponto.repository.UserRepository;
import br.com.devcave.s2it.ponto.service.PeriodService;
import br.com.devcave.s2it.ponto.dto.DayDTO;
import br.com.devcave.s2it.ponto.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/rest/period")
public class PeriodRestController {

    @Autowired
    private PeriodService periodService;
    @Autowired
    private UserService userService;

    @PostMapping("/user/{userId}")
    public void create(@PathVariable Long userId, @RequestBody Set<DayDTO> dayDTOList){
        periodService.create(userId, dayDTOList);
    }

    @GetMapping("/{day}")
    public HttpEntity<DayDTO> getDay(Long userId, LocalDate date){
        User user = userService.findById(userId);
        return new HttpEntity<>(periodService.getDay(user, date));
    }
}
