package br.com.devcave.s2it.ponto.repository;

import br.com.devcave.s2it.ponto.domain.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    boolean existsByDate(LocalDate date);

    @Modifying
    void deleteByDate(LocalDate dateList);
}
