/*
 * Copyright (c) 2012-2013, The Linux Foundation. All rights reserved.
 * Not a Contribution.
 * Copyright (C) 2008 The Android Open Source Project, 2016 NikitaProAndroid
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

#ifndef ANDROID_HOOKJNICOMPAT_H_
#define ANDROID_HOOKJNICOMPAT_H_

#include <utils/Errors.h>
#include <utils/Mutex.h>
#include <utils/String8.h>

namespace android {

class String8;

class LGAudioSystem
{
public:
    status_t    setRingerMode(int a2);
    status_t    setMABLEnable(int a2);
    status_t    setMABLControl(int a2, int a3);
    String8     getParameters(const String8& a2);
    status_t     setParameters(const String8& a2);
};

class AudioPlaybackHook
{
public:
    status_t    getMixerOutputFormat(int a2);
    void        getMixerOutput();
    status_t    getMixerSampleRate(int a2);
    status_t    bufferSize(int a2);
    void        start();
    void        stop();
    status_t    set(uint32_t a2, uint32_t a3, void (*a4)(int a5, void *a6, void *a7), void *a8);
    void        initCheck() const;
    AudioPlaybackHook();

protected:
    virtual ~AudioPlaybackHook();
};

class MediaRecorder
{
public:
    status_t    setRecordZoomEnable(int a2, int a3);
    status_t    setRecordAngle(int a2);
    status_t    changeMaxFileSize(long long a2);
    void        setaudiozoom();
    void        setAudioZoomExceptionCase();
    void        resume();
};

class MediaPlayer
{
public:
    void        screenCapture();
};

class AudioRecord
{
public:
    status_t    setRecordZoomEnable(int a, int b);
    status_t    setRecordAngle(int c);
};

class AudioSystem
{
public:
    status_t    setRecordHookingEnabled(int a2, int a3, int a4);
};

class Camera
{
public:
    void        cancelPicture();
};

};  // namespace android

#endif  /*ANDROID_HOOKJNICOMPAT_H_*/
