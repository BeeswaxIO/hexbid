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

/**
 * Handler which handles /var requests. to show server variables.</br>
 * </br>
 * Hexbid server doesn't have internal variables so it simply returns 200 "ok" but
 * this handler can show metrics like request count, bids and etc.</br>
 * 
 */
public class VarHandler implements RequestHandler {
  private static final Logger LOGGER = LogManager.getLogger(VarHandler.class);
  public static final String PATH = "/var";
  public static final String CONTENT_MESSAGE = "var";

  public FullHttpResponse processRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
    LOGGER.debug("/var request");

    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
        Unpooled.wrappedBuffer(CONTENT_MESSAGE.getBytes()));
  }

}
