package io.github.sidf.documentreader.featuredetection;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import io.github.sidf.documentreader.system.PathHelper;

public class FeatureDetector implements Runnable {
	private static String faceImagePath;
	private static CascadeClassifier faceClassifier;
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		faceImagePath = PathHelper.getResourcePath("images/faceImageEyesOpen.jpg");
		faceClassifier = new CascadeClassifier(PathHelper.getResourcePath("cascades/lbpcascade_frontalface.xml"));
	}
	
	public FeatureDetector() {
		
	}
	
	public static void main(String[] array) {
		FeatureDetector featureDetector = new FeatureDetector();
		featureDetector.featureDetectorLoop();
	}

	public void run() {
		featureDetectorLoop();
	}
	
	private void featureDetectorLoop() {
		Mat originalImage = Imgcodecs.imread(faceImagePath);
		Mat grayImage = new Mat();
		Imgproc.cvtColor(originalImage, grayImage, Imgproc.COLOR_BGR2GRAY);
		
		MatOfRect faceDetections = new MatOfRect();
		faceClassifier.detectMultiScale(originalImage, faceDetections);
		
		System.out.println("Detected faces: " +  faceDetections.toArray().length);
	}
}
