/*******************************************************************************
 * Copyright 2014-2017 BeeswaxIO Corporation.
 * Portions may be licensed to BeeswaxIO Corporation under one or more contributor license agreements.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.beeswax.hexbid.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.beeswax.http.handler.RequestHandler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * Default handler for unknown path.</br>
 * returns a 404 error to indicate that server cannot find what was requested.
 *
 */
public class DefaultHandler implements RequestHandler {
  private static final Logger LOGGER = LogManager.getLogger(DefaultHandler.class);
  private static final String CONTENT_MESSAGE = "page not found";

  public FullHttpResponse processRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
    final QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
    LOGGER.debug("Responding with 404: not found path {}", queryDecoder.path());

    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND,
        Unpooled.wrappedBuffer(CONTENT_MESSAGE.getBytes()));
  }

}
