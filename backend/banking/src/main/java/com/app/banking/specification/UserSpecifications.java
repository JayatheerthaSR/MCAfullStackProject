package com.app.banking.specification;

import org.springframework.data.jpa.domain.Specification;

import com.app.banking.entity.User;

public class UserSpecifications {

    public static Specification<User> hasUsername(String username) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + username.toLowerCase() + "%");
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> hasRole(String role) {
        try {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("role"), role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.literal(1), 0);
        }
    }

    public static Specification<User> isActive(boolean active) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isActive"), active);
    }
}