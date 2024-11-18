package com.ttn.mohitramtari.bootcampproject.ecommerce.app.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class Scheduler {

    @Autowired
    EmailSenderService emailSenderService;

    //reference - https://spring.io/blog/2020/11/10/new-in-spring-5-3-improved-cron-expressions
    @Scheduled(cron = "@daily") //Here @daily means everyday at 12:00 a.m.
    public void fixedRateSch() {
        emailSenderService.sendEmail(emailSenderService.sendSellerActivationMail());
        emailSenderService.sendEmail(emailSenderService.sendProductActivationListMail());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        System.out.println("Fixed Rate scheduler:: " + strDate);
    }

}
