package com.od.jtimeseries.server.util;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.BeansException;

import java.util.Properties;
import java.util.Map;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 01-Dec-2009
 * Time: 19:01:28
 *
 * Log the properties loaded from the property file
 */
public class LoggingPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private static LogMethods m = LogUtils.getLogMethods(LoggingPropertyPlaceholderConfigurer.class);

    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        for ( Map.Entry<Object,Object> e : props.entrySet()) {
            m.logInfo("Property-->'" + e.getKey() + "'='" + e.getValue() + "'");
        }
    }

}
