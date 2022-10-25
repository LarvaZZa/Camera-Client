import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class AudioReceiverThread implements Runnable {

    private Socket socket;
    private InputStream inputStream;
    private FileOutputStream fileOutputStream;
    private MakeSound makeSound;

    public AudioReceiverThread(Socket socket){
        this.socket=socket;
    }

    public void run() {
        try {
            inputStream = socket.getInputStream();
            fileOutputStream = new FileOutputStream(Main.AUDIO_FILE_LOCATION);
            makeSound = new MakeSound(Main.AUDIO_FILE_LOCATION);
            byte buffer[] = new byte[4096];
            for (int bytesRead = inputStream.read(buffer); bytesRead != -1; bytesRead = inputStream.read(buffer)) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Audio received");
            inputStream.close();
            fileOutputStream.close();
            new Thread(makeSound).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
