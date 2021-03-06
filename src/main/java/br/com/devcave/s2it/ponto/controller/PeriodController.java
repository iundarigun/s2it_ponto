package br.com.devcave.s2it.ponto.controller;

import br.com.devcave.s2it.ponto.domain.User;
import br.com.devcave.s2it.ponto.dto.DayDTO;
import br.com.devcave.s2it.ponto.dto.MonthSummaryDTO;
import br.com.devcave.s2it.ponto.dto.PeriodDTO;
import br.com.devcave.s2it.ponto.exception.PeriodValidationException;
import br.com.devcave.s2it.ponto.service.PeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/period")
public class PeriodController extends BaseController {

    @Autowired
    private PeriodService periodService;

    @GetMapping("week")
    public ModelAndView week(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        if (date == null){
            date = LocalDate.now();
        }
        User user = getUser();
        List<DayDTO> week = periodService.getWeek(user, date);

        return new ModelAndView("period/week")
                .addObject("user", user)
                .addObject("week", week)
                .addObject("date", date)
                .addObject("previousWeek", date.minusWeeks(1))
                .addObject("nextWeek", date.plusWeeks(1));

    }

    @DeleteMapping("day")
    public ModelAndView deleteDay(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, RedirectAttributes redirectAttributes){
        periodService.deleteDay(getUser(), date);

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Períodos removidos com sucesso");

        StringBuilder url = new StringBuilder()
                .append("redirect:/period/week?date=")
                .append(date);
        return new ModelAndView(url.toString());
    }

    @GetMapping("day")
    public ModelAndView editDay(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        User user = getUser();
        DayDTO dayDTO= periodService.getDay(user, date);
        return getDayModelAndView(user, dayDTO);
    }

    @PostMapping("day")
    public ModelAndView saveDay(DayDTO day, HttpServletRequest request, RedirectAttributes redirectAttributes){
        List<PeriodDTO> periodList = day.getPeriodList().stream()
                .filter(p -> p.getStartTime() != null && p.getEndTime() != null)
                .collect(Collectors.toList());
        day.setPeriodList(periodList);

        User user = getUser();
        try {
            periodService.persistDay(user, day);
        }catch(PeriodValidationException e){
            request.setAttribute(ERROR_MESSAGES, Collections.singletonList(e.getMessage()));
            return  getDayModelAndView(user, day);
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Período persistido com sucesso");

        StringBuilder url = new StringBuilder()
                .append("redirect:/period/week?date=")
                .append(day.getDay());
        return new ModelAndView(url.toString());
    }

    @GetMapping("month")
    public ModelAndView monthSummary(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, int minusMonth){
        MonthSummaryDTO monthSummaryDTO = periodService.monthSummary(date.minusMonths(minusMonth), getUser());
        return new ModelAndView("period/month")
                .addObject("monthSummary", monthSummaryDTO)
                .addObject("month", date.format(DateTimeFormatter.ofPattern("MM-yyyy")))
                .addObject("date", date);
    }

    @GetMapping("month/export")
    public ModelAndView exportMonth(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, HttpServletRequest request, HttpServletResponse response){
        try (final ServletOutputStream outputStream = response.getOutputStream()){
            byte[] bytes = periodService.exportMonth(date, getUser());

            response.setHeader("Content-Disposition", "attachment; filename=" + date.format(DateTimeFormatter.ofPattern("MM-yyyy")) + ".xlsx");
            response.setHeader("pragma", "public");
            response.setHeader("Cache-control", "must-revalidate");
            response.setContentLength(bytes.length);
            outputStream.write(bytes);
            outputStream.flush();
        }catch (Exception e){
            request.setAttribute(ERROR_MESSAGES, Collections.singletonList(e.getMessage()));
            monthSummary(date,0);
        }
        return null;
    }

    private ModelAndView getDayModelAndView(User user, DayDTO dayDTO) {
        ArrayList<PeriodDTO> periodList = new ArrayList<>();
        if (dayDTO.getPeriodList() != null){
            periodList.addAll(dayDTO.getPeriodList());
        }
        IntStream.range(0, 3 - dayDTO.getPeriodList().size()).forEach(i->periodList.add(new PeriodDTO()));
        dayDTO.setPeriodList(periodList);
        return new ModelAndView("period/day")
                .addObject("user", user)
                .addObject("day", dayDTO);
    }

}
