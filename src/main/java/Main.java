import org.opencv.core.Core;

public class Main {

    public static final String AUDIO_FILE_LOCATION = "LOCATION_OF_AUDIO_FILE"; //any location, I recommend C:/[some folder name]/sound.wav
    public static final String CLIENT = "YOUR_FCM_REGISTRATION_TOKEN_HERE";

    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new Thread(new FrameCapture()).start();
        new Thread(new AudioReceiverHandler()).start();
    }
}
