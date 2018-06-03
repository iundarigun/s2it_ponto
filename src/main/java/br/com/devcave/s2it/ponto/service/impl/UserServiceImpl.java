package br.com.devcave.s2it.ponto.service.impl;

import br.com.devcave.s2it.ponto.domain.User;
import br.com.devcave.s2it.ponto.repository.UserRepository;
import br.com.devcave.s2it.ponto.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
