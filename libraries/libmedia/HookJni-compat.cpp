/*
 * Copyright (C) 2006-2007 The Android Open Source Project, 2016 NikitaProAndroid
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

#define LOG_TAG "hook_jni-compat"
//#define LOG_NDEBUG 0

#include <utils/Log.h>
#include "HookJni-compat.h"

// ----------------------------------------------------------------------------

namespace android {

status_t LGAudioSystem::setParameters(const String8& a2)
{
    ALOGW("hook_jni stub: LGAudioSystem::setParameters");
    return NO_ERROR;
}

String8 LGAudioSystem::getParameters(const String8& a2)
{
    ALOGW("hook_jni stub: LGAudioSystem::getParameters");
    String8 result = String8("");
    return result;
}

status_t LGAudioSystem::setRingerMode(int a2)
{
    ALOGW("hook_jni stub: LGAudioSystem::setRingerMode");
    return NO_ERROR;
}

status_t LGAudioSystem::setMABLEnable(int a2)
{
    ALOGW("hook_jni stub: LGAudioSystem::setMABLEnable");
    return NO_ERROR;
}

status_t LGAudioSystem::setMABLControl(int a2, int a3)
{
    ALOGW("hook_jni stub: LGAudioSystem::setMABLControl");
    return NO_ERROR;
}

status_t AudioPlaybackHook::getMixerOutputFormat(int a2)
{
    ALOGW("hook_jni stub: AudioPlaybackHook::getMixerOutputFormat");
    return NO_ERROR;
}

void AudioPlaybackHook::getMixerOutput()
{
    ALOGW("hook_jni stub: AudioPlaybackHook::getMixerOutput");
}

status_t AudioPlaybackHook::getMixerSampleRate(int a2)
{
    ALOGW("hook_jni stub: AudioPlaybackHook::getMixerSampleRate");
    return NO_ERROR;
}

status_t AudioPlaybackHook::bufferSize(int a2)
{
    ALOGW("hook_jni stub: AudioPlaybackHook::bufferSize");
    return NO_ERROR;
}

void AudioPlaybackHook::stop()
{
    ALOGW("hook_jni stub: AudioPlaybackHook::stop");
}

void AudioPlaybackHook::start()
{
    ALOGW("hook_jni stub: AudioPlaybackHook::start");
}

void AudioPlaybackHook::initCheck() const
{
    ALOGW("hook_jni stub: AudioPlaybackHook::initCheck");
}

AudioPlaybackHook::AudioPlaybackHook()
{
    ALOGW("hook_jni stub: AudioPlaybackHook::AudioPlaybackHook");
}

AudioPlaybackHook::~AudioPlaybackHook()
{
    ALOGW("hook_jni stub: AudioPlaybackHook::~AudioPlaybackHook");
}

status_t AudioPlaybackHook::set(uint32_t a2, uint32_t a3, void (*a4)(int a5, void *a6, void *a7), void *a8)
{
    ALOGW("hook_jni stub: AudioPlaybackHook::set");
    return NO_ERROR;
}

status_t MediaRecorder::setRecordZoomEnable(int a2, int a3)
{
    ALOGW("hook_jni stub: MediaRecorder::setRecordZoomEnable");
    return NO_ERROR;
}

status_t MediaRecorder::setRecordAngle(int a2)
{
    ALOGW("hook_jni stub: MediaRecorder::setRecordAngle");
    return NO_ERROR;
}

status_t MediaRecorder::changeMaxFileSize(long long a2)
{
    ALOGW("hook_jni stub: MediaRecorder::changeMaxFileSize");
    return NO_ERROR;
}

void MediaRecorder::setaudiozoom()
{
    ALOGW("hook_jni stub: MediaRecorder::setaudiozoom");
}

void MediaRecorder::setAudioZoomExceptionCase()
{
    ALOGW("hook_jni stub: MediaRecorder::setAudioZoomExceptionCase");
}

void MediaRecorder::resume()
{
    ALOGW("hook_jni stub: MediaRecorder::resume");
}

void MediaPlayer::screenCapture()
{
     ALOGW("hook_jni stub: MediaPlayer::screenCapture");
}

status_t AudioRecord::setRecordZoomEnable(int a2, int a3)
{
    ALOGW("hook_jni stub: AudioRecord::setRecordZoomEnable");
    return NO_ERROR;
}

status_t AudioRecord::setRecordAngle(int a2)
{
    ALOGW("hook_jni stub: AudioRecord::setRecordAngle");
    return NO_ERROR;
}

status_t AudioSystem::setRecordHookingEnabled(int a2, int a3, int a4)
{
    ALOGW("hook_jni stub: AudioSystem::setRecordHookingEnabled");
    return NO_ERROR;
}

void Camera::cancelPicture()
{
    ALOGW("hook_jni stub: Camera::cancelPicture");
}

extern "C" void _ZN9SkLGMovie6bitmapEv()
{
    ALOGW("hook_jni stub: SkLGMovie::bitmap");
}

extern "C" void _ZN9SkLGMovie7setTimeEj()
{
    ALOGW("hook_jni stub: SkLGMovie::setTime");
}

extern "C" void _ZN9SkLGMovie8durationEv()
{
    ALOGW("hook_jni stub: SkLGMovie::duration");
}

extern "C" void _ZN9SkLGMovie8isOpaqueEv()
{
    ALOGW("hook_jni stub: SkLGMovie::isOpaque");
}

extern "C" void _ZN9SkLGMovie6heightEv()
{
    ALOGW("hook_jni stub: SkLGMovie::height");
}

extern "C" void _ZN9SkLGMovie5widthEv()
{
    ALOGW("hook_jni stub: SkLGMovie::duration");
}

extern "C" void _ZN9SkLGMovie12DecodeMemoryEPKvj()
{
    ALOGW("hook_jni stub: SkLGMovie::DecodeMemory");
}

extern "C" void _ZN9SkLGMovie12DecodeStreamEP18SkStreamRewindable()
{
    ALOGW("hook_jni stub: SkLGMovie::DecodeStream");
}

}; // namespace android
