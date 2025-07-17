package com.circleon.common.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Component
public class SignedUrlManager {

    @Value("${signed-url.secret}")
    private String signedUrlSecretKey;

    private final long expiredTimeSec = 3600L; // 1시간

    //생성
    public String createSignedUrl(String baseUrl){

        long now = Instant.now().getEpochSecond();

        long rounded = (now / expiredTimeSec) * expiredTimeSec;

        long expires = rounded + expiredTimeSec;

        String signature = createSignature(baseUrl, expires);
        return baseUrl + "?expires=" + expires + "&signature=" + signature;
    }


    //사인 검증
    public boolean isValidSignedUrl(String baseUrl, String expires, String signature){
        String expected = createSignature(baseUrl, Long.parseLong(expires));
        return expected.equals(signature);
    }

    //유효기간 검증
    public boolean isExpired(String expires){
        long exp = Long.parseLong(expires);
        return Instant.now().getEpochSecond() > exp;
    }


    private String createSignature(String baseUrl, long expires){
        try{
            String data = baseUrl + "|" + expires;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signedUrlSecretKey.getBytes(StandardCharsets.UTF_8), mac.getAlgorithm()));
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
        }catch (Exception e){
            throw new RuntimeException("Error while generating signature", e);
        }
    }
}
