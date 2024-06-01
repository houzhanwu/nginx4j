package com.github.houbb.nginx4j.support.index;

import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.heaven.util.io.ResourceUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.houbb.nginx4j.config.NginxUserServerConfig;
import com.github.houbb.nginx4j.support.request.dispatch.NginxRequestDispatchContext;

import java.io.File;
import java.util.List;

public class NginxIndexFileDefault implements NginxIndexFile {

    private static final Log log = LogFactory.getLog(NginxIndexFileDefault.class);

    @Override
    public File getIndexFile(NginxRequestDispatchContext context) {
        return getIndexContentBytes(context);
    }

    public File getIndexContentBytes(NginxRequestDispatchContext context) {
        final NginxUserServerConfig nginxUserServerConfig = context.getCurrentNginxUserServerConfig();

        List<String> indexHtmlList = nginxUserServerConfig.getHttpServerIndexList();
        String basicPath = nginxUserServerConfig.getHttpServerRoot();
        for(String indexHtml : indexHtmlList) {
            String fullPath = FileUtil.buildFullPath(basicPath, indexHtml);

            File file = new File(fullPath);
            if(file.exists()) {
                log.info("[Nginx4j] indexFile match define path={}", file.getAbsolutePath());
                return file;
            }
        }

        // 默认
        log.info("[Nginx4j] indexFile try read default index.html");
        String resource = ResourceUtil.getClassResource(NginxIndexFileDefault.class);
        int index = resource.indexOf("/target/classes") + "/target/classes/".length();
        String basicDir = resource.substring(0, index);
        String fullIndexPath = FileUtil.buildFullPath(basicDir, "index.html");
        log.info("[Nginx4j] resource={}", fullIndexPath);
        return new File(fullIndexPath);
    }

}
