package br.com.devcave.s2it.ponto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"periodList"})
public class DayDTO {
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate day;
    private List<PeriodDTO> periodList;
    private boolean holiday;
    private boolean weekend;

}
