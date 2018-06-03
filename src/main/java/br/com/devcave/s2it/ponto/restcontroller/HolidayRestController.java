package br.com.devcave.s2it.ponto.restcontroller;

import br.com.devcave.s2it.ponto.domain.Holiday;
import br.com.devcave.s2it.ponto.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rest/holiday")
public class HolidayRestController {

    @Autowired
    private HolidayService holidayService;

    @PostMapping("/import/{year}")
    public void importHoliday(@PathVariable Integer year){
        holidayService.importHolidays(year);
    }

    @PostMapping
    public HttpEntity<Long> create(@RequestBody Holiday holiday){
        holiday = holidayService.create(holiday);
        return new HttpEntity<>(holiday.getId());
    }

    @DeleteMapping
    public void delete(@RequestBody List<LocalDate> dateList){
        holidayService.delete(dateList);
    }

    @GetMapping
    public List<Holiday> findAll(){
        return holidayService.getHolidayList();
    }
}
