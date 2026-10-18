package com.jnotifier.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.request.SignupRequest;
import com.jnotifier.payload.response.ServiceReply;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    public ServiceReply registerAdminUsers(SignupRequest signupRequest) throws JsonProcessingException, GenericException;
    public ServiceReply getAllUsersDetailsExceptSA(Pageable pageable) throws GenericException;
    public ServiceReply markUserAsDeleted(Long id) throws GenericException;
    public ServiceReply markUserAsSuspended(Long id) throws GenericException;
    public ServiceReply activateUser(Long id) throws GenericException;
    public ServiceReply editProfile(String username, com.jnotifier.payload.request.EditProfileRequest request) throws GenericException;
}
