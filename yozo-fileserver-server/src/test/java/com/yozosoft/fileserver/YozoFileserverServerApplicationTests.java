package com.yozosoft.fileserver;

import com.yozosoft.fileserver.api.FileServerFeignApi;
import com.yozosoft.fileserver.common.helper.SignHelper;
import com.yozosoft.fileserver.common.utils.UUIDHelper;
import com.yozosoft.fileserver.dto.FileInfoDto;
import com.yozosoft.fileserver.dto.ServerDownloadDto;
import com.yozosoft.fileserver.dto.ServerUploadFileDto;
import com.yozosoft.fileserver.dto.UserDownloadDto;
import com.yozosoft.fileserver.utils.FileServerVerifyUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YozoFileserverServerApplicationTests {

    @Autowired
    private SignHelper signHelper;

    @Autowired
    private FileServerFeignApi fileServerFeignApi;

    @Test
    public void test() throws Exception {
        ServerDownloadDto serverDownloadDto = new ServerDownloadDto();
        serverDownloadDto.setAppName("yzcloud");
        serverDownloadDto.setStorageDir("/users/data/files/333\\document\\d50caaef4ce744f89690f421dad6dbc7");
        FileInfoDto fileInfoDto = new FileInfoDto();
        fileInfoDto.setFileRefId(1L);
        serverDownloadDto.setFileInfos(Arrays.asList(fileInfoDto));
        String nonce = UUIDHelper.generateUUID();
        String s = FileServerVerifyUtil.generateJsonSign(serverDownloadDto, nonce, "qaxet9223210^*&");
        System.out.println(s);
    }

    @Test
    public void testFeignApi() {
//        UserDownloadDto userDownloadDto = new UserDownloadDto();
//        userDownloadDto.setAppName("yzcloud");
//        FileInfoDto fileInfoDto = new FileInfoDto();
//        fileInfoDto.setFileName("123");
//        fileInfoDto.setFileRefId(471996124580810752L);
//        userDownloadDto.setFileInfos(Arrays.asList(fileInfoDto));
//        ResponseEntity<Map<String, Object>> result = fileServerFeignApi.getDownloadUrl(userDownloadDto);
//        System.out.println(result.toString());
//        System.out.println("end");
        ServerUploadFileDto serverUploadFileDto = new ServerUploadFileDto();
        MultipartFile multipartFile = new MultipartFile() {
            @Override
            public String getName() {
                return "123";
            }

            @Override
            public String getOriginalFilename() {
                return "123";
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 3;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File file) throws IOException, IllegalStateException {

            }
        };
        ServerUploadFileDto serverUploadFileDto1 = new ServerUploadFileDto();
        serverUploadFileDto.setAppName("yzcloud");
        ResponseEntity<Map<String, Object>> mapResponseEntity = fileServerFeignApi.serverUploadByFile("yozo", "yozosoft", multipartFile, "yzcloud", "");
        System.out.println("end");
    }

}
