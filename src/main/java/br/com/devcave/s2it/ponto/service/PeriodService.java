package br.com.devcave.s2it.ponto.service;

import br.com.devcave.s2it.ponto.domain.Period;
import br.com.devcave.s2it.ponto.domain.User;
import br.com.devcave.s2it.ponto.dto.DayDTO;
import br.com.devcave.s2it.ponto.dto.MonthSummaryDTO;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface PeriodService {

    void create(Long userId, Set<DayDTO> dayDTOList);

    DayDTO getDay(User user, LocalDate date);

    List<DayDTO> getWeek(User user, LocalDate localDate);

    void deleteDay(User user, LocalDate date);

    void persistDay(User user, DayDTO dayDTO);

    MonthSummaryDTO monthSummary(LocalDate date, User user);

    byte[] exportMonth(LocalDate date, User user);
}
