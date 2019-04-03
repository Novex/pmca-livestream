package com.github.sebbity.sonyactioncam.stream.UstreamMod;

public class LivestreamSettings {
    // JSON Input:
    /*
    [
        [
            "twitterEnabled",
            0
        ],
        [
            "twitterConsumerKey",
            "o9fJcQiWnVokpEAsPz3qg"
        ],
        [
            "twitterConsumerSecret",
            "TTLzNJiKQlntLHXdk3YDCpx9CllM6EQdivTXesA"
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
            "75826bba9a2efd1b82ede34d381a2c83cd297c63"
        ],
        [
            "macSecret",
            "df114176c8373075f739141a1ffd95539d8e0779"
        ],
        [
            "macIssueTime",
            "dbe19a5c00000000"
        ],
        [
            "unknown",
            1
        ],
        [
            "channels",
            [
                23649863
            ]
        ],
        [
            "shortURL",
            "http://www.ustream.tv/channel/B24Hn2gMA2D"
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
        ],
        [
            "arbitraryFoo",
            "bazzzz"
        ]
    ]
    */

    // On filesystem in csv format, eg.
    /*
            # cat /data/misc/livestreaming/liveStreamingServiceInfo.conf
            "targetService",0
            0,"macId",75826bba9a2efd1b82ede34d381a2c83cd297c63
            0,"macSecret",df114176c8373075f739141a1ffd95539d8e0779
            0,"macIssueTime",1553654235
            0,"channelId",23649863
            0,"shortURL","http://www.ustream.tv/channel/B24Hn2gMA2D"
            0,"videoWidth",1280
            0,"videoHeight",720
            0,"enableRecordMode","false"
            0,"videoTitle","Recorded with Action Cam by Sony"
            0,"videoDescription","Shot 100% with Sony's Action Cam #SonyActionCam #ProveYourself"

            # cat /data/misc/livestreaming/liveStreamingSNSInfo.conf
            0,"consumerKey",o9fJcQiWnVokpEAsPz3qg
            0,"consumerSecret",TTLzNJiKQlntLHXdk3YDCpx9CllM6EQdivTXesA
            0,"message","Live Streaming from Action Cam by Sony"
            1,"message","Live Streaming from Action Cam by Sony"
    */
}
