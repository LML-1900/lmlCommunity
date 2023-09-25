package com.lml.community.community;

import com.lml.community.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这里可以⭐赌⭐博⭐，可以嫖娼，可以吸毒，可以开票，哈哈哈！赌博吸毒嫖娼开票";
//        String text = "hello";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
