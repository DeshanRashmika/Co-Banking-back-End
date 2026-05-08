package edu.icet.banking.auth.api.mapper;

import edu.icet.banking.auth.api.dto.UserResponse;
import edu.icet.banking.auth.domain.entity.User;

public final class AuthMapper {
    private AuthMapper() {
    }

    public static UserResponse toUserResponse(User user) {
        return UserResponse.from(user);
    }
}

