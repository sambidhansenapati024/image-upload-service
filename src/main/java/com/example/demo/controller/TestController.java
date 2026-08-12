//package com.example.demo.controller;
//
//import com.example.demo.enums.OtpPurpose;
//import com.example.demo.model.OtpSession;
//import com.example.demo.service.reids.RedisService;
//import com.example.demo.util.OtpGenerator;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class TestController {
//
//    private final RedisService redisService;
//
//    public TestController(RedisService redisService) {
//        this.redisService = redisService;
//    }
//
//    @GetMapping("/redis-test")
//    public OtpSession test() {
//
//        OtpSession session = new OtpSession();
//
//        session.setEmail("test@gmail.com");
//        session.setOtp("1234");
//        session.setAttempts(0);
//        session.setPurpose(OtpPurpose.LOGIN);
//
//        redisService.save("otp:test", session, 60);
//
//        return redisService.get("otp:test", OtpSession.class);
//
//    }
//
//    @GetMapping("/otp")
//    public String otp() {
//
//        return OtpGenerator.generateOtp();
//
//    }
//
//
//}
