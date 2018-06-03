package br.com.devcave.s2it.ponto.exception;

public class PeriodLimitExcedingException extends PeriodValidationException {
    public PeriodLimitExcedingException() {
        super("O limite do período foi excedido");
    }
}
