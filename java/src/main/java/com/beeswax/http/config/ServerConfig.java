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
package com.beeswax.http.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.channel.nio.NioEventLoopGroup;

/**
 * {@link ServerConfig} loads configurations from a specified property file and falls back to default
 * value when the file is missing or not found.</br>
 * </br>
 * Server Configurable values:</br>
 * </br>
 * Port</br>
 *   - This is the port that the HTTP server is listening on to accept new connections.</br>
 * </br>
 * Max request size</br>
 *   - maximum length of aggregated content in HTTP request</br>
 * </br>
 * Boss group size</br>
 *   - number of threads used by boss {@link NioEventLoopGroup} which only accepts 
 *     incoming connection and registers the connection to the worker.</br>
 * </br>
 * Backlog size</br>
 *   - the maximum queue length for incoming connection indications(a request to connect)</br>
 * </br>
 * TCP nodelay</br>
 *   - option to disable Nagle's algorithm to achieve lower latency on every packet sent</br>
 * </br>
 * Keep alive</br>
 *   - option to enable keep-alive packets for a socket connection</br>
 */
public class ServerConfig {
  private static final Logger LOGGER = LogManager.getLogger(ServerConfig.class);
  public final int port;
  public final int maxRequestSize;
  public final int bossGroupSize;
  public final int backlogSize;
  public final boolean tcpNodelay;
  public final boolean keepAlive;

  private ServerConfig(int port, int maxRequestSize, int bossGroupSize, int backlogSize,
      boolean tcpNodelay, boolean keepAlive) {
    this.port = port;
    this.maxRequestSize = maxRequestSize;
    this.bossGroupSize = bossGroupSize;
    this.backlogSize = backlogSize;
    this.tcpNodelay = tcpNodelay;
    this.keepAlive = keepAlive;
  }

  /**
   * Builder class builds server configuration from property file.
   *
   */
  public static class ServerConfigBuilder {
    private static final int DEFAULT_PORT = 8999;
    private static final int DEFAULT_MAX_REQUEST_SIZE = 1024 * 1024; // 1MB
    private static final int DEFAULT_BOSS_GROUP_SIZE = 1;
    private static final int DEFAULT_BACKLOG_SIZE = 200;
    private static final boolean DEFAULT_TCP_NODELAY = true;
    private static final boolean DEFAULT_KEEP_ALIVE = true;

    private int port;
    private int maxRequestSize;
    private int bossGroupSize;
    private int backlogSize;
    private boolean tcpNodelay;
    private boolean keepAlive;

    public ServerConfigBuilder() {}

    /**
     * Load server configurations from property files.
     * 
     * @param propertyFile
     * @return ServerConfigBuilder
     */
    public ServerConfigBuilder fromProperties(String propertyFile) throws IOException {

      final InputStream config = ServerConfigBuilder.class.getClassLoader()
                                                          .getResourceAsStream(propertyFile);
        if (config == null) {
          LOGGER.error("Failed to load server config file [{}]", propertyFile);
          throw new IOException(String.format("Failed to load server config file [%s]", propertyFile));
        }

        final Properties properties = PropertyParserUtils.loadFromProperties(config);
        port = PropertyParserUtils.getIntegerProperty("server.port", DEFAULT_PORT, properties);
        maxRequestSize = PropertyParserUtils.getIntegerProperty("server.max_request_size",
            DEFAULT_MAX_REQUEST_SIZE, properties);
        bossGroupSize = PropertyParserUtils.getIntegerProperty("server.boss_group_size",
            DEFAULT_BOSS_GROUP_SIZE, properties);
        backlogSize = PropertyParserUtils.getIntegerProperty("server.backlog_size",
            DEFAULT_BACKLOG_SIZE, properties);
        tcpNodelay =
            PropertyParserUtils.parseBoolean("server.tcp_nodelay", DEFAULT_TCP_NODELAY, properties);
        keepAlive =
            PropertyParserUtils.parseBoolean("server.keep_alive", DEFAULT_KEEP_ALIVE, properties);

      return this;
    }

    public ServerConfig build() {
      LOGGER.info("Port : {}", port);
      LOGGER.info("Max request size : {}", maxRequestSize);
      LOGGER.info("Boss group size : {}", bossGroupSize);
      LOGGER.info("Backlog size : {}", backlogSize);
      LOGGER.info("TCP nodelay : {}", tcpNodelay);
      LOGGER.info("Keep alive : {}", keepAlive);

      return new ServerConfig(port, maxRequestSize, bossGroupSize, backlogSize, tcpNodelay,
          keepAlive);
    }
  }
}

