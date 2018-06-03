package br.com.devcave.s2it.ponto.client;

import br.com.devcave.s2it.ponto.dto.HolidayDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "holiday-client", url = "${holiday.url}")
public interface HolidayClient {

    @GetMapping
    List<HolidayDTO> findAllHolidaysInCity(@RequestParam("ano") final String year,
                                           @RequestParam("estado") final String state,
                                           @RequestParam("cidade") final String city);
}
