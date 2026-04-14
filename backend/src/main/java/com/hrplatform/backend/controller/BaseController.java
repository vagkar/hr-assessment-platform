package com.hrplatform.backend.controller;

import com.hrplatform.backend.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {

    protected UserPrincipal getPrincipal() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
