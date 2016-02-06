package controller;

import java.io.*;
import java.net.*;

import lejos.hardware.Button;

import lejos.hardware.motor.Motor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.utility.Delay;

/**
 * This class is the Server file to be run on the EV3 brick
 * PRIOR to performing commands with the EV3ControlGUI1. It acts
 * as a receiver for commands sent from the user's PC via the 
 * EV3ControlGUI1 class and executes them on the EV3 using the
 * leJos DifferentialPilot class.
 * 
 * @author Muhammad Rizvi
 * @since April 2015
 *
 */

public class EV3Server {
	public static final int port = 1234;
	public static void main(String[] args) throws IOException, EOFException, NullPointerException{


		DifferentialPilot pilot = new DifferentialPilot(5.5, 13.7, Motor.A, Motor.D, false);
		while (true){
			Delay.msDelay(1000);
			ServerSocket server = new ServerSocket(port);
			System.out.println("Awaiting client ...");
			Socket client = server.accept();
			System.out.println("CONNECTED!");

			InputStream in = client.getInputStream();
			DataInputStream dIn = new DataInputStream(in);

			OutputStream out = client.getOutputStream();
			DataOutputStream dOut = new DataOutputStream(out);


			String str = dIn.readUTF();
			// Deals with traveling forward or backward
			if( str.equals("1")){

				String Direction = dIn.readUTF();

				int distance = Integer.parseInt(dIn.readUTF());

				if (Direction.equals("back")){
					distance = distance * -1;
					Button.LEDPattern(5);
				} else {
					Button.LEDPattern(1);
				}

				pilot.travel(distance);
				Button.LEDPattern(0);
				dOut.writeUTF("done");
			} else if (str.equals("turn")){			// Deals with turning right or left
				String Direction = dIn.readUTF();

				int angle = Integer.parseInt(dIn.readUTF());

				if (Direction.equals("right")){
					angle = angle * -1;
				}

				Button.LEDPattern(3);
				pilot.rotate(angle);
				Button.LEDPattern(0);
				dOut.writeUTF("done");
			} else if (str.equals("0")){
				server.close();	
				break; 
			}
			
			try {
				dIn.reset();
			} catch(IOException e){

			}
			server.close();
		}
	}
}
