package com.yozosoft.fileserver.common.utils;

import com.yozosoft.fileserver.common.constants.SysConstant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhoufeng
 * @description URL encode工具类
 * @create 2019-05-05 14:26
 **/
public class UrlEncodingUtils {

    private static Pattern p = Pattern.compile("^(http|https)://[^/]+/", Pattern.CASE_INSENSITIVE);

    /**
     * encode URl
     *
     * @param url 待encode URL
     * @return encode后URL
     * @author zhoufeng
     * @date 2019/7/3
     */
    public static String encodeUrl(String url) {
        return myEncodingUrl(folderUrl(url));
    }

    private static String folderUrl(String url) {
        // 拿到前面的 协议：IP（域名）[端口]/
        Matcher m = p.matcher(url);
        if (m.find()) {
            String hreadStr = m.group();
            url = url.replace(hreadStr, "");
            // 调整结构
            url = url.replaceAll("[^/]+/\\.\\./", "");
            while (url.startsWith("../")) {
                url = url.substring(3);
            }
            return hreadStr + url;
        }
        return url;
    }

    private static String myEncodingUrl(String url) {
        Matcher m = p.matcher(url);
        if (m.find()) {
            int isSplit = url.indexOf("?");
            String urlPart1 = null;
            String urlPart2 = null;
            if (isSplit > -1) {
                urlPart1 = url.substring(0, isSplit);
                urlPart2 = url.substring(isSplit);
            } else {
                urlPart1 = url;
            }
            String result = MyUrlEncode.DEFAULT.encode(urlPart1, Charset.forName(SysConstant.CHARSET));
            if (urlPart2 != null && !"".equals(urlPart2)) {
                //对{和}进行一下处理，免得报非法字符
                urlPart2 = urlPart2.replaceAll("\\{", "%7b");
                urlPart2 = urlPart2.replaceAll("\\}", "%7d");
                result += urlPart2;
            }
            return result;
        } else {
            return url;
        }
    }

    /**
     * @author zhoufeng
     * @description 抄的tomcat的urlencode
     * @date 2019/6/13
     */
    private static class MyUrlEncode implements Cloneable {
        private static final char[] hexadecimal = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        public static final MyUrlEncode DEFAULT = new MyUrlEncode();
        public static final MyUrlEncode QUERY = new MyUrlEncode();
        private final BitSet safeCharacters;
        private boolean encodeSpaceAsPlus;

        public MyUrlEncode() {
            this(new BitSet(256));

            char i;
            for (i = 'a'; i <= 'z'; ++i) {
                this.addSafeCharacter(i);
            }

            for (i = 'A'; i <= 'Z'; ++i) {
                this.addSafeCharacter(i);
            }

            for (i = '0'; i <= '9'; ++i) {
                this.addSafeCharacter(i);
            }

        }

        private MyUrlEncode(BitSet safeCharacters) {
            this.encodeSpaceAsPlus = false;
            this.safeCharacters = safeCharacters;
        }

        public void addSafeCharacter(char c) {
            this.safeCharacters.set(c);
        }

        public void removeSafeCharacter(char c) {
            this.safeCharacters.clear(c);
        }

        public void setEncodeSpaceAsPlus(boolean encodeSpaceAsPlus) {
            this.encodeSpaceAsPlus = encodeSpaceAsPlus;
        }

        public String encode(String path, Charset charset) {
            int maxBytesPerChar = 10;
            StringBuilder rewrittenPath = new StringBuilder(path.length());
            ByteArrayOutputStream buf = new ByteArrayOutputStream(maxBytesPerChar);
            OutputStreamWriter writer = new OutputStreamWriter(buf, charset);

            for (int i = 0; i < path.length(); ++i) {
                int c = path.charAt(i);
                if (this.safeCharacters.get(c)) {
                    rewrittenPath.append((char) c);
                } else if (this.encodeSpaceAsPlus && c == ' ') {
                    rewrittenPath.append('+');
                } else {
                    try {
                        writer.write((char) c);
                        writer.flush();
                    } catch (IOException var14) {
                        buf.reset();
                        continue;
                    }

                    byte[] ba = buf.toByteArray();

                    for (int j = 0; j < ba.length; ++j) {
                        byte toEncode = ba[j];
                        rewrittenPath.append('%');
                        int low = toEncode & 15;
                        int high = (toEncode & 240) >> 4;
                        rewrittenPath.append(hexadecimal[high]);
                        rewrittenPath.append(hexadecimal[low]);
                    }

                    buf.reset();
                }
            }

            return rewrittenPath.toString();
        }

        public Object clone() {
            MyUrlEncode result = new MyUrlEncode((BitSet) this.safeCharacters.clone());
            result.setEncodeSpaceAsPlus(this.encodeSpaceAsPlus);
            return result;
        }

        static {
            DEFAULT.addSafeCharacter('-');
            DEFAULT.addSafeCharacter('.');
            DEFAULT.addSafeCharacter('_');
            DEFAULT.addSafeCharacter('~');
            DEFAULT.addSafeCharacter('!');
            DEFAULT.addSafeCharacter('$');
            DEFAULT.addSafeCharacter('&');
            DEFAULT.addSafeCharacter('\'');
            DEFAULT.addSafeCharacter('(');
            DEFAULT.addSafeCharacter(')');
            DEFAULT.addSafeCharacter('*');
            DEFAULT.addSafeCharacter('+');
            DEFAULT.addSafeCharacter(',');
            DEFAULT.addSafeCharacter(';');
            DEFAULT.addSafeCharacter('=');
            DEFAULT.addSafeCharacter(':');
            DEFAULT.addSafeCharacter('@');
            DEFAULT.addSafeCharacter('/');
            DEFAULT.addSafeCharacter('?');
            DEFAULT.addSafeCharacter('%');
            QUERY.setEncodeSpaceAsPlus(true);
            QUERY.addSafeCharacter('*');
            QUERY.addSafeCharacter('-');
            QUERY.addSafeCharacter('.');
            QUERY.addSafeCharacter('_');
            QUERY.addSafeCharacter('=');
            QUERY.addSafeCharacter('&');
        }
    }
}