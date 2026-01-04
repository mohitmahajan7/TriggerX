package org.stark.triggerxbackend.user.repository;

import org.springframework.stereotype.Repository;
import org.stark.triggerxbackend.user.model.User;

import java.util.Optional;

@Repository
public interface UserRepository {

    void save(User user);

    Optional<User> findByEmail(String email);

}
