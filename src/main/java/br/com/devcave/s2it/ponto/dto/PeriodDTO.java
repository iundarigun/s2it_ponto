package br.com.devcave.s2it.ponto.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.LocalTime;

@Data
public class PeriodDTO {
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    public Long calculateDurationInMinuts(){
        return Duration.between(getStartTime(), getEndTime()).toMinutes();
    }
}
