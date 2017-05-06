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

/**
 * Class that handles the light sensor and the led lighting system
 * @author Esc
 */
public class Lighting implements Runnable {
	private static Logger logger = Logger.getLogger(Lighting.class.getName());
	
	private boolean isStillRunning;
	
	/**
	 * Concrete GpioController instance that is used to provision / unprovision the GPIO pins
	 */
	private final GpioController gpioController =  GpioFactory.getInstance();
	
	/**
	 * List of GpioSetStateTrigger that is used to sync the state of the led output pins with light sensor's output level
	 */
	private final List<GpioSetStateTrigger> triggers = new ArrayList<GpioSetStateTrigger>();
	
	/**
	 * Controller of the pin that is connected to the light sensor's output
	 */
	private GpioPinDigitalInput sensorInputController;
	
	/**
	 * Controller of the pin that is connected to the light sensor's input
	 */
	private GpioPinDigitalOutput sensorOutputController;
	
	/**
	 * List that holds references to the sensor and led related pin controllers
	 */
	private List<GpioPin> pinControllers = new ArrayList<>();
	
	private static final Pin sensorInputPin = RaspiPin.GPIO_00;
	private static final Pin sensorOutputPin = RaspiPin.GPIO_01;
	private static final PinState[] pinStates = { PinState.LOW, PinState.HIGH };
	private static final Pin[] ledInputPins = { RaspiPin.GPIO_02, RaspiPin.GPIO_03, RaspiPin.GPIO_04, RaspiPin.GPIO_05 };
	
	/**
	 * Configures all input and output pins
	 */
	private void setupPins() {
		setupOutputPins();
		setupInputPins();
	}
	
	/**
	 * Configures the pins that are meant to power the light sensor and the leds
	 */
	private void setupOutputPins() {
		// provisioning seems to configure pins in LOW mode by default
		sensorOutputController = gpioController.provisionDigitalOutputPin(sensorOutputPin);
		pinControllers.add(sensorOutputController);
		
		for (Pin pin : ledInputPins) {
			GpioPinDigitalOutput ledController = gpioController.provisionDigitalOutputPin(pin);
			pinControllers.add(ledController);
			for (PinState state : pinStates) {
				triggers.add(new GpioSetStateTrigger(state, ledController, state));
			}
		}
	}
	
	/**
	 * Configures the pin that is tied to the light sensor's output
	 */
	private void setupInputPins() {
		// provisioning seems to configure pins in LOW mode by default
		sensorInputController = gpioController.provisionDigitalInputPin(sensorInputPin);
		
		// this will sync the state of the led output pins when it's state changes
		sensorInputController.addTrigger(triggers);
		pinControllers.add(sensorInputController);
	}
	
	@Override
	public void run() {
		start();
	}
	
	/**
	 * Calls {@link #setupPins()} and then calls {@link #cleanupPins()} if an exception occurs or 
	 * the value of {@link #isStillRunning} becomes false
	 */
	private void start() {
		setupPins();
		sensorOutputController.high();
		
		isStillRunning = true;
		
		while (isStillRunning) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				logger.log(Level.WARNING, "Lighting thread sleep was interrupted", e);
			}
		}
		
		cleanupPins();
		stop();
	}
	
	/**
	 * Releases every pin referenced in {@link #pinControllers}
	 */
	private void cleanupPins() {
		for (GpioPin pin : pinControllers) {
			if (pin instanceof GpioPinDigitalOutput) {
				GpioPinDigitalOutput pinDigitalOutput = (GpioPinDigitalOutput) pin;
				if (pinDigitalOutput.isHigh()) {
					pinDigitalOutput.low();
				}
			}
			
			try {
				pin.unexport();
				gpioController.unprovisionPin(pin);
			} catch (GpioPinNotProvisionedException e) {
				logger.log(Level.WARNING, "Couldn't unprovision pin " + pin.getName(), e);
			}
		}
	}
	
	/**
	 * Determines the stopping of the lighting functionality
	 */
	public void stop() {
		isStillRunning = false;
	}
}
