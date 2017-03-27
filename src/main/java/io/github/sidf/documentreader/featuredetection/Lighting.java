package io.github.sidf.documentreader.featuredetection;

import java.util.List;
import java.util.ArrayList;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.trigger.GpioSetStateTrigger;

public class Lighting implements Runnable {
	private boolean isStillRunning;
	private final GpioController gpioController =  GpioFactory.getInstance();
	private final List<GpioSetStateTrigger> triggers = new ArrayList<GpioSetStateTrigger>();
	
	private GpioPinDigitalInput sensorInputController;
	private GpioPinDigitalOutput sensorOutputController;
	private static final Pin sensorInputPin = RaspiPin.GPIO_00;
	private static final Pin sensorOutputPin = RaspiPin.GPIO_01;
	private static final PinState[] pinStates = { PinState.LOW, PinState.HIGH };
	private static final Pin[] ledInputPins = { RaspiPin.GPIO_02, RaspiPin.GPIO_03, RaspiPin.GPIO_04, RaspiPin.GPIO_05 };
	
	private void setupPins() {
		setupOutputPins();
		setupInputPins();
		
		sensorOutputController.high();
	}
	
	private void setupOutputPins() {
		sensorOutputController = gpioController.provisionDigitalOutputPin(sensorOutputPin);
		sensorOutputController.setShutdownOptions(true, PinState.LOW);
		
		for (Pin pin : ledInputPins) {
			GpioPinDigitalOutput ledController = gpioController.provisionDigitalOutputPin(pin);
			for (PinState state : pinStates) {
				triggers.add(new GpioSetStateTrigger(state, ledController, state));
			}
			
			ledController.setShutdownOptions(true, PinState.LOW);
		}
	}
	
	private void setupInputPins() {
		sensorInputController = gpioController.provisionDigitalInputPin(sensorInputPin);
		sensorInputController.addTrigger(triggers);
	}
	
	public static void main(String[] array) throws InterruptedException {
		Lighting lighting = new Lighting();
		lighting.run();
	}

	public void run() {
		start();
	}
	
	private void start() {
		setupPins();
		isStillRunning = true;
		
		while (isStillRunning) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}
		
		stop();
	}
	
	public void stop() {
		isStillRunning = false;
		gpioController.shutdown();
	}
}
