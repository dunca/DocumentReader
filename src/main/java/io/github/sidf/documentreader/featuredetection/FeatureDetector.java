package io.github.sidf.documentreader.featuredetection;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

import io.github.sidf.documentreader.system.Device;
import io.github.sidf.documentreader.system.PathHelper;

public class FeatureDetector implements Runnable {
	private boolean isStillRunning;
	private VideoCapture captureDevice;
	private ScheduledFuture scheduledFuture;
	private static CascadeClassifier faceClassifier;
	private static CascadeClassifier leftEyeClassifier;
	private static CascadeClassifier rightEyeClassifier;
	private ScheduledExecutorService scheduledExecutorService;
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		leftEyeClassifier = new CascadeClassifier(PathHelper.getResourcePath("cascades/left_eye_lbp.xml"));
		rightEyeClassifier = new CascadeClassifier(PathHelper.getResourcePath("cascades/right_eye_lbp.xml"));
		faceClassifier = new CascadeClassifier(PathHelper.getResourcePath("cascades/lbpcascade_frontalface.xml"));
	}
	
	public FeatureDetector() throws IOException {
		captureDevice = new VideoCapture(0);
		
		if (!captureDevice.isOpened()) {
			throw new IOException("Could not open video capture device");
		}
	}
	
	public static void main(String[] array) throws IOException {
		FeatureDetector featureDetector = new FeatureDetector();
		featureDetector.featureDetectorLoop();
	}

	public void run() {
		featureDetectorLoop();
	}
	
	private void featureDetectorLoop() {
		isStillRunning = true;
		scheduledExecutorService = Executors.newScheduledThreadPool(1);
		
		Mat image;
		Mat grayImage;
		
		while (isStillRunning) {
			image = new Mat();
			grayImage = new Mat();
			
			captureDevice.retrieve(image);
			System.out.println("Image grabbed");
			Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
			
			MatOfRect faceDetections = detectFaces(grayImage);
			
			Mat grayFaceImage = grayImage.submat(faceDetections.toArray()[0]);
			
			int x = 0;
			int y = (int)(grayFaceImage.size().height * 0.2);
			int width = (int)grayFaceImage.size().width;
			int height = (int)(y * 1.7);

			Rect eyeRegionRect = new Rect(x, y, width, height);
			Mat eyeRegionImage = grayFaceImage.submat(eyeRegionRect);
			
			Imgcodecs.imwrite("C:\\face.jpg", eyeRegionImage);
			
			MatOfRect eyeDetections = detectClosedEyes(eyeRegionImage);
			
			if (eyeDetections.elemSize() == 0) {
				if (scheduledFuture != null) {
					scheduledFuture.cancel(false);
					scheduledFuture = null;
					System.out.print("no longer scheduled");
				}
			} else if (scheduledFuture == null) {
				scheduledFuture = scheduledExecutorService.schedule(new Runnable() {
					public void run() {
						stop();
//						Device.shutDown();
						System.out.println("Scheduled thing executed");
					}
				}, 30, TimeUnit.SECONDS);
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				stop();
			}
		}

	}
	
	private MatOfRect detectFaces(Mat grayImage) {
		MatOfRect faces = new MatOfRect();
		faceClassifier.detectMultiScale(grayImage, faces);
		return faces;
	}
	
	private MatOfRect detectClosedEyes(Mat eyeRegionImage) {
		int width = (int)eyeRegionImage.size().width / 2;
		int height = (int)eyeRegionImage.size().height;
		
		MatOfRect rightEyes = detectClosedEyes(rightEyeClassifier, eyeRegionImage.submat(0, height, 0, width));
		
		if (rightEyes.elemSize() != 0) {
			System.out.println("right detected");
			return rightEyes;
		}
		MatOfRect leftEyes = detectClosedEyes(leftEyeClassifier, eyeRegionImage.submat(0, height, width, (int)eyeRegionImage.size().width));
		if (leftEyes.elemSize() != 0) {
			System.out.println("left detected");
		}
		return leftEyes;
	}
	
	private MatOfRect detectClosedEyes(CascadeClassifier classifier, Mat grayFaceImage) {
		MatOfRect mat = new MatOfRect();
		classifier.detectMultiScale(grayFaceImage, mat);
		return mat;
	}
	
	public void stop() {
		isStillRunning = false;
		
		if (scheduledExecutorService != null) {
			scheduledExecutorService.shutdown();
		}
	}
}
