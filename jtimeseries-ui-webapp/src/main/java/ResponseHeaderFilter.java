/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
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
