package io.github.sidf.documentreader.system;

import java.util.List;
import java.util.ArrayList;
import com.pi4j.io.gpio.Pin;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.trigger.GpioSetStateTrigger;
import com.pi4j.io.gpio.exception.GpioPinNotProvisionedException;

public class Lighting implements Runnable {
	private static Logger logger = Logger.getLogger(Lighting.class.getName());
	
	private boolean isStillRunning;
	private final GpioController gpioController =  GpioFactory.getInstance();
	private final List<GpioSetStateTrigger> triggers = new ArrayList<GpioSetStateTrigger>();
	
	private GpioPinDigitalInput sensorInputController;
	private GpioPinDigitalOutput sensorOutputController;
	private List<GpioPin> pinControllers = new ArrayList<>();
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
		pinControllers.add(sensorOutputController);
		
		for (Pin pin : ledInputPins) {
			GpioPinDigitalOutput ledController = gpioController.provisionDigitalOutputPin(pin);
			pinControllers.add(ledController);
			for (PinState state : pinStates) {
				triggers.add(new GpioSetStateTrigger(state, ledController, state));
			}
			
			ledController.setShutdownOptions(true, PinState.LOW);
		}
	}
	
	private void setupInputPins() {
		sensorInputController = gpioController.provisionDigitalInputPin(sensorInputPin);
		sensorInputController.addTrigger(triggers);
		pinControllers.add(sensorInputController);
	}
	
	@Override
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
				logger.log(Level.WARNING, "Thread sleep interrupted", e);
			}
		}
		
		unprovisionPins();
		stop();
	}
	
	private void unprovisionPins() {
		for (GpioPin pin : pinControllers) {
			try {
				gpioController.unprovisionPin(pin);
			} catch (GpioPinNotProvisionedException e) {
				logger.log(Level.WARNING, "Couldn't unprovision pin " + pin.getName(), e);
			}
		}
	}
	
	public void stop() {
		isStillRunning = false;
		
		// unnecessary if we unprovision https://github.com/Pi4J/pi4j/issues/220
//		gpioController.shutdown();
	}
}
