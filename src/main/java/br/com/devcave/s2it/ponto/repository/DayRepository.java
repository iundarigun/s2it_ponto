package br.com.devcave.s2it.ponto.repository;

import br.com.devcave.s2it.ponto.domain.Day;
import br.com.devcave.s2it.ponto.domain.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DayRepository extends CrudRepository<Day, Long> {
    Optional<Day> findByDayAndMonthAndUser(LocalDate day, LocalDate month, User user);

    List<Day> findByMonthAndUserOrderByDay(LocalDate month, User user);

    @Modifying
    void deleteByDayAndUser(LocalDate day, User user);

}
