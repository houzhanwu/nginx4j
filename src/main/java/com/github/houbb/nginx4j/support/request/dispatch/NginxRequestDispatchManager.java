package com.github.houbb.nginx4j.support.request.dispatch;

import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.houbb.nginx4j.config.NginxConfig;
import com.github.houbb.nginx4j.config.NginxGzipConfig;
import com.github.houbb.nginx4j.constant.EnableStatusEnum;
import com.github.houbb.nginx4j.constant.NginxConst;
import com.github.houbb.nginx4j.support.request.dispatch.http.NginxRequestDispatches;
import com.github.houbb.nginx4j.support.server.NginxServerSocket;
import com.github.houbb.nginx4j.util.InnerMimeUtil;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.File;
import java.util.List;


/**
 * 静态资源
 * @author 老马啸西风
 * @since 0.2.0
 */
public class NginxRequestDispatchManager implements NginxRequestDispatch {

    private static final Log log = LogFactory.getLog(NginxServerSocket.class);

    /**
     * 内容的分发处理
     *
     * @param context 上下文
     */
    public void dispatch(NginxRequestDispatchContext context) {
        final NginxRequestDispatch dispatch = getDispatch(context);

        dispatch.dispatch(context);
    }

    protected NginxRequestDispatch getDispatch(NginxRequestDispatchContext context) {
        final FullHttpRequest requestInfoBo = context.getRequest();
        final NginxConfig nginxConfig = context.getNginxConfig();

        // 消息解析不正确
        /*如果无法解码400*/
        if (!requestInfoBo.decoderResult().isSuccess()) {
            return NginxRequestDispatches.http400();
        }

        // 文件
        File targetFile = getTargetFile(requestInfoBo, nginxConfig);
        // 是否存在
        if(targetFile.exists()) {
            // 设置文件
            context.setFile(targetFile);

            // 如果是文件夹
            if(targetFile.isDirectory()) {
                return NginxRequestDispatches.fileDir();
            }

            // range ?
            boolean isRangeRequest = isRangeRequest(requestInfoBo, nginxConfig);
            if(isRangeRequest) {
                return NginxRequestDispatches.fileRange();
            }

            long fileSize = targetFile.length();

            // 是否需要压缩
            boolean needCompress = isNeedCompress(targetFile, requestInfoBo, nginxConfig);
            if(needCompress) {
               return NginxRequestDispatches.fileCompress();
            }

            // 小文件
            if(fileSize <= NginxConst.BIG_FILE_SIZE) {
                return NginxRequestDispatches.fileSmall();
            }
            // 大文件
            return NginxRequestDispatches.fileBig();
        }  else {
            return NginxRequestDispatches.http404();
        }
    }

    /**
     * 是否需要压缩
     *
     *         gzip on;
     *         gzip_vary on;
     *         gzip_proxied any;
     *         gzip_comp_level 5;
     *         gzip_min_length 256;
     *         gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;
     *
     * @param request 请求
     * @param nginxConfig 配置
     * @return 结果
     */
    private boolean isNeedCompress(final File targetFile,
                                   final FullHttpRequest request,
                                   final NginxConfig nginxConfig) {
        final NginxGzipConfig gzipConfig = nginxConfig.getNginxGzipConfig();

        if(EnableStatusEnum.ON.getCode().equalsIgnoreCase(gzipConfig.getGzip())) {
            // 大小
            long configSize = gzipConfig.getGzipMinLength();
            long fileLength = targetFile.length();
            if(fileLength < configSize) {
                return false;
            }

            // 文件类别
            List<String> configContentTypeList = gzipConfig.getGzipTypes();
            String contentType = InnerMimeUtil.getContentType(targetFile);
            if(!configContentTypeList.contains(contentType)) {
                return false;
            }

            // 真
            return true;
        }


        return false;
    }

    /**
     * 是否为范围查询
     * @param request 请求
     * @param nginxConfig 配置
     * @return 结果
     * @since 0.7.0
     */
    protected boolean isRangeRequest(final FullHttpRequest request, final NginxConfig nginxConfig) {
        // 解析Range头
        String rangeHeader = request.headers().get("Range");
        return StringUtil.isNotEmpty(rangeHeader);
    }

    protected File getTargetFile(final FullHttpRequest request, final NginxConfig nginxConfig) {
        boolean isRootPath = isRootPath(request, nginxConfig);
        // 根路径
        if(isRootPath) {
            log.info("[Nginx] current req meet root path");
            return nginxConfig.getNginxIndexContent().getIndexFile(nginxConfig);
        }

        final String basicPath = nginxConfig.getHttpServerRoot();
        final String path = request.uri();

        // other
        String fullPath = FileUtil.buildFullPath(basicPath, path);
        return new File(fullPath);
    }

    protected boolean isRootPath(final FullHttpRequest request, final NginxConfig nginxConfig) {
        final String path = request.uri();

        //root path
        if(StringUtil.isEmpty(path) || "/".equals(path)) {
            return true;
        }
        return false;
    }

}
