package br.com.devcave.s2it.ponto.service.impl;

import br.com.devcave.s2it.ponto.domain.Day;
import br.com.devcave.s2it.ponto.domain.Period;
import br.com.devcave.s2it.ponto.domain.User;
import br.com.devcave.s2it.ponto.exception.DayLimitExcedingException;
import br.com.devcave.s2it.ponto.exception.IntervalLimitException;
import br.com.devcave.s2it.ponto.exception.PeriodLimitExcedingException;
import br.com.devcave.s2it.ponto.exception.SuperimposePeriodException;
import br.com.devcave.s2it.ponto.exception.UserNotExistsException;
import br.com.devcave.s2it.ponto.mapper.PeriodMapper;
import br.com.devcave.s2it.ponto.repository.DayRepository;
import br.com.devcave.s2it.ponto.repository.HolidayRepository;
import br.com.devcave.s2it.ponto.repository.PeriodRepository;
import br.com.devcave.s2it.ponto.repository.UserRepository;
import br.com.devcave.s2it.ponto.service.PeriodService;
import br.com.devcave.s2it.ponto.dto.DayDTO;
import br.com.devcave.s2it.ponto.dto.PeriodDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PeriodServiceImpl implements PeriodService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private PeriodRepository periodRepository;

    @Autowired
    private PeriodMapper periodMapper;

    @Override
    @Transactional
    public void create(Long userId, Set<DayDTO> dayDTOList) {
        User user = userRepository.findById(userId).orElseThrow(UserNotExistsException::new);
        dayDTOList.forEach(d-> persistDay(user, d));
    }

    @Override
    @Transactional(readOnly = true)
    public DayDTO getDay(User user, LocalDate date) {
        DayDTO dayDTO = DayDTO.builder()
                .day(date)
                .holiday(holidayRepository.existsByDate(date))
                .weekend(date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY))
                .periodList(periodMapper.toDTOList(periodRepository.findByDayDayAndDayUser(date, user)))
                .build();
        return dayDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DayDTO> getWeek(User user, LocalDate date){
        LocalDate startWeek = date.with(WeekFields.of(DayOfWeek.MONDAY, 1).dayOfWeek(), 1L);
        LocalDate endWeek = date.with(WeekFields.of(DayOfWeek.MONDAY, 1).dayOfWeek(), 7L);

        List<DayDTO> dayDTOList = Stream.iterate(startWeek, d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startWeek, endWeek.plusDays(1)))
                .map(d -> DayDTO.builder()
                        .day(d)
                        .holiday(holidayRepository.existsByDate(d))
                        .weekend(d.getDayOfWeek().equals(DayOfWeek.SATURDAY)||d.getDayOfWeek().equals(DayOfWeek.SUNDAY))
                        .periodList(periodMapper.toDTOList(periodRepository.findByDayDayAndDayUser(d, user)))
                        .build())
                .collect(Collectors.toList());

        return dayDTOList;
    }

    @Override
    @Transactional
    public void deleteDay(User user, LocalDate date){
        dayRepository.deleteByDayAndUser(date, user);
    }

    @Override
    @Transactional
    public void persistDay(User user, DayDTO dayDTO){

        LocalDate monthDate = dayDTO.getDay().withDayOfMonth(1);

        Day day = dayRepository.findByDayAndMonthAndUser(dayDTO.getDay(), monthDate, user)
                .orElse(
                        Day
                            .builder()
                            .day(dayDTO.getDay())
                            .month(monthDate)
                            .user(user)
                            .build());

        // Validar periodos
        validatePeriods(dayDTO.getPeriodList());

        List<Period> periodList = dayDTO.getPeriodList().stream().map(period ->
                    Period.builder()
                        .day(day)
                        .startTime(period.getStartTime())
                        .endTime(period.getEndTime())
                        .build())
                .collect(Collectors.toList());

        day.setPeriodList(periodList);

        dayRepository.save(day);

    }

    private void validatePeriods(List<PeriodDTO> periodDTOList){
        // Validar total e tamanho do periodo
        long total = periodDTOList.stream().mapToLong(periodDTO ->
                {
                    long periodTime = periodDTO.calculateDurationInMinuts();
                    if (periodTime > 6 * 60) {
                        throw new PeriodLimitExcedingException();
                    }
                    return periodTime;
                }
            ).sum();

        if (total > 600){
            throw new DayLimitExcedingException();
        }


        periodDTOList.stream().sorted(Comparator.comparing(PeriodDTO::getStartTime));
        // Validando superposição
        periodDTOList.forEach( periodDTO ->
                periodDTOList.stream().filter(
                        p2-> isTimeBetween(periodDTO.getStartTime(),p2.getStartTime(),p2.getEndTime())
                                || isTimeBetween(periodDTO.getEndTime(),p2.getStartTime(),p2.getEndTime()))
                        .findFirst().ifPresent(p ->
                        {
                            throw new SuperimposePeriodException();
                        }
                )
        );
        // Validar intervalos
        LocalTime comparing = null;
        for (PeriodDTO periodDTO:periodDTOList){
            if (comparing != null){
                long interval = Duration.between(comparing, periodDTO.getStartTime()).toMinutes();
                if (interval < 60){
                    throw new IntervalLimitException();
                }
            }
            comparing = periodDTO.getEndTime();
        }
    }

    private boolean isTimeBetween(LocalTime target, LocalTime startTime, LocalTime endTime){
        return target.isAfter(startTime) && target.isBefore(endTime);
    }
}
