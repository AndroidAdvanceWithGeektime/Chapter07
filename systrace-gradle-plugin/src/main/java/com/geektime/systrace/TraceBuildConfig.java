/*
 * Tencent is pleased to support the open source community by making wechat-matrix available.
 * Copyright (C) 2018 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geektime.systrace;

import com.geektime.systrace.retrace.MappingCollector;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by caichongyang on 2017/6/20.
 */
public class TraceBuildConfig {
    private static final String TAG = "Matrix.TraceBuildConfig";
    private final String mPackageName;
    private final String mMappingPath;
    private final String mBaseMethodMap;
    private final String mMethodMapFile;
    private final String mIgnoreMethodMapFile;

    private final String mBlackListDir;
    private final HashSet<String> mBlackClassMap;
    private final HashSet<String> mBlackPackageMap;

    public TraceBuildConfig(String packageName, String mappingPath, String baseMethodMap, String methodMapFile, String ignoreMethodMapFile, String blackListFile) {
        mPackageName = packageName;
        mMappingPath = mappingPath;
        mBaseMethodMap = baseMethodMap;
        mMethodMapFile = methodMapFile;
        mIgnoreMethodMapFile = ignoreMethodMapFile;
        mBlackListDir = blackListFile;
        mBlackClassMap = new HashSet();
        mBlackPackageMap = new HashSet();
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getMappingPath() {
        return mMappingPath;
    }

    public String getBaseMethodMap() {
        return mBaseMethodMap;
    }

    public String getMethodMapFile() {
        return mMethodMapFile;
    }

    public String getBlackListFile() {
        return mBlackListDir;
    }


    public String getIgnoreMethodMapFile() {
        return mIgnoreMethodMapFile;
    }

    @Override
    public String toString() {
        return "\n" + "PackageName:" + mPackageName + "\n" + "MappingPath:" + mMappingPath + "\n" + "MethodMapFile:" + mMethodMapFile;
    }

    /**
     * whether it need to trace by class filename
     * @param fileName
     * @return
     */
    public boolean isNeedTraceClass(String fileName) {
        boolean isNeed = true;
        if (fileName.endsWith(".class")) {
            for (String unTraceCls : TraceBuildConstants.UN_TRACE_CLASS) {
                if (fileName.contains(unTraceCls)) {
                    isNeed = false;
                    break;
                }
            }
        } else {
            isNeed = false;
        }
        return isNeed;
    }

    /**
     * whether it need to trace.
     * if this class in collected set,it return true.
     * @param clsName
     * @param mappingCollector
     * @return
     */
    public boolean isNeedTrace(String clsName, MappingCollector mappingCollector) {
        boolean isNeed = true;
        if (mBlackClassMap.contains(clsName)) {
            isNeed = false;
        } else {
            if (null != mappingCollector) {
                clsName = mappingCollector.originalClassName(clsName, clsName);
            }
            for (String packageName : mBlackPackageMap) {
                if (clsName.startsWith(packageName.replaceAll("/", "."))) {
                    isNeed = false;
                    break;
                }
            }
        }
        return isNeed;
    }

    public boolean isMethodBeatClass(String className, HashMap<String, String> mCollectedClassExtendMap) {
        className = className.replace(".", "/");
        boolean isApplication = className.equals(TraceBuildConstants.MATRIX_TRACE_METHOD_BEAT_CLASS);
        if (isApplication) {
            return true;
        } else if (mCollectedClassExtendMap.containsKey(className)) {
            return mCollectedClassExtendMap.get(className).equals(TraceBuildConstants.MATRIX_TRACE_METHOD_BEAT_CLASS);
        } else {
            return false;
        }
    }

    /**
     * parse the BlackFile in order to pass some class/method
     * @param processor
     */
    public void parseBlackFile(MappingCollector processor) {
        File blackConfigFile = new File(mBlackListDir);
        if (!blackConfigFile.exists()) {
            Log.w(TAG, "black config file not exist %s", blackConfigFile.getAbsoluteFile());
        }
        String blackStr = TraceBuildConstants.DEFAULT_BLACK_TRACE + Util.readFileAsString(blackConfigFile.getAbsolutePath());

        String[] blackArray = blackStr.split("\n");

        if (blackArray != null) {
            for (String black : blackArray) {
                black = black.trim().replace("/", ".");
                if (black.length() == 0) {
                    continue;
                }
                if (black.startsWith("#")) {
                    Log.i(TAG, "[parseBlackFile] comment:%s", black);
                    continue;
                }
                if (black.startsWith("[")) {
                    continue;
                }

                if (black.startsWith("-keepclass ")) {
                    black = black.replace("-keepclass ", "");
                    mBlackClassMap.add(processor.proguardClassName(black, black));

                } else if (black.startsWith("-keeppackage ")) {
                    black = black.replace("-keeppackage ", "");
                    mBlackPackageMap.add(black);
                }

            }
        }

        Log.i(TAG, "[parseBlackFile] BlackClassMap size:%s BlackPrefixMap size:%s", mBlackClassMap.size(), mBlackPackageMap.size());
    }

    public static class Builder {

        public String mPackageName;
        public String mMappingPath;
        public String mBaseMethodMap;
        public String mMethodMapFile;
        public String mIgnoreMethodMapFile;
        public String mBlackListFile;

        public Builder setPackageName(String packageName) {
            mPackageName = packageName;
            return this;
        }

        public Builder setMappingPath(String mappingPath) {
            mMappingPath = mappingPath;
            return this;
        }

        public Builder setBaseMethodMap(String baseMethodMap) {
            mBaseMethodMap = baseMethodMap;
            return this;
        }

        public Builder setMethodMapDir(String methodMapDir) {
            mMethodMapFile = methodMapDir;
            return this;
        }

        public Builder setIgnoreMethodMapDir(String methodMapDir) {
            mIgnoreMethodMapFile = methodMapDir;
            return this;
        }

        public Builder setBlackListFile(String blackListFile) {
            mBlackListFile = blackListFile;
            return this;
        }

        public TraceBuildConfig build() {
            return new TraceBuildConfig(mPackageName, mMappingPath, mBaseMethodMap, mMethodMapFile, mIgnoreMethodMapFile, mBlackListFile);
        }

    }


}
