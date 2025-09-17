package com.example.bankcards.utility.validator;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.error.BadRequestException;
import com.example.bankcards.utility.constant.ErrorMessagesConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserValidator {

    public void validateUserForUpdate(User user) {

        if (!user.isActive()) {
            log.error("validateUserForUpdate. Cannot update deactivated user. User id: {}", user.getId());
            throw new BadRequestException(ErrorMessagesConstant.CANNOT_UPDATE_DEACTIVATED_USER);
        }

    }

    public void validateUserForDeactivation(User user) {
        log.debug("validateUserForDeactivation. Validating user for deactivation. User id: {}", user.getId());

        if (!user.isActive()) {
            log.error("validateUserForDeactivation. User is already deactivated. User id: {}", user.getId());
            throw new BadRequestException(ErrorMessagesConstant.USER_ALREADY_DEACTIVATED);
        }

        if (user.getRole() == Role.ROLE_ADMIN) {
            log.error("validateUserForDeactivation. User role is ADMIN. User id: {}", user.getId());
            throw new BadRequestException(ErrorMessagesConstant.CANNOT_DEACTIVATED_ADMIN);
        }
    }

    public void validateUserForActivation(User user) {
        log.debug("validateUserForActivation. Validating user for activation. User id: {}", user.getId());

        if (user.isActive()) {
            log.error("validateUserForActivation. User is already active. User id: {}", user.getId());
            throw new BadRequestException(ErrorMessagesConstant.USER_ALREADY_ACTIVE);
        }
    }

    public void validateUserForDelete(User user) {
        log.debug("validateUserForDelete. Validating user for deletion. User id: {}", user.getId());

        if (user.getCards() != null && !user.getCards().isEmpty()) {
            log.error("validateUserForDelete. Cannot delete user with associated cards. User id: {}", user.getId());
            throw new BadRequestException(ErrorMessagesConstant.CANNOT_DELETE_USER_WITH_CARDS);
        }
    }

}
