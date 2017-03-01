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
package com.beeswax.http.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * Global handler handles all HTTP requests received by the server.</br>
 * It routes incoming HTTP request to appropriate handler based on request path.</br>
 * </br>
 * GlobalHandler is designed to handle *stateless* HTTP requests so a single instance can be shared in
 * {@link ChannelPipeline}.</br>
 * </br>
 * Full HTTP response may have following error codes:</br>
 * 200 if it processed request successfully.</br>
 * 204 if request has no content.</br>
 * 400 if there is a {@link IllegalArgumentException}.</br>
 * 500 if there is an internal error.</br>
 * 
 */
@io.netty.channel.ChannelHandler.Sharable
public class GlobalHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
  private static final Logger LOGGER = LogManager.getLogger(GlobalHandler.class);
  private final HandlerFactory handlerFactory;

  public GlobalHandler(HandlerFactory handlerFactory) {
    this.handlerFactory = handlerFactory;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
    final QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
    LOGGER.debug("path: {}", queryDecoder.path());

    // trim trailing backslash so that the handler can recognize /PATH/
    final RequestHandler handler = handlerFactory.getHandler(queryDecoder.path()
                                                                         .replaceAll("/$", ""));
    final FullHttpResponse response = handler.processRequest(ctx, request);
    response.headers()
            .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
            .set(HttpHeaderNames.CONTENT_LENGTH, response.content()
                                                         .readableBytes());

    ctx.writeAndFlush(response);
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, Throwable cause) {
    try {
      LOGGER.error("Exception occurred. Returning empty `500` response", cause);
      final FullHttpResponse errResponse;
      if (cause instanceof IllegalArgumentException) {
        errResponse =
            new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
      } else {
        errResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
            HttpResponseStatus.INTERNAL_SERVER_ERROR);
      }

      errResponse.headers()
                 .set(HttpHeaderNames.CONTENT_LENGTH, errResponse.content()
                                                                 .readableBytes());
      ctx.writeAndFlush(errResponse);
    } catch (Throwable t) {
      LOGGER.error("Error occured when returning empty `500` response", t);
    }
  }
}
