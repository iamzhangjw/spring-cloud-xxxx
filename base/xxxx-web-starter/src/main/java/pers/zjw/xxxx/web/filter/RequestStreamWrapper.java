package pers.zjw.xxxx.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * HttpServletRequest input stream 包装
 * 避免 stream 只能读取一次
 *
 * @date 2022/04/04 0004 09:12
 * @author zhangjw
 */
@Slf4j
public class RequestStreamWrapper extends HttpServletRequestWrapper {
    private String charset;
    private byte[] cachedBytes;

    public RequestStreamWrapper(HttpServletRequest request) {
        super(request);
    }

    public RequestStreamWrapper(HttpServletRequest request, String charset) {
        this(request);
        this.charset = charset;
    }


    @Override
    public String getCharacterEncoding() {
        if (charset != null) {
            return charset;
        }
        String c = getRequest().getCharacterEncoding();
        return StringUtils.isEmpty(c) ? StandardCharsets.UTF_8.displayName() : c;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (cachedBytes == null) {
            cacheInputStream();
        }
        return new ByteArrayServletInputStream(cachedBytes);
    }

    private void cacheInputStream() throws IOException {
        cachedBytes = StreamUtils.copyToByteArray(super.getInputStream());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(
                            getInputStream(), getCharacterEncoding()));
    }

    @Override
    public String getHeader(String name) {
        // else return from into the original wrapped object
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> set = new HashSet<String>();

        // now add the headers from the wrapped request object
        @SuppressWarnings("unchecked")
        Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (e.hasMoreElements()) {
            // add the names of the request headers into the list
            String n = e.nextElement();
            set.add(n);
        }

        // create an enumeration from the set and return
        return Collections.enumeration(set);
    }

    private static class ByteArrayServletInputStream extends ServletInputStream {

        private ByteArrayInputStream inputStream;

        private ByteArrayServletInputStream(byte[] bytes) {
            Assert.notNull(bytes, "bytes must not be null");
            this.inputStream = new ByteArrayInputStream(bytes);
        }

        @Override
        public boolean isFinished() {
            return 0 == inputStream.available();
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

    }
}
