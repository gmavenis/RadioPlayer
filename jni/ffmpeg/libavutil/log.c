/*
 * log functions
 * Copyright (c) 2003 Michel Bardiaux
 *
 * This file is part of FFmpeg.
 *
 * FFmpeg is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * FFmpeg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with FFmpeg; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

/**
 * @file
 * logging functions
 */

#include <unistd.h>
#include <stdlib.h>
#include "avutil.h"
#include "log.h"
#include "config.h"

#if OS_ANDROID
#include <android/log.h>
#endif

#if LIBAVUTIL_VERSION_MAJOR > 50
static
#endif
int av_log_level = AV_LOG_INFO;

static int use_ansi_color=-1;

#if OS_ANDROID
static int android_levels[7] = { 
    ANDROID_LOG_FATAL,
    ANDROID_LOG_FATAL,
    ANDROID_LOG_ERROR,
    ANDROID_LOG_WARN,
    ANDROID_LOG_INFO,
    ANDROID_LOG_VERBOSE,
    ANDROID_LOG_DEBUG
};
#endif

#undef fprintf
static void colored_fputs(int color, const char *str){
    if(use_ansi_color<0){
#if HAVE_ISATTY && !defined(_WIN32)
        use_ansi_color= getenv("TERM") && !getenv("NO_COLOR") && isatty(2);
#else
        use_ansi_color= 0;
#endif
    }

    if(use_ansi_color){
        fprintf(stderr, "\033[%d;3%dm", color>>4, color&15);
    }
    fputs(str, stderr);
    if(use_ansi_color){
        fprintf(stderr, "\033[0m");
    }
}

void av_log_default_callback(void* ptr, int level, const char* fmt, va_list vl)
{
    static int print_prefix=1;
    static int count;
    static char line[1024], prev[1024];
#if OS_ANDROID
    static int prev_android_level;
#endif
    static const uint8_t color[]={0x41,0x41,0x11,0x03,9,9,9};
    AVClass* avc= ptr ? *(AVClass**)ptr : NULL;

    if(level>av_log_level)
        return;
#undef fprintf
    if(print_prefix && avc) {
        snprintf(line, sizeof(line), "[%s @ %p]", avc->item_name(ptr), ptr);
    }else
        line[0]=0;

    vsnprintf(line + strlen(line), sizeof(line) - strlen(line), fmt, vl);

    print_prefix= line[strlen(line)-1] == '\n';
    if(print_prefix && !strcmp(line, prev)){
        count++;
        return;
    }
    if(count>0){
#if OS_ANDROID
        __android_log_print( prev_android_level, "FF-MPEG", "    Last message repeated %d times\n", count);
#else
        fprintf(stderr, "    Last message repeated %d times\n", count);
#endif
        count=0;
    }
#if OS_ANDROID
    prev_android_level = android_levels[av_clip(level>>3,0,6)];
    __android_log_print( prev_android_level, "FF-MPEG", line);
#else
    colored_fputs(color[av_clip(level>>3, 0, 6)], line);
#endif
    strcpy(prev, line);
}

static void (*av_log_callback)(void*, int, const char*, va_list) = av_log_default_callback;

void av_log(void* avcl, int level, const char *fmt, ...)
{
    va_list vl;
    va_start(vl, fmt);
    av_vlog(avcl, level, fmt, vl);
    va_end(vl);
}

void av_vlog(void* avcl, int level, const char *fmt, va_list vl)
{
    av_log_callback(avcl, level, fmt, vl);
}

int av_log_get_level(void)
{
    return av_log_level;
}

void av_log_set_level(int level)
{
    av_log_level = level;
}

void av_log_set_callback(void (*callback)(void*, int, const char*, va_list))
{
    av_log_callback = callback;
}
