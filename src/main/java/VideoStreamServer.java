
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class VideoStreamServer implements Runnable {

    public boolean watchingStream = false;
    private BufferedImage img;
    ByteArrayOutputStream baos;
    Rect[] facesArray;
    byte[] imageBytes;
    private ServerSocket serverSocket;
    private Socket socket;
    private final String boundary = "stream";
    private OutputStream outputStream;
    public Mat imag;

    private int port = 8088;
    CascadeClassifier cascadeClassifier;
    MatOfRect facesDetected;
    int minFaceSize;

    public VideoStreamServer(Mat imagFr) {
        this.imag = imagFr;
    }

    public void run() {
        try {
            System.out.println("footage available at http://localhost:8088");
            startStreamingServer();

            cascadeClassifier = new CascadeClassifier();
            cascadeClassifier.load("src/main/xml/haarcascade_frontalface_alt.xml");
            facesDetected = new MatOfRect();

            while (true) {
                pushImage(imag);
            }

        } catch (IOException e) {
            System.out.println(e);
        }

    }


    public void startStreamingServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Waiting for client");
        socket = serverSocket.accept();
        System.out.println("Client connected for Video Stream");
        watchingStream = true;
        writeHeader(socket.getOutputStream(), boundary);
    }

    private void writeHeader(OutputStream stream, String boundary) throws IOException {
        stream.write(("HTTP/1.0 200 OK\r\n" +
                "Connection: close\r\n" +
                "Max-Age: 0\r\n" +
                "Expires: 0\r\n" +
                "Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\r\n" +
                "Pragma: no-cache\r\n" +
                "Content-Type: multipart/x-mixed-replace; " +
                "boundary=" + boundary + "\r\n" +
                "\r\n" +
                "--" + boundary + "\r\n").getBytes());
    }

    public void pushImage(Mat frame) throws IOException {
        if (frame == null)
            return;
        try {

            minFaceSize = Math.round(frame.rows() * 0.1f);

            cascadeClassifier.detectMultiScale(frame, facesDetected,1.1,3, Objdetect.CASCADE_SCALE_IMAGE,new Size(minFaceSize, minFaceSize),new Size());

            facesArray = facesDetected.toArray();
            for(Rect face : facesArray) {
                Imgproc.rectangle(frame, face.tl(), face.br(), new Scalar(25, 15, 213), 3);
            }
            outputStream = socket.getOutputStream();
            img = Mat2bufferedImage(frame);
            baos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", baos);
            imageBytes = baos.toByteArray();
            outputStream.write(("Content-type: image/jpeg\r\n" +
                    "Content-Length: " + imageBytes.length + "\r\n" +
                    "\r\n").getBytes());
            outputStream.write(imageBytes);
            outputStream.write(("\r\n--" + boundary + "\r\n").getBytes());
            outputStream.flush();
        } catch (Exception e) {
            watchingStream = false;
            System.out.println("Waiting for client");
            socket = serverSocket.accept();
            System.out.println("Client connected for Video Stream");
            watchingStream = true;
            writeHeader(socket.getOutputStream(), boundary);
        }
    }

    //might use or might not
    public void stopStreamingServer() throws IOException {
        socket.close();
        serverSocket.close();
    }
    
    public static BufferedImage Mat2bufferedImage(Mat image) throws IOException {
        MatOfByte byteMat = new MatOfByte();
        Imgcodecs.imencode(".jpg", image, byteMat);
        return ImageIO.read(new ByteArrayInputStream(byteMat.toArray()));
    }
}