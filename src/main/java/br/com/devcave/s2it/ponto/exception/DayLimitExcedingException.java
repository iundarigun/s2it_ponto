package br.com.devcave.s2it.ponto.exception;

public class DayLimitExcedingException extends PeriodValidationException {

    public DayLimitExcedingException(){
        super("O limite de horas por dia foi execedido");
    }
}
