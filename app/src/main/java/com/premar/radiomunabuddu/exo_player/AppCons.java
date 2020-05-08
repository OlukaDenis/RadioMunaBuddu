package com.premar.radiomunabuddu.exo_player;

public class AppCons {

    public AppCons() {
    }

    //Name of radio station
    private final String radioName = "Radio MB FM";

    public static final String IDLE = "PlaybackStatus_IDLE";

    public static final String LOADING = "PlaybackStatus_LOADING";

    public static final String PLAYING = "PlaybackStatus_PLAYING";

    public static final String PAUSED = "PlaybackStatus_PAUSED";

    public static final String STOPPED = "PlaybackStatus_STOPPED";

    public static final String ERROR = "PlaybackStatus_ERROR";

    public static String radioStreamURL = "https://munnabudduusa19.radioca.st/stream";

    //URL of webcam (or YouTube link maybe)
    public static String radioWebcamURL = "http://youtube.com/channel/UCJEqaSGJWfXPRLMGIHerK7Q";

    //Contact button email address
    public static  String emailAddress = "radiomunnabudduusa@gmail.com";

    //Facebook profile page link
    public static  String facebookAddress = "https://m.facebook.com/radiomunnabudduusa";

    //Contact button phone number
    public static  String phoneNumber = "+18184918052";

    //Contact button website URL
    public static  String websiteURL = "http://radiomunnabudduusa.com";

    //Contact button SMS number
    public static  String smsNumber = "+18184918052";

    //Message to be shown in notification center whilst playing
    public static  String mainNotificationMessage = "You're listening to Radio MB FM";

    //TOAST notification when play button is pressed
    public static  String playNotificationMessage = "Starting Radio Munnabuddu USA ";

    //Play store URL (not known until published
    public static  String playStoreURL = "http://play.google.com/";


}
