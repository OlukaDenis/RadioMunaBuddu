package com.mcdenny.radiomunabuddu;

public class RadioSettings {
    /********ALL EDITABLE SETTINGS ARE HERE *****/

    //Name of radio station
    private final String radioName = "Radio Munnabuddu USA";

    //URL of the radio stream
    //private String radioStreamURL = "http://markswist.com/markpersonal/B.O.B.-The_Adventures_Of_Bobby_Ray/04-b.o.b.-airplanes_(ft._hayley_williams_of_paramore).mp3";
    private String radioStreamURL = "https://munnabudduusa19.radioca.st/stream";

    //URL of webcam (or YouTube link maybe)
    private String radioWebcamURL = "http://youtube.com/";

    //URL for the advertising / message banner. For no banner leave blank, i.e: ""
    //private String adBannerURL = "http://www.aironair.co.uk/wp-content/uploads/2013/09/App-Banner.png";

    //Contact button email address
    private String emailAddress = "radiomunnabudduusa@gmail.com";

    //Contact button phone number
    private String phoneNumber = "+18184918052";

    //Contact button website URL
    private String websiteURL = "http://radiomunnabudduusa.com";

    //Contact button SMS number
    private String smsNumber = "0703269426";

    //Message to be shown in notification center whilst playing
    private String mainNotificationMessage = "You're listening to RadioMunnabudduUsa.Com";

    //TOAST notification when play button is pressed
    private String playNotificationMessage = "Starting Radio Munnabuddu USA ";

    //Play store URL (not known until published
    private String playStoreURL = "http://play.google.com/";

    //Enable console output for streaming info (Buffering etc) - Disable = false
    private boolean allowConsole = true;

    /********END OF EDITABLE SETTINGS**********/



    /********DO NOT EDIT BELOW THIS LINE*******/
    public String getRadioName(){
        return radioName;
    }

    public String getRadioStreamURL(){
        return radioStreamURL;
    }

    public String getRadioWebcamURL(){
        return radioWebcamURL;
    }

    /*public String getAdBannerURL(){
        return adBannerURL;
    }*/

    public String getEmailAddress(){
        return emailAddress;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public String getWebsiteURL(){
        return websiteURL;
    }

    public String getSmsNumber(){
        return smsNumber;
    }

    public String getMainNotificationMessage(){
        return mainNotificationMessage;
    }

    public String getPlayNotificationMessage(){
        return playNotificationMessage;
    }

    public String getPlayStoreURL(){
        return playStoreURL;
    }

    public boolean getAllowConsole(){
        return allowConsole;
    }
}
