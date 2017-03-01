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

import com.beeswax.http.handler.HandlerFactory;
import com.beeswax.http.handler.RequestHandler;
import com.google.common.collect.ImmutableMap;

/**
 * Following end points are currently supported by the Hexbid HTTP server:</br>
 *</br>
 * /bid - request to set bid price for bid agent request</br>
 * /health - request to check server health</br>
 * /var - request to check server variables</br>
 *
 */
public class HexbidHandlerFactory implements HandlerFactory {

  private final ImmutableMap<String, RequestHandler> registry;

  public HexbidHandlerFactory() {
    registry = ImmutableMap.<String, RequestHandler>builder()
                           .put(HealthHandler.PATH, new HealthHandler())
                           .put(VarHandler.PATH, new VarHandler())
                           .put(BidHandler.PATH, new BidHandler())
                           .build();
  }

  public RequestHandler getHandler(String path) {
    return registry.getOrDefault(path, new DefaultHandler());
  }
}
