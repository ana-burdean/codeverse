package com.codeverse.platform.service;

import com.codeverse.platform.model.RegistrationRequest;
import com.codeverse.platform.model.User;
import com.codeverse.platform.model.LoginRequest;
import java.util.List;// This line will now work
// <-- Add this line

public interface UserService {
    User registerUser(RegistrationRequest request);
    User loginUser(LoginRequest request);
    List<User> getAllUsers(int adminUserId);
    void deleteUser(int userIdToDelete, int adminUserId);
}