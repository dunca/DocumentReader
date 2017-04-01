package io.github.sidf.documentreader.featuredetection;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;

import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.MatOfRect;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

import io.github.sidf.documentreader.util.PathUtil;
import io.github.sidf.documentreader.util.CommandUtil;

public class FeatureDetector implements Runnable, AutoCloseable {
	private boolean isStillRunning;
	private VideoCapture captureDevice;
	private static FeatureDetector instance;
	private ScheduledFuture scheduledFuture;
	private static CascadeClassifier faceClassifier;
	private static CascadeClassifier leftEyeClassifier;
	private static CascadeClassifier rightEyeClassifier;
	private ScheduledExecutorService scheduledExecutorService;
	private static Logger logger = Logger.getLogger(FeatureDetector.class.getName());
	private static final String[] autofocusTweakCommands = { "uvcdynctrl --set='Focus, Auto' 0", "uvcdynctrl --set='Focus (absolute)' 5" };
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	private FeatureDetector() throws IOException {
		captureDevice = new VideoCapture(0);

		if (!captureDevice.isOpened()) {
			String message = "Could not open video capture device";
			logger.warning(message);
			throw new IOException(message);
		}
		
		disableAutofocus();
		
		if (faceClassifier == null) {
			leftEyeClassifier = new CascadeClassifier(PathUtil.resourcePathToFilePath("cascades/left_eye_lbp.xml"));
			rightEyeClassifier = new CascadeClassifier(PathUtil.resourcePathToFilePath("cascades/right_eye_lbp.xml"));
			faceClassifier = new CascadeClassifier(PathUtil.resourcePathToFilePath("cascades/lbpcascade_frontalface.xml"));
		}
	}
	
	public static FeatureDetector getInstance() throws IOException {
		if (instance == null) {
			instance = new FeatureDetector();
		}
		return instance;
	}
	
	public void run() {
		featureDetectorLoop();
	}
	
	private void featureDetectorLoop() {
		isStillRunning = true;
		scheduledExecutorService = Executors.newScheduledThreadPool(2);
		
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				System.runFinalization();
				System.gc();
			}
			
		}, 20, 20, TimeUnit.SECONDS);
		
		Mat image;
		Mat grayImage;
		
		while (isStillRunning) {
			image = new Mat();

			// top quality hack used to clear the buffer
			for (int i = 0; i < 6; i++) {
				captureDevice.read(image);
			}
			grayImage = new Mat();
			
			Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
			MatOfRect faceDetections = detectFaces(grayImage);
			
			if (faceDetections.empty()) {
				continue;
			}
			
			Mat grayFaceImage = grayImage.submat(faceDetections.toArray()[0]);
//			saveImageToDesktop(grayFaceImage, "face");
			
			Size grayFaceImageSize = grayFaceImage.size();
			
			int x = 0;
			int y = (int)(grayFaceImageSize.height * 0.2);
			int width = (int)(grayFaceImageSize.width);
			int height = (int)(y * 1.7);
			
			int halfWidth = width / 2;
			Rect rightEyeRegion = new Rect(x, y, halfWidth, height);
			Rect leftEyeRegion = new Rect(halfWidth, y, (int)(grayFaceImageSize.width - halfWidth), height);

			MatOfRect eyeDetections = detectClosedEyes(grayFaceImage, rightEyeRegion, leftEyeRegion);
			managerSchedule(eyeDetections);			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				stop();
			}
		}

	}
	
	private void managerSchedule(Mat eyeDetections) {
		if (eyeDetections.elemSize() == 0) {
			if (scheduledFuture != null) {
				scheduledFuture.cancel(false);
				scheduledFuture = null;
				logger.info("Disabled schedule");
			}
		} else if (scheduledFuture == null) {
			scheduledFuture = scheduledExecutorService.schedule(new Runnable() {
				public void run() {
					stop();
					logger.info("Enabled schedule");
				}
			}, 15, TimeUnit.SECONDS);
		}
	}
	
	private MatOfRect detectFaces(Mat grayImage) {
		MatOfRect faces = new MatOfRect();
		faceClassifier.detectMultiScale(grayImage, faces);

		if (!faces.empty()) {
			logger.info("Face detected");
		}
		return faces;
	}
	
	private MatOfRect detectClosedEyes(Mat grayFaceImage, Rect... rects) {
		Mat rightEyeImage = grayFaceImage.submat(rects[0]);
		MatOfRect rightEyes = detectClosedEyes(rightEyeClassifier, rightEyeImage);
		
		if (!rightEyes.empty()) {
			logger.info("The right eye seems to be closed");
//			saveImageToDesktop(rightEyeImage, "right");
			return rightEyes;
		}
		
		Mat leftEyeImage = grayFaceImage.submat(rects[1]);
		MatOfRect leftEyes = detectClosedEyes(leftEyeClassifier, leftEyeImage);
		if (!leftEyes.empty()) {
			logger.info("The left eye seems to be closed");
//			saveImageToDesktop(leftEyeImage, "left");
		}
		return leftEyes;
	}
	
	private MatOfRect detectClosedEyes(CascadeClassifier classifier, Mat grayFaceImage) {
		MatOfRect eyes = new MatOfRect();
		classifier.detectMultiScale(grayFaceImage, eyes);
		return eyes;
	}
	
	private void saveImage(Mat image, String imageName) {
		String path = System.getProperty("user.home");
		
		if (!new File(path).exists()) {
			logger.warning(String.format("Could not save image %s", imageName));
		}
		
		Highgui.imwrite(String.format("%s/%s.jpg", path, imageName), image);
	}
	
	public void stop() {
		isStillRunning = false;
		
		if (scheduledExecutorService != null) {
			scheduledExecutorService.shutdown();
		}
	}

	public void close() throws Exception {
		captureDevice.release();
	}
	
	private void disableAutofocus() {
		logger.info("Trying to disable autofocus");
		for (String command : autofocusTweakCommands) {
			try {
				CommandUtil.launchNonBlockingCommand(command);
			} catch (Exception e) {
				logger.log(Level.WARNING, "Something went wrong while trying to disable autofocus", e);
			}
		}
	}
	
	public static void main(String[] arr) throws IOException {
		FeatureDetector fDetector = new FeatureDetector();
		fDetector.run();
	}
}
