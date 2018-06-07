package br.com.devcave.s2it.ponto.service.impl;

import br.com.devcave.s2it.ponto.domain.Day;
import br.com.devcave.s2it.ponto.domain.Period;
import br.com.devcave.s2it.ponto.domain.User;
import br.com.devcave.s2it.ponto.dto.DayDTO;
import br.com.devcave.s2it.ponto.dto.MonthSummaryDTO;
import br.com.devcave.s2it.ponto.dto.PeriodDTO;
import br.com.devcave.s2it.ponto.exception.DayLimitExcedingException;
import br.com.devcave.s2it.ponto.exception.IntervalDayException;
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
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
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
        dayDTOList.forEach(d -> persistDay(user, d));
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
    public List<DayDTO> getWeek(User user, LocalDate date) {
        LocalDate startWeek = date.with(WeekFields.of(DayOfWeek.MONDAY, 1).dayOfWeek(), 1L);
        LocalDate endWeek = date.with(WeekFields.of(DayOfWeek.MONDAY, 1).dayOfWeek(), 7L);

        List<DayDTO> dayDTOList = Stream.iterate(startWeek, d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startWeek, endWeek.plusDays(1)))
                .map(d -> getDayDTO(d, periodRepository.findByDayDayAndDayUser(d, user)))
                .collect(Collectors.toList());

        return dayDTOList;
    }

    @Override
    @Transactional
    public void deleteDay(User user, LocalDate date) {
        dayRepository.deleteByDayAndUser(date, user);
    }

    @Override
    @Transactional
    public void persistDay(User user, DayDTO dayDTO) {

        LocalDate monthDate = dayDTO.getDay().withDayOfMonth(1);

        Day day = dayRepository.findByDayAndMonthAndUser(dayDTO.getDay(), monthDate, user)
                .orElse(Day
                        .builder()
                        .day(dayDTO.getDay())
                        .month(monthDate)
                        .user(user)
                        .build());

        // Validar periodos
        validatePeriods(dayDTO.getDay(), user, dayDTO.getPeriodList());

        day.getPeriodList().forEach(p -> periodRepository.delete(p));

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

    @Override
    @Transactional(readOnly = true)
    public MonthSummaryDTO monthSummary(LocalDate date, User user) {
        LocalDate monthDate = date.withDayOfMonth(1);
        List<DayDTO> dayDTOList = dayRepository.findByMonthAndUserOrderByDay(monthDate, user)
                .stream()
                .map(d -> getDayDTO(d.getDay(), d.getPeriodList()))
                .collect(Collectors.toList());

        MonthSummaryDTO monthSummaryDTO = new MonthSummaryDTO();
        monthSummaryDTO.setFillDays(dayDTOList.size());
        dayDTOList.forEach(d ->
        {
            monthSummaryDTO.setHoursInWeekendHoliday(monthSummaryDTO.isHoursInWeekendHoliday() || d.isHoliday() || d.isWeekend());
            monthSummaryDTO.setTotalHours(monthSummaryDTO.getTotalHours() +
                    (d.getPeriodList().stream()
                            .mapToDouble(p -> Duration.between(p.getStartTime(), p.getEndTime()).toMinutes())
                            .sum() / 60) - ((d.isHoliday() || d.isWeekend()) ? 0 : 8));
        });

        return monthSummaryDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportMonth(LocalDate date, User user) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("model/planilhaBase.xlsx").getFile());
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook wb = new XSSFWorkbook(fis);


            XSSFSheet sheet = wb.getSheetAt(0);


            setValueInCell(sheet, 1, 1, user.getName());
            setValueInCell(sheet, 4, 1, date.format(DateTimeFormatter.ofPattern("MMM/yyyy")));

            List<Day> dayList = dayRepository.findByMonthAndUserOrderByDay(date.withDayOfMonth(1), user);
            int initialRow = 7;
            IntStream.range(0, dayList.size()).forEach(row ->
                    {
                        int initialCol = 3;
                        List<Period> periodList = dayList.get(row).getPeriodList();
                        setValueInCell(sheet, initialRow + row, 0, dayList.get(row).getDay().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                        setValueInCell(sheet, initialRow + row, 1, user.getCostCenter());
                        setValueInCell(sheet, initialRow + row, 2, user.getTeam());
                        setValueInCell(sheet, initialRow + row, 12, user.getManager());

                        IntStream.range(0, periodList.size()).forEach(col ->
                                {
                                    Period period = periodList.get(col);
                                    setValueInCell(sheet, initialRow + row, initialCol + (col * 2), period.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                                    setValueInCell(sheet, initialRow + row, initialCol + (col * 2) + 1, period.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                                }
                        );
                    }
            );

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // Usamos para escrever o workbook
            wb.write(bos);
            // Fechamos o stream
            bos.close();
            return bos.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    private void setValueInCell(XSSFSheet sheet, int row, int col, String value) {
        sheet.getRow(row).getCell(col).setCellValue(value);
    }

    private DayDTO getDayDTO(LocalDate date, List<Period> periodList) {
        return DayDTO.builder()
                .day(date)
                .holiday(holidayRepository.existsByDate(date))
                .weekend(date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY))
                .periodList(periodMapper.toDTOList(periodList))
                .build();
    }

    private void validatePeriods(LocalDate date, User user, List<PeriodDTO> periodDTOList) {
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

        if (total > 600) {
            throw new DayLimitExcedingException();
        }


        periodDTOList.stream().sorted(Comparator.comparing(PeriodDTO::getStartTime));
        // Validando superposição
        periodDTOList.forEach(periodDTO ->
                periodDTOList.stream().filter(
                        p2 -> isTimeBetween(periodDTO.getStartTime(), p2.getStartTime(), p2.getEndTime())
                                || isTimeBetween(periodDTO.getEndTime(), p2.getStartTime(), p2.getEndTime()))
                        .findFirst().ifPresent(p ->
                        {
                            throw new SuperimposePeriodException();
                        }
                )
        );
        // Validar intervalos
        LocalTime comparing = null;
        for (PeriodDTO periodDTO : periodDTOList) {
            if (comparing != null) {
                long interval = Duration.between(comparing, periodDTO.getStartTime()).toMinutes();
                if (interval < 60) {
                    throw new IntervalLimitException();
                }
            }
            comparing = periodDTO.getEndTime();
        }

        // Validar periods anterior/posterior
        Period previousPeriod = periodRepository.findFirstByDayDayAndDayUserOrderByStartTimeDesc(date.minusDays(1), user);
        if (previousPeriod != null) {
            long interval = Duration.between(previousPeriod.getEndTime(), LocalTime.of(23,59)).toMinutes() + 1 +
                    Duration.between( LocalTime.of(0,0),periodDTOList.get(0).getStartTime()).toMinutes();

            if (interval < 11 * 60) {
                throw new IntervalDayException();
            }
        }
        Period nextPeriod = periodRepository.findFirstByDayDayAndDayUserOrderByStartTime(date.plusDays(1), user);
        if (nextPeriod != null) {
            long interval = Duration.between(periodDTOList.get(periodDTOList.size() - 1).getEndTime(),
                    LocalTime.of(23,59)).toMinutes() + 1 +
                    Duration.between( LocalTime.of(0,0), nextPeriod.getStartTime()).toMinutes();
            if (interval < 11 * 60) {
                throw new IntervalDayException();
            }
        }
    }

    private boolean isTimeBetween(LocalTime target, LocalTime startTime, LocalTime endTime) {
        return target.isAfter(startTime) && target.isBefore(endTime);
    }
}
