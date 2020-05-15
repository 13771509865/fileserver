package com.yozosoft.fileserver;

import com.yozosoft.fileserver.model.po.FileRefRelationPo;
import com.yozosoft.fileserver.service.refrelation.impl.RefRelationServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YozoFileserverApplicationTests {

    @Autowired
    private RefRelationServiceImpl refRelationService;

    @Test
    public void test() {
        FileRefRelationPo fileRefRelationPo = refRelationService.selectByRefAndApp(1L, 1);
    }
}
