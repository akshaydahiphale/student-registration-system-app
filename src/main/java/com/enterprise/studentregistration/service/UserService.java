package com.enterprise.studentregistration.service;

import com.enterprise.studentregistration.dto.ChangePasswordDTO;
import com.enterprise.studentregistration.dto.CreateUserDTO;
import com.enterprise.studentregistration.dto.ForgotPasswordDTO;
import com.enterprise.studentregistration.dto.ResetPasswordDTO;
import com.enterprise.studentregistration.dto.SelfRegisterDTO;
import com.enterprise.studentregistration.entity.User;

import java.util.List;

public interface UserService {

    User getByUsername(String username);

    String initiatePasswordReset(ForgotPasswordDTO dto);

    void resetPassword(ResetPasswordDTO dto);

    void changePassword(String username, ChangePasswordDTO dto);

    /** Admin-created account (ADMIN or STUDENT). */
    User createUser(CreateUserDTO dto);

    /** Public self-registration: creates Student + linked User, disabled until approved. */
    User registerStudent(SelfRegisterDTO dto);

    /** All accounts awaiting admin approval. */
    List<User> getPendingUsers();

    /** Enables a pending account so it can log in. */
    void approveUser(Long userId);
}