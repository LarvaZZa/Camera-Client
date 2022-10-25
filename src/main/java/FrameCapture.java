import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.TimerTask;

import org.opencv.core.Core;

public class FrameCapture implements Runnable{

    private static VideoStreamServer videoStreamService;
    static VideoCapture videoCapture;
    static Timer tmrVideoProcess;
    static Timer tmrNotificationSent;
    private Mat currentFrame = new Mat();
    private Mat lastFrame = new Mat();
    private Mat frame = new Mat();
    private Mat processedFrame = new Mat();
    boolean notificationSent = false;
    boolean firstNotification = true;

    public void run() {

        videoCapture = new VideoCapture();
        videoCapture.open(0);

        if (!videoCapture.isOpened()) {
            return;
        }

        videoCapture.read(frame);
        frame.copyTo(lastFrame);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        videoStreamService = new VideoStreamServer(frame);
        new Thread(videoStreamService).start();

        tmrVideoProcess = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!videoCapture.read(frame)) {
                    tmrVideoProcess.stop();
                }

                frame.copyTo(currentFrame);

                Imgproc.GaussianBlur(currentFrame, currentFrame, new Size(3, 3), 0);
                Imgproc.GaussianBlur(lastFrame, lastFrame, new Size(3, 3), 0);

                Core.subtract(currentFrame, lastFrame, processedFrame);

                Imgproc.cvtColor(processedFrame, processedFrame, Imgproc.COLOR_RGB2GRAY);
                Imgproc.threshold(processedFrame, processedFrame, 30, 255, Imgproc.THRESH_BINARY);

                if (detection_contours(currentFrame, processedFrame).size() > 0 && !videoStreamService.watchingStream && !notificationSent) {
                    if (firstNotification){
                        //this has to be done because it will always execute the if statement for some reason.
                        firstNotification = false;
                    } else {
                        notificationSent = true;
                        new Thread(new Notification(Main.CLIENT, "ATTENTION!", "ACTIVITY ALERT")).start();
                        new java.util.Timer().schedule(
                                new java.util.TimerTask(){
                                    @Override
                                    public void run() {
                                        notificationSent = false;
                                    }
                                }, 7000
                        );
                    }
                }
                frame.copyTo(lastFrame);
                videoStreamService.imag = currentFrame;
            }
        });
        tmrVideoProcess.start();
    }

    public ArrayList<Rect> detection_contours(Mat frame, Mat outmat)
    {
        Mat v = new Mat();
        Mat vv = outmat.clone();
        List<MatOfPoint> contours = new ArrayList();
        Imgproc.findContours(vv, contours, v, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        double maxArea = 100;
        int maxAreaIdx;
        Rect r;
        ArrayList<Rect> rect_array = new ArrayList();

        for(int idx = 0; idx < contours.size(); idx++)
        {
            Mat contour = contours.get(idx);
            double contourarea = Imgproc.contourArea(contour);
            if(contourarea > maxArea)
            {
                // maxArea = contourarea;
                maxAreaIdx = idx;
                r = Imgproc.boundingRect(contours.get(maxAreaIdx));
                rect_array.add(r);
                Imgproc.drawContours(frame, contours, maxAreaIdx, new Scalar(96, 216, 252), 3);
            }
        }

        v.release();
        return rect_array;
    }

}
