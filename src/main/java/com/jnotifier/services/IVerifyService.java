package com.jnotifier.services;

import com.jnotifier.payload.response.ServiceReply;

public interface IVerifyService {
    ServiceReply login(String username);
    ServiceReply login(String username, String clientType);
    ServiceReply verifyEmail(String username);
    ServiceReply forgotPassword(String username);
}
