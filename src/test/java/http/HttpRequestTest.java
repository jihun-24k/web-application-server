package http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import enums.HttpMethod;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.junit.Test;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";
    private HttpMethod method;

    @Test
    public void request_GET() throws Exception {
        InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
        HttpRequest request = new HttpRequest(in);

        assertTrue(request.getMethod().isGet());
        assertEquals("/user/create", request.getPath());
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("javajigi",request.getParameter("userId"));
    }

    @Test
    public void request_POST() throws Exception {
        InputStream in = new FileInputStream(new File(testDirectory + "Http_POST.txt"));
        HttpRequest request = new HttpRequest(in);

        assertTrue(request.getMethod().isPost());
        assertEquals("/user/create", request.getPath());
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("javajigi",request.getParameter("userId"));
    }
}
