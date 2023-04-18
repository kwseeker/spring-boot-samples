package top.kwseeker.samples.anti.reptile.controller;

import cn.keking.anti_reptile.annotation.AntiReptile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/anti")
public class AntiReptileDemoController {

    @AntiReptile
    @GetMapping("/greet")
    public String greet() {
        return "Hello，World!";
    }
}
