package com.zzx.config;

import com.zzx.controller.HostController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("allowRegistration")
    public boolean allowRegistration() {
        return HostController.allowRegistration;
    }

    @ModelAttribute("allowInteraction")
    public boolean allowInteraction() {
        return HostController.allowInteraction;
    }
}
