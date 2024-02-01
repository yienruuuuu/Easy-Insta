package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

@Slf4j
@Tag(name = "Login controller", description = "login account and password controller")
@RestController
@RequestMapping("login")
public class LoginController extends BaseController {

    public LoginController(HttpSession session, HttpServletRequest request, HttpServletResponse response, MessageSource message) {
        super(session, request, response, message);
    }

    @Operation(summary = "check login account and password", description = "try to get login account")
    @GetMapping(value = "getLoginAccount")
    public List<Objects> getLoginAccount() {
        return null;
    }
}