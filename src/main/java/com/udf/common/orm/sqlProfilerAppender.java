package com.udf.common.orm;

import ch.qos.logback.classic.net.SocketAppender;
import org.springframework.stereotype.Component;

/**
 * Created by zwr on 2015/2/17.
 */
@Component
public class sqlProfilerAppender extends SocketAppender{
    private int port = 4445;
    private String remoteHost = "localhost";
}
