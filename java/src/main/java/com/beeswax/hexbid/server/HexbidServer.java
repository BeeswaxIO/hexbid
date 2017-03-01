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
package com.beeswax.hexbid.server;

import com.beeswax.hexbid.handler.HexbidHandlerFactory;
import com.beeswax.http.config.ServerConfig;
import com.beeswax.http.config.ServerConfig.ServerConfigBuilder;
import com.beeswax.http.handler.HandlerFactory;
import com.beeswax.http.server.HttpServer;
import com.google.common.annotations.VisibleForTesting;

/**
 * Hexbid HTTP Server</br>
 * Refer to the {@link HexbidHandlerFactory} for the list of end points
 * currently supported by this server.
 *
 */
public class HexbidServer extends HttpServer {
  private static final String CONFIG_FILE = "config.properties";

  public HexbidServer(ServerConfig serverConfig) {
    this(serverConfig, new HexbidHandlerFactory());
  }

  @VisibleForTesting
  HexbidServer(ServerConfig serverConfig, HandlerFactory handlerFactory) {
    super(serverConfig, handlerFactory);
  }

  public void run() throws InterruptedException {
    super.run();
  }
  
  /**
   * Entry point of hexbid server.</br>
   * </br>
   * Main method starts bid server to wait/process bid requests.</br>
   * The first argument should be the property file path. It falls back to
   * default values in {@link ServerConfig} if the file/property is absent.
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {

    final ServerConfigBuilder configBuilder = new ServerConfigBuilder();
    final ServerConfig serverConfig = configBuilder.fromProperties(CONFIG_FILE).build();

    new HexbidServer(serverConfig).run();
  }
}
