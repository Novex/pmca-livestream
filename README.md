# Sony Action Cam Livestreaming
[![let's talk shop](https://img.shields.io/discord/314235571344244777.svg?color=%237289DA&label=Discord&logo=discord&logoColor=%23FFFFFF)](https://discord.gg/MMv23Fn)

A very hacked together app to investigate whether it's possible to stream from sony action cameras to custom RTMP servers (eg. Twitch, Youtube, etc)

The current status is that this is super buggy but works enough to get an RTMP stream to a custom endpoint. Unfortunately, the stream quality is terrible (lots of pixelation/artifacts on movement) and nothing I've been able to change has made it better (help wanted!)

[![potato encoder](https://user-images.githubusercontent.com/564860/55494111-e68ab580-5685-11e9-83ff-354e2fc7d9a4.png)](https://gfycat.com/improbablethunderouschimneyswift)

## Development
This project should build at least with Android Studio 3.3.2 - contributions are very welcome! I have been testing with an FDR-x3000, but this should at least work on the AS300 as well.

Install the latest [OpenMemories-Launcher](https://github.com/ma1co/OpenMemories-Launcher/releases/latest) and the built apk using [pmca-console](https://github.com/ma1co/Sony-PMCA-RE). 

You will need to have set up wifi and possibly [dummy livestream credentials](#setting-up-dummy-livestream-credentials)

```
> pmca-console install -f OpenMemories-Launcher-release-2.0.apk
> pmca-console install -f app\build\outputs\apk\debug\app-debug.apk
```

The OpenMemories-Launcher will overwrite the ustream app - to run switch it to the livestream mode, start it and select PMCA-Livestream from the app list.

There's rudimentary debug logging displayed on screen that can be scrolled through, but it should also start an ADB server after it connects to wifi which will give access to `adb logcat`.

### Connecting to your rtmp server
The main thing you will need to do is hex edit in your rtmp endpoint into `app\src\main\jniLibs\armeabi\libUstreamLib.so`. You can use an app like [HxD](https://mh-nexus.de/en/hxd/) to do this - the address the string starts at is `0x490cc`:

![rtmp hax](https://user-images.githubusercontent.com/564860/55463072-f71a3c00-5643-11e9-8c65-ce95093491f9.png)

When the camera connects it will append `/broadcaster_live1234` to the rtmp url (where `1234` is a random four digit number), so the full endpoint will look something like `rtmp://192.168.1.198/stream/broadcaster_live1234`

The easiest way to work out what to connect your rtmp client to is to just tail your rtmp server logs.

### Setting up dummy livestream credentials
Run this command:

```
> pmca-console stream -w ustream.json
```

with a `ustream.json` which looks something like this:

```
[
    [
        "twitterEnabled",
        0
    ],
    [
        "twitterConsumerKey",
        "AAAAAAAAAAAAAAAAAAAAA"
    ],
    [
        "twitterConsumerSecret",
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    ],
    [
        "twitterAccessToken1",
        ""
    ],
    [
        "twitterAccessTokenSecret",
        ""
    ],
    [
        "twitterMessage",
        "Live Streaming from Action Cam by Sony"
    ],
    [
        "facebookEnabled",
        0
    ],
    [
        "facebookAccessToken",
        ""
    ],
    [
        "facebookMessage",
        "Live Streaming from Action Cam by Sony"
    ],
    [
        "service",
        0
    ],
    [
        "enabled",
        1
    ],
    [
        "macId",
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    ],
    [
        "macSecret",
        "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
    ],
    [
        "macIssueTime",
        "dbeaaaaa00000000"
    ],
    [
        "unknown",
        1
    ],
    [
        "channels",
        [
            12345678
        ]
    ],
    [
        "shortURL",
        "http://www.ustream.tv/channel/asdf"
    ],
    [
        "videoFormat",
        3
    ],
    [
        "supportedFormats",
        [
            1,
            3
        ]
    ],
    [
        "enableRecordMode",
        0
    ],
    [
        "videoTitle",
        "Recorded with Action Cam by Sony"
    ],
    [
        "videoDescription",
        "Shot 100% with Sony's Action Cam #SonyActionCam #ProveYourself"
    ],
    [
        "videoTag",
        ""
    ]
]
```

### Sometimes crashes will break the OpenMemories-Launcher
Sometimes when the app crashes hard, or even just exiting normally, something gets triggered as part of the recovery and the launcher will stop working (flashing a blank screen on start then going back to the normal interface).

To fix this you need to reinstall the launcher via `pmca-console updatershell` (the normal `pmca-console install` command will fail with `Communication error 100: Error completed`).

You will need to use the master branch of pmca-console, since the updatershell install command isn't in the releases yet:

```
C:\Users\Seb\Development\Sony-PMCA-RE>python pmca-console.py updatershell
Using drivers Windows-MSC, Windows-MTP
Looking for Sony devices

Querying mass storage device
Sony Camcorder is a camera in mass storage mode

Getting device info
Using firmware for model FDR-X3000

Initializing firmware update
Switching to updater mode

Waiting for camera to switch...
Please follow the instructions on the camera screen.
Initializing firmware update
Writing firmware
0%
100%
Starting updater shell...

Welcome to the USB debug shell.
Type `help` for the list of supported commands.
Type `exit` to quit.
>install "C:\Users\Seb\Downloads\OpenMemories-Launcher-release-2.0.apk"
>exit
Done
```

## TODO
 - [ ] Avoid app/stream crashes after ~30 seconds
 - [ ] Graceful exit/restarts
 - [ ] Avoid whatever bug causes the camera to break the launcher
 - [ ] Read RTMP server from the `liveStreamingServiceInfo.conf` and send it to Ustreamlib instead of hardcoding it
 - [ ] Fully control the RTMP endpoint (instead of the camera appending `broadcaster_live1234`)