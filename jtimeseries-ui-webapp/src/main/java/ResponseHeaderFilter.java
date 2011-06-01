
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

public class ResponseHeaderFilter implements Filter
{
    FilterConfig fc;

    public void init(FilterConfig filterConfig)
    {
        fc = filterConfig;
    }

    public void destroy()
    {
        fc = null;
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        HttpServletResponse response = (HttpServletResponse) res;
        for (Enumeration e = fc.getInitParameterNames(); e.hasMoreElements();)
        {
            String headerName = (String) e.nextElement();
            response.addHeader(headerName, fc.getInitParameter(headerName));
        }
        chain.doFilter(req, response);
    }


}
