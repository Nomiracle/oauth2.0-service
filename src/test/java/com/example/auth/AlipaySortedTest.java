package com.example.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

@Slf4j
@RunWith(SpringRunner.class)
public class AlipaySortedTest {
    @Test
    public void testSorted(){
        Map<String, String> form = new TreeMap<>();
        form.put("app_id", "1234");
        form.put("method","alipay.system.oauth.token");
        form.put("charset","GBK");
        form.put("sign_type","RSA2");
        form.put("timestamp","1111");

        form.put("version","1.0");
        form.put("grant_type","authorization_code");
        form.put("code","codexxx");

        StringJoiner sj = new StringJoiner("&");
        String sorted = StringUtils.join(form.values(),"&");
        log.info("sorted:{}",sorted);

        form.keySet().forEach(key->{
            sj.add(form.get(key));
        });
        log.info("sorted:{}",sj.toString());

    }
}
