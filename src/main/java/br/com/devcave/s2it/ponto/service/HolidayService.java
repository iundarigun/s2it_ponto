package br.com.devcave.s2it.ponto.service;

import br.com.devcave.s2it.ponto.domain.Holiday;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface HolidayService {

    void importHolidays(Integer year);

    Holiday create(Holiday holiday);

    void delete(List<LocalDate> dateList);

    List<Holiday> getHolidayList();
}
