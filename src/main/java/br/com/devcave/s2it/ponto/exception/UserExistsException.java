package br.com.devcave.s2it.ponto.exception;

public class UserExistsException extends RuntimeException {

    public UserExistsException(){
        super("Usuário já existe");
    }
}
