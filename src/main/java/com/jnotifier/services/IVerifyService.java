package com.jnotifier.services;

import com.jnotifier.payload.response.ServiceReply;

public interface IVerifyService {
    ServiceReply login(String username);
    ServiceReply verifyEmail(String username);
    ServiceReply forgotPassword(String username);
}
