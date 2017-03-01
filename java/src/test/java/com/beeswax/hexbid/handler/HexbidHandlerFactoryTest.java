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

import org.junit.Assert;
import org.junit.Test;

import com.beeswax.hexbid.handler.BidHandler;
import com.beeswax.hexbid.handler.DefaultHandler;
import com.beeswax.hexbid.handler.HealthHandler;
import com.beeswax.hexbid.handler.VarHandler;

public class HexbidHandlerFactoryTest {

  @Test
  public void getHandlerTest() {
    HexbidHandlerFactory factory = new HexbidHandlerFactory();
    Assert.assertTrue(factory.getHandler("/bid") instanceof BidHandler);
    Assert.assertTrue(factory.getHandler("/health") instanceof HealthHandler);
    Assert.assertTrue(factory.getHandler("/var") instanceof VarHandler);
    Assert.assertTrue(factory.getHandler("random") instanceof DefaultHandler);
  }
}
