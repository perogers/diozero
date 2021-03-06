package com.diozero.sampleapps;

/*
 * #%L
 * Organisation: mattjlewis
 * Project:      Device I/O Zero - Sample applications
 * Filename:     LDRTest.java  
 * 
 * This file is part of the diozero project. More information about this project
 * can be found at http://www.diozero.com/
 * %%
 * Copyright (C) 2016 - 2017 mattjlewis
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import org.pmw.tinylog.Logger;

import com.diozero.devices.LDR;
import com.diozero.devices.McpAdc;
import com.diozero.util.RuntimeIOException;
import com.diozero.util.SleepUtil;

/**
 * Sample application for reading LDR analog values. To run:
 * <ul>
 * <li>sysfs:<br>
 *  {@code java -cp tinylog-1.2.jar:diozero-core-$DIOZERO_VERSION.jar:diozero-sampleapps-$DIOZERO_VERSION.jar com.diozero.sampleapps.LDRTest MCP3304 0 2}</li>
 * <li>JDK Device I/O 1.0:<br>
 *  {@code sudo java -cp tinylog-1.2.jar:diozero-core-$DIOZERO_VERSION.jar:diozero-sampleapps-$DIOZERO_VERSION.jar:diozero-provider-jdkdio10-$DIOZERO_VERSION.jar:dio-1.0.1-dev-linux-armv6hf.jar -Djava.library.path=. com.diozero.sampleapps.LDRTest MCP3304 0 2}</li>
 * <li>JDK Device I/O 1.1:<br>
 *  {@code sudo java -cp tinylog-1.2.jar:diozero-core-$DIOZERO_VERSION.jar:diozero-sampleapps-$DIOZERO_VERSION.jar:diozero-provider-jdkdio11-$DIOZERO_VERSION.jar:dio-1.1-dev-linux-armv6hf.jar -Djava.library.path=. com.diozero.sampleapps.LDRTest MCP3304 0 2}</li>
 * <li>Pi4j:<br>
 *  {@code sudo java -cp tinylog-1.2.jar:diozero-core-$DIOZERO_VERSION.jar:diozero-sampleapps-$DIOZERO_VERSION.jar:diozero-provider-pi4j-$DIOZERO_VERSION.jar:pi4j-core-1.1-SNAPSHOT.jar com.diozero.sampleapps.LDRTest MCP3304 0 2}</li>
 * <li>wiringPi:<br>
 *  {@code sudo java -cp tinylog-1.2.jar:diozero-core-$DIOZERO_VERSION.jar:diozero-sampleapps-$DIOZERO_VERSION.jar:diozero-provider-wiringpi-$DIOZERO_VERSION.jar:pi4j-core-1.1-SNAPSHOT.jar com.diozero.sampleapps.LDRTest MCP3304 0 2}</li>
 * <li>pigpgioJ:<br>
 *  {@code sudo java -cp tinylog-1.2.jar:diozero-core-$DIOZERO_VERSION.jar:diozero-sampleapps-$DIOZERO_VERSION.jar:diozero-provider-pigpio-$DIOZERO_VERSION.jar:pigpioj-java-1.0.1.jar com.diozero.sampleapps.LDRTest MCP3304 0 2}</li>
 * </ul>
 */
public class LDRTest {
	private static final int ITERATIONS = 20;

	public static void main(String[] args) {
		if (args.length < 3) {
			Logger.error("Usage: {} <mcp-name> <chip-select> <adc-pin>", LDRTest.class.getName());
			System.exit(2);
		}
		McpAdc.Type type = McpAdc.Type.valueOf(args[0]);
		if (type == null) {
			Logger.error("Invalid MCP ADC type '{}'. Usage: {} <mcp-name> <spi-chip-select> <adc_pin>", args[0], LDRTest.class.getName());
			System.exit(2);
		}
		
		int chip_select = Integer.parseInt(args[1]);
		int adc_pin = Integer.parseInt(args[2]);
		float vref = 3.3f;
		int r1 = 10_000;
		test(type, chip_select, adc_pin, vref, r1);
	}
	
	public static void test(McpAdc.Type type, int chipSelect, int pin, float vRef, int r1) {
		try (McpAdc adc = new McpAdc(type, chipSelect, vRef); LDR ldr = new LDR(adc, pin, r1)) {
			for (int i=0; i<ITERATIONS; i++) {
				double v_ldr = ldr.getScaledValue();
				double r_ldr = ldr.getLdrResistance();
				Logger.info(String.format("vLDR: %.2f, rLDR: %.2f", Double.valueOf(v_ldr), Double.valueOf(r_ldr)));
				SleepUtil.sleepSeconds(.5);
			}
		} catch (RuntimeIOException e) {
			Logger.error(e, "Error: {}", e);
		}
	}
}
