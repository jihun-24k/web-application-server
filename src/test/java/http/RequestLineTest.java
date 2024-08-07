package http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import enums.HttpMethod;
import java.util.Map;
import org.junit.Test;

public class RequestLineTest{

    @Test
    public void create_method() {
        RequestLine line = new RequestLine("GET /index.html HTTP/1.1");
        assertTrue(line.getMethod().isGet());
        assertEquals("/index.html", line.getPath());

        line = new RequestLine("POST /index.html HTTP/1.1\"");
        assertTrue(line.getMethod().isPost());
        assertEquals("/index.html", line.getPath());
    }

    @Test
    public void create_path_and_params() {
        RequestLine line = new RequestLine("GET /user/create?userId=javajigi&password=password&name=JaeSung HTTP/1.1");
        assertTrue(line.getMethod().isGet());
        assertEquals("/user/create", line.getPath());
        Map<String, String> params = line.getParams();
        assertEquals(3, params.size());
    }
}