package br.com.devcave.s2it.ponto.dto;

import lombok.Data;

@Data
public class MonthSummaryDTO {
    private int fillDays;
    private double totalHours;
    private boolean hoursInWeekendHoliday;
}
