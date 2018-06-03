package br.com.devcave.s2it.ponto.service.impl;

import br.com.devcave.s2it.ponto.client.HolidayClient;
import br.com.devcave.s2it.ponto.domain.Holiday;
import br.com.devcave.s2it.ponto.dto.HolidayDTO;
import br.com.devcave.s2it.ponto.exception.HolidayAlreadyExistsException;
import br.com.devcave.s2it.ponto.mapper.HolidayMapper;
import br.com.devcave.s2it.ponto.repository.HolidayRepository;
import br.com.devcave.s2it.ponto.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class HolidayServiceImpl implements HolidayService {

    @Autowired
    private HolidayClient holidayClient;

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private HolidayMapper holidayMapper;

    @Override
    @Transactional
    public void importHolidays(Integer year){
        List<HolidayDTO> holidayDTOList = holidayClient.findAllHolidaysInCity(year.toString(), "SP", "ARARAQUARA");

        holidayMapper.fromDTOList(holidayDTOList)
                .stream()
                .filter(holiday -> !holidayRepository.existsByDate(holiday.getDate()))
                .forEach(holiday -> holidayRepository.save(holiday));
    }

    @Override
    @Transactional
    public Holiday create(Holiday holiday) {
        if (holidayRepository.existsByDate(holiday.getDate())){
            throw new HolidayAlreadyExistsException();
        }
        return holidayRepository.save(holiday);
    }

    @Override
    @Transactional
    public void delete(List<LocalDate> dateList) {
        dateList.forEach(date->
            holidayRepository.deleteByDate(date));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Holiday> getHolidayList(){
        return holidayRepository.findAll();
    }
}


