/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sqoop.json;

import static org.apache.sqoop.json.util.ConfigInputSerialization.extractConfigList;
import static org.apache.sqoop.json.util.ConfigBundleSerialization.extractConfigParamBundle;
import static org.apache.sqoop.json.util.ConfigBundleSerialization.restoreConfigParamBundle;



import static org.apache.sqoop.json.util.ConfigInputSerialization.restoreConfigs;
import static org.apache.sqoop.json.util.ConfigInputSerialization.restoreValidator;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.sqoop.classification.InterfaceAudience;
import org.apache.sqoop.classification.InterfaceStability;
import org.apache.sqoop.json.util.ConfigInputConstants;
import org.apache.sqoop.model.MConfig;
import org.apache.sqoop.model.MDriver;
import org.apache.sqoop.model.MDriverConfig;
import org.apache.sqoop.model.MValidator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 * Json representation of the driver
 */
@InterfaceAudience.Private
@InterfaceStability.Unstable
public class DriverBean extends ConfigurableBean {

  public static final String CURRENT_DRIVER_VERSION = "1";
  static final String DRIVER_JOB_CONFIG = "job-config";

  private MDriver driver;
  private ResourceBundle driverConfigBundle;

  // for "extract"
  public DriverBean(MDriver driver, ResourceBundle bundle) {
    this.driver = driver;
    this.driverConfigBundle = bundle;
  }

  // for "restore"
  public DriverBean() {
  }

  public MDriver getDriver() {
    return driver;
  }

  public ResourceBundle getDriverConfigResourceBundle() {
    return driverConfigBundle;
  }

  @SuppressWarnings("unchecked")
  @Override
  public JSONObject extract(boolean skipSensitive) {
    JSONObject configs =
      extractConfigList(driver.getDriverConfig(), skipSensitive);

    JSONObject result = new JSONObject();
    result.put(ID, driver.getPersistenceId());
    result.put(CONFIGURABLE_VERSION, driver.getVersion());
    result.put(DRIVER_JOB_CONFIG, configs);
    result.put(ALL_CONFIGS, extractConfigParamBundle(driverConfigBundle));
    return result;
  }

  @Override
  public void restore(JSONObject jsonObject) {
    long id = JSONUtils.getLong(jsonObject, ID);
    String driverVersion = JSONUtils.getString(jsonObject, CONFIGURABLE_VERSION);
    JSONObject driverJobConfig = JSONUtils.getJSONObject(jsonObject, DRIVER_JOB_CONFIG);
    List<MConfig> driverConfigs = restoreConfigs(JSONUtils.getJSONArray(driverJobConfig, ConfigInputConstants.CONFIGS));
    List<MValidator> driverValidators = restoreValidator(JSONUtils.getJSONArray(driverJobConfig, ConfigInputConstants.CONFIG_VALIDATORS));
    driver = new MDriver(new MDriverConfig(driverConfigs, driverValidators), driverVersion);
    driver.setPersistenceId(id);
    driverConfigBundle = restoreConfigParamBundle(JSONUtils.getJSONObject(jsonObject, ALL_CONFIGS));
  }
}
