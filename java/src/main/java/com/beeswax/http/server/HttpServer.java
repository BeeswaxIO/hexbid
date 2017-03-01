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
import com.beeswax.http.handler.HandlerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Http Server handles HTTP requests.
 *
 */
public class HttpServer {
  private final ServerConfig serverConfig;
  private final HandlerFactory handlerFactory;

  public HttpServer(ServerConfig serverConfig, HandlerFactory handlerFactory) {
    this.serverConfig = serverConfig;
    this.handlerFactory = handlerFactory;
  }

  public void run() throws InterruptedException {
    // Create event loop groups. One for incoming connections handling and
    // second for handling actual event by workers
    final NioEventLoopGroup bossGroup = new NioEventLoopGroup(serverConfig.bossGroupSize);
    final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      ServerBootstrap bootStrap = new ServerBootstrap();
      bootStrap.group(bossGroup, workerGroup)
               .channel(NioServerSocketChannel.class)
               .handler(new LoggingHandler(LogLevel.INFO))
               // SO_BACKLOG : The maximum queue length for incoming connections.
               .option(ChannelOption.SO_BACKLOG, serverConfig.backlogSize)
               // TCP_NODELAY: option to disable Nagle's algorithm to achieve lower latency on every packet sent
               .option(ChannelOption.TCP_NODELAY, serverConfig.tcpNodelay)
               // SO_KEEPALIVE: option to enable keep-alive packets for a socket connection
               .childOption(ChannelOption.SO_KEEPALIVE, serverConfig.keepAlive)
               .childHandler(new HttpServerChannelInitializer(serverConfig, handlerFactory));

      // bind to port
      final ChannelFuture channelFuture = bootStrap.bind(serverConfig.port)
                                                   .sync();

      // Wait until the server socket is closed.
      channelFuture.channel()
                   .closeFuture()
                   .sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
