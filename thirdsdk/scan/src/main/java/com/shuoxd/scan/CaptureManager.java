/*
 * Copyright (C) 2019 Jenly Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shuoxd.scan;


import com.growatt.scan.camera.CameraManager;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface CaptureManager {

    /**
     * Get {@link CameraManager}
     *
     * @return {@link CameraManager}
     */
    CameraManager getCameraManager();

    /**
     * Get {@link com.growatt.scan.BeepManager}
     *
     * @return {@link com.growatt.scan.BeepManager}
     */
    com.growatt.scan.BeepManager getBeepManager();

    /**
     * Get {@link com.growatt.scan.AmbientLightManager}
     *
     * @return {@link com.growatt.scan.AmbientLightManager}
     */
    com.growatt.scan.AmbientLightManager getAmbientLightManager();

    /**
     * Get {@link com.growatt.scan.InactivityTimer}
     *
     * @return {@link com.growatt.scan.InactivityTimer}
     */
    com.growatt.scan.InactivityTimer getInactivityTimer();
}
