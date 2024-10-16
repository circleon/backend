package com.circleon.authentication.jwt;


import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Date;



class JwtUtilTest {

    private static final Logger log = LoggerFactory.getLogger(JwtUtilTest.class);

    @Test
    void test(){
        Date date = new Date();
        log.info(date.toString());
    }

}