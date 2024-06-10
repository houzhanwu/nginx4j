package com.github.houbb.nginx4j.support.placeholder.impl;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.houbb.nginx4j.support.placeholder.AbstractNginxPlaceholderRequest;
import com.github.houbb.nginx4j.support.request.dispatch.NginxRequestDispatchContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * 同 `$uri`。
 * @since 0.19.0
 *
 * @author 老马啸西风
 */
public class NginxPlaceholderDocumentUri extends AbstractNginxPlaceholderRequest {

    private static final Log logger = LogFactory.getLog(NginxPlaceholderDocumentUri.class);


    @Override
    protected Object extractBeforeDispatch(FullHttpRequest request, NginxRequestDispatchContext context) {
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        return decoder.path();
    }

    @Override
    protected String getKeyBeforeDispatch(FullHttpRequest request, NginxRequestDispatchContext context) {
        return "$document_uri";
    }

}
