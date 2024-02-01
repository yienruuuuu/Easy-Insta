package org.example.controller;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public abstract class BaseController {

    protected final HttpSession session;
    protected final HttpServletRequest request;
    protected final HttpServletResponse response;
    protected final MessageSource message;

    public BaseController(HttpSession session, HttpServletRequest request, HttpServletResponse response, MessageSource message) {
        this.session = session;
        this.request = request;
        this.response = response;
        this.message = message;
    }

    protected String getRemoteAddress() {
        return this.request.getHeader("X-forwarded-for");
    }

}