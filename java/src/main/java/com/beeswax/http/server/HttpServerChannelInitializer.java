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
package com.beeswax.http.server;

import com.beeswax.http.config.ServerConfig;
import com.beeswax.http.handler.GlobalHandler;
import com.beeswax.http.handler.HandlerFactory;
import com.google.common.annotations.VisibleForTesting;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Initialize Pipeline Channel for HTTP Server.
 *
 */
public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {
  private final ServerConfig serverConfig;
  private final GlobalHandler globalHandler;

  public HttpServerChannelInitializer(ServerConfig serverConfig, HandlerFactory handlerFactory) {
    this(serverConfig, new GlobalHandler(handlerFactory));
  }

  @VisibleForTesting
  private HttpServerChannelInitializer(ServerConfig config, GlobalHandler handler) {
    serverConfig = config;
    globalHandler = handler;
  }

  /**
   * Configure the channel pipeline with the following handler in order:</br>
   * </br>
   * {@link HttpServerCodec} - decodes/encodes {@link ByteBuf} into/from {@link HttpRequest} and
   * {@link HttpContent}</br>
   * {@link HttpObjectAggregator} - aggregates an {@link HttpMessage} and its following {@link HttpContent}s into a
   * single {@link FullHttpRequest} or {@link FullHttpResponse}.</br>
   * {@link GlobalHandler} - handles request processing for all end points.</br>
   */
  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ch.pipeline()
      .addLast("codec", new HttpServerCodec())
      .addLast("http-aggregator", new HttpObjectAggregator(serverConfig.maxRequestSize))
      .addLast("global-handler", this.globalHandler);
  }
}
