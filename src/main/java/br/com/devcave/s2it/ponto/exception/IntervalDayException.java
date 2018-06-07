package br.com.devcave.s2it.ponto.exception;

public class IntervalDayException extends PeriodValidationException {

    public IntervalDayException() {
        super("O intervalo entre dias deve ser superior a 11 horas.");
    }
}
