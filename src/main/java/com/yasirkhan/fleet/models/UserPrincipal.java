package com.yasirkhan.fleet.models;

public record UserPrincipal(
        String userId,
        String username,
        String role
) {}