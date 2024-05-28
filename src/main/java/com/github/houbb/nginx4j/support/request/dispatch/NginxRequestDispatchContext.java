package com.github.houbb.nginx4j.support.request.dispatch;

import com.github.houbb.nginx4j.config.NginxConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.File;

/**
 * 分发上下文
 * @since 0.6.0
 */
public class NginxRequestDispatchContext {

    private ChannelHandlerContext ctx;

    private FullHttpRequest request;

    private NginxConfig nginxConfig;

    private File file;

    /**
     * 确切的文件长度
     * @since 0.10.0
     */
    private long actualFileLength;

    /**
     * 确切的开始位置
     * @since 0.10.0
     */
    private long actualStart;

    public long getActualStart() {
        return actualStart;
    }

    public void setActualStart(long actualStart) {
        this.actualStart = actualStart;
    }

    public long getActualFileLength() {
        return actualFileLength;
    }

    public void setActualFileLength(long actualFileLength) {
        this.actualFileLength = actualFileLength;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }

    public NginxConfig getNginxConfig() {
        return nginxConfig;
    }

    public void setNginxConfig(NginxConfig nginxConfig) {
        this.nginxConfig = nginxConfig;
    }

    @Override
    public String toString() {
        return "NginxRequestDispatchContext{" +
                "ctx=" + ctx +
                ", request=" + request +
                ", nginxConfig=" + nginxConfig +
                '}';
    }

}
