import org.opencv.core.Core;

public class Main {

    public static final String AUDIO_FILE_LOCATION = "C:/testing/sound666.wav";
    public static final String CLIENT = "eISYtGyjRDq7gaEKvQWXHV:APA91bHcAHMydKFTnJPotD8MFsgexcPWWeHtm-TSXEobMrnYqceuz7wqmMWpSX8TEYzI58zxhUoczzl7H9tboL-mLtSuDoVyoc2kqBjlEiKWe88HQ6YX5MJgk4GS1AYzmis1iD_Ekymg";

    //public static final String CLIENT_FROM_LAPTOP = "eC_rP-c9RBia7rEX195Mml:APA91bEwyI1fLcN2FZdUXRNL09zjTg6OXoOjgj8Gg_9-IwRojygYBfJDHyFFdjhWdXBLiUt_ry6nBVpG1c2LzLmP7Gg0MgHuaQJtMojmRSfriTbwcSx66x0c1yz4D65u01JR8Bdy6Nhj";

    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new Thread(new FrameCapture()).start();
        new Thread(new AudioReceiverHandler()).start();
    }
}
