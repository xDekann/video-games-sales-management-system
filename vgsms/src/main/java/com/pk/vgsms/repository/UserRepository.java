package com.pk.vgsms.repository;

import com.pk.vgsms.model.entity.Authority;
import com.pk.vgsms.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Query("SELECT u FROM User u JOIN FETCH u.userDetails ud " +
            "JOIN FETCH u.authority ua WHERE u.username = :username")
    User findUserByName(@Param("username") String username);
    @Query("SELECT a FROM Authority a WHERE a.authorityName = :authorityName")
    Authority findAuthorityByAuthorityName(@Param("authorityName") String authorityName);
    Page<User> findAll(Specification<User> spec, Pageable pageable);
    @Query("SELECT u FROM User u JOIN FETCH u.userDetails ud " +
            "JOIN FETCH u.authority ua WHERE u.id = :id")
    User findUserById(@Param("id") Long id);
    @Query("SELECT u FROM User u JOIN FETCH u.userDetails ud " +
            "JOIN FETCH u.authority ua WHERE u.userDetails.email = :email")
    User findUserByEmail(String email);
    Page<User> findAllByUsernameContainingIgnoreCaseAndUsernameNotOrderByUsername(String phrase, String loggedUserName, Pageable pageable);
}
