package br.com.devcave.s2it.ponto.repository;

import br.com.devcave.s2it.ponto.domain.Period;
import br.com.devcave.s2it.ponto.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface PeriodRepository extends CrudRepository<Period, Long> {
    List<Period> findByDayDayAndDayUser(LocalDate day, User user);
}
