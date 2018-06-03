package br.com.devcave.s2it.ponto.exception;

public class IntervalLimitException extends PeriodValidationException {
    public IntervalLimitException() {
        super("O descanso entre periodos é inferior ao permitido");
    }
}
