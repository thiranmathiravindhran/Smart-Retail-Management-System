
package com.smartretail.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/reorder")
    public String reorderRedirect() {
        return "redirect:/reorders.html";
    }
}
