package br.com.devcave.s2it.ponto.controller;

import br.com.devcave.s2it.ponto.domain.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {
    protected final static String ERROR_MESSAGES = "ERROR_MESSAGES";
    protected final static String SUCCESS_MESSAGE = "SUCCESS_MESSAGE";

    protected User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}
