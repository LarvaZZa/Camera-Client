import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AudioReceiverHandler implements Runnable{

    ServerSocket serverSocket;
    Socket socket;

    public void run() {
        try{
            serverSocket = new ServerSocket(8089);
            while(true){
                socket = serverSocket.accept();
                new Thread(new AudioReceiverThread(socket)).start();
            }
        } catch(IOException ioe){
            ioe.printStackTrace();
        }


    }
}
