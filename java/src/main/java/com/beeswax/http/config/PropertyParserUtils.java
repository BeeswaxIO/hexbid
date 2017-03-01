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

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for parsing property file.
 *
 */
public class PropertyParserUtils {

  private static final Logger LOGGER = LogManager.getLogger(PropertyParserUtils.class);

  /**
   * Load properties from a file path.
   * 
   * @param propFile
   * @return Properties
   * @throws IOException
   */
  public static Properties loadFromProperties(final String propFile) throws IOException {
    LOGGER.debug("Loading properties from {}", propFile);
    final Properties properties = new Properties();
    properties.load(new FileReader(propFile));
    return properties;
  }

  /**
   * Load properties from Inputstream.
   * 
   * @param inputStream
   * @return Properties
   * @throws IOException
   */
  public static Properties loadFromProperties(final InputStream inputStream) throws IOException {
    LOGGER.debug("Loading properties from inputstream");
    final Properties properties = new Properties();
    properties.load(inputStream);
    return properties;
  }

  /**
   * Returns String property value if exists. Else throws {@link IllegalArgumentException}
   */
  public static String getStringProperty(String property, Properties properties) {
    if (!properties.containsKey(property)) {
      throw new IllegalArgumentException("Missing property: " + property);
    }
    return properties.getProperty(property);
  }

  /**
   * Returns Integer property value if exists. Else return default value.
   * 
   * @param property
   * @param defaultValue
   * @param properties
   * @return int
   */
  public static int getIntegerProperty(String property, int defaultValue, Properties properties) {
    String propertyValue = properties.getProperty(property, Integer.toString(defaultValue));
    try {
      return Integer.parseInt(propertyValue.trim());
    } catch (NumberFormatException e) {
      LOGGER.error(e);
      return defaultValue;
    }
  }

  /**
   * Returns Long property value if exists. Else return default value.
   * 
   * @param property
   * @param defaultValue
   * @param properties
   * @return long
   */
  public static long getLongProperty(String property, long defaultValue, Properties properties) {
    final String propertyValue = properties.getProperty(property, Long.toString(defaultValue));
    try {
      return Long.parseLong(propertyValue.trim());
    } catch (NumberFormatException e) {
      LOGGER.error(e);
      return defaultValue;
    }
  }

  /**
   * Helper method used to parse boolean properties.
   *
   * @param property The String key for the property
   * @param defaultValue The default value for the boolean property
   * @param properties The properties file to get property from
   * @return property from property file, or if it is not specified, the default value
   */
  public static boolean parseBoolean(String property, boolean defaultValue, Properties properties) {
    return Boolean.parseBoolean(properties.getProperty(property, Boolean.toString(defaultValue)));
  }

}