package cn.lollipop.search.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {

    @Autowired
    private ItemClient itemClient;

    @Test
    public void queryCategoryByIds() {
        System.out.println(itemClient.queryCategoryByIds(Arrays.asList(1L, 2L, 3L)));
    }
}