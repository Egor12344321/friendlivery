package com.globallogix.auth.repository;

import com.globallogix.auth.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.documents WHERE u.username = :username")
    Optional<User> findByUsernameWithDocuments(@Param("username") String username);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

}
