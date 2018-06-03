package br.com.devcave.s2it.ponto.exception;

public class SuperimposePeriodException extends PeriodValidationException{
    public SuperimposePeriodException() {
        super("Os peridos não podem ser superpostos");
    }
}
