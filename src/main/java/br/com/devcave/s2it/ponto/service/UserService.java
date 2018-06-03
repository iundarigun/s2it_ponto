package br.com.devcave.s2it.ponto.service;

import br.com.devcave.s2it.ponto.domain.User;

public interface UserService {
    User findById(Long id);
}
