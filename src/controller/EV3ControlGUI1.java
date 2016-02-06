package controller;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.JEditorPane;
import javax.swing.AbstractListModel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import java.awt.Toolkit;
import javax.swing.ImageIcon;

/**
 * This class is run on the user's computer and issues
 * commands to an EV3 brick. The user may dynamically enter commands,
 * or enter a preset list of instructions to be executed consecutively. If 
 * this is chosen, the user may return "home" by pressing the home button (i.e. the
 * EV3 traces its path back to the relative starting point.
 * Note commands should only be sent to the EV3 AFTER running 
 * the EV3Server class on the brick.
 * 
 * @author Muhammad Rizvi
 * @since April 2015
 *
 */

public class EV3ControlGUI1 extends JFrame {

	private static final long serialVersionUID = 1L;

	// list used to store instructions provided by user in a 'Preset' situation
	private ArrayList<String> values = new ArrayList<String>(); 

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */	
	static final String IP = "192.168.1.149";
	private JTextField txtDistance;
	private JTextField txtDegrees;

	final JList<String> list = new JList<String>();

	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EV3ControlGUI1 frame = new EV3ControlGUI1();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}

	/**
	 * Create the frame.
	 */
	public EV3ControlGUI1() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(EV3ControlGUI1.class.getResource("/com/sun/java/swing/plaf/windows/icons/Computer.gif")));
		setTitle("EV3 Controller");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 475, 306);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		final JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				values.remove(list.getSelectedValue());
				updateCommands();
			}
		});
		btnRemove.setBounds(254, 159, 89, 23);
		btnRemove.setEnabled(false);
		contentPane.add(btnRemove);

		final JButton btnGoAll = new JButton("GO!");
		btnGoAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int x = 0; x < values.size(); x++){
					String command = values.get(x);
					System.out.println(command);
					String[] temp = command.split(" ");
					String action = temp[0];
					switch (action){
					case "forward":
						try {
							forward(temp[1]);
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						break;
					case "backward":
						try {
							backward(temp[1]);
						} catch (IOException e2) {
							e2.printStackTrace();
							break;
						}
					case "right":
						try {
							right(temp[1]);
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						break;
					case "left":
						try {
							left(temp[1]);
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						break;
					}
				}
			}
		});
		btnGoAll.setBounds(358, 159, 89, 23);
		btnGoAll.setEnabled(false);
		contentPane.add(btnGoAll);

		final JButton btnHome = new JButton("");
		btnHome.setIcon(new ImageIcon(EV3ControlGUI1.class.getResource("/com/sun/java/swing/plaf/windows/icons/HomeFolder.gif")));
		btnHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				goHome();
			}
		});
		btnHome.setBounds(325, 194, 51, 23);
		contentPane.add(btnHome);


		final JToggleButton tglbtnPreset = new JToggleButton("Preset");
		tglbtnPreset.setBounds(171, 228, 121, 23);
		tglbtnPreset.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				if (tglbtnPreset.isSelected()){
					btnRemove.setEnabled(true);
					btnGoAll.setEnabled(true);
					btnHome.setEnabled(true);
				} else if (tglbtnPreset.isSelected() == false){
					btnRemove.setEnabled(false);
					btnGoAll.setEnabled(false);
					btnHome.setEnabled(false);
				}
			}
		});
		contentPane.add(tglbtnPreset);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(263, 9, 161, 136);
		contentPane.add(scrollPane);


		scrollPane.setViewportView(list);
		list.setModel(new AbstractListModel<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public int getSize() {
				return values.size();
			}
			public String getElementAt(int index) {
				return values.get(index);
			}
		});

		JButton btnForward = new JButton("Forward");
		btnForward.setBounds(60, 72, 112, 29);
		btnForward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(tglbtnPreset.isSelected()){
					values.add("forward " + txtDistance.getText());
					updateCommands();
				}else if(tglbtnPreset.isSelected() == false){
					try {
						forward(txtDistance.getText());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		contentPane.setLayout(null);
		contentPane.add(btnForward);

		JButton btnClose = new JButton("close");
		btnClose.setBounds(10, 228, 81, 23);
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//String IP = "192.168.1.152";
				try{
					Socket socket = new Socket (IP, EV3Server.port);	
					OutputStream out = socket.getOutputStream();
					DataOutputStream dOut = new DataOutputStream(out);
					dOut.writeUTF("0");
					socket.close();

				}catch(IOException e1){

				}
			}
		});
		contentPane.add(btnClose);

		txtDistance = new JTextField();
		txtDistance.setBounds(10, 27, 114, 34);
		contentPane.add(txtDistance);
		txtDistance.setColumns(10);

		JButton btnLeft = new JButton("Left");
		btnLeft.setBounds(10, 116, 98, 29);
		btnLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(tglbtnPreset.isSelected()){
					values.add("left " + txtDegrees.getText());
					updateCommands();
				}else if(tglbtnPreset.isSelected() == false){
					try {
						left(txtDegrees.getText());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		contentPane.add(btnLeft);

		txtDegrees = new JTextField();
		txtDegrees.setBounds(139, 27, 109, 34);
		contentPane.add(txtDegrees);
		txtDegrees.setColumns(10);

		JButton btnBackward = new JButton("Backward");
		btnBackward.setBounds(60, 156, 112, 29);
		btnBackward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(tglbtnPreset.isSelected()){
					values.add("backward " + txtDistance.getText());
					updateCommands();
				}else if(tglbtnPreset.isSelected() == false){
					try {
						backward(txtDistance.getText());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		});
		contentPane.add(btnBackward);

		JButton btnRight = new JButton("Right");
		btnRight.setBounds(118, 116, 98, 29);
		btnRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnPreset.isSelected()){
					values.add("right " + txtDegrees.getText());
					updateCommands();
				}else if(tglbtnPreset.isSelected() == false){
					try {
						right(txtDegrees.getText());
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		contentPane.add(btnRight);

		JLabel lblDistance = new JLabel("Distance (cm) :");
		lblDistance.setBounds(10, 11, 114, 14);
		contentPane.add(lblDistance);

		JLabel lblDegrees = new JLabel("Degrees:");
		lblDegrees.setBounds(139, 11, 70, 14);
		contentPane.add(lblDegrees);

	}

	
	/**
	 * Moves the EV3 forward in a straight line.
	 * @param dist The distance in cm to travel forward.
	 * @throws IOException
	 */
	public void forward(String dist) throws IOException{

		boolean done = false;

		Socket socket = new Socket (IP, EV3Server.port);	
		OutputStream out = socket.getOutputStream();
		DataOutputStream dOut = new DataOutputStream(out);

		InputStream in = socket.getInputStream();
		DataInputStream dIn = new DataInputStream(in);

		dOut.writeUTF("1");
		dOut.writeUTF("forward");
		dOut.writeUTF(dist);

		while(!done){

			String wait = dIn.readUTF();

			if (wait.equals("done")){
				done = true;
			}
		}
		socket.close();
	}
	
	
	/**
	 * Moves the EV3 backwards in a straight line.
	 * @param dist The distance in cm to travel backward.
	 * @throws IOException
	 */
	public void backward(String dist) throws IOException{
		boolean done = false;

		Socket socket = new Socket (IP, EV3Server.port);	
		OutputStream out = socket.getOutputStream();
		DataOutputStream dOut = new DataOutputStream(out);

		InputStream in = socket.getInputStream();
		DataInputStream dIn = new DataInputStream(in);

		dOut.writeUTF("1");
		dOut.writeUTF("back");
		dOut.writeUTF(dist);

		while(!done ){

			String wait = dIn.readUTF();

			if (wait.equals("done")){
				done = true;
			}
		}
		socket.close();
	}
	
	
	/**
	 * Rotates the EV3 an angle right (clockwise).
	 * @param degrees The angle in degrees to rotate.
	 * @throws IOException
	 */
	public void right(String degrees) throws IOException{
		boolean done = false;

		Socket socket = new Socket (IP, EV3Server.port);	
		OutputStream out = socket.getOutputStream();
		DataOutputStream dOut = new DataOutputStream(out);

		InputStream in = socket.getInputStream();
		DataInputStream dIn = new DataInputStream(in);

		dOut.writeUTF("turn");
		dOut.writeUTF("right");
		dOut.writeUTF(degrees);

		while(!done ){

			String wait = dIn.readUTF();

			if (wait.equals("done")){
				done = true;
			}
		}
		socket.close();
	}


	/**
	 * Rotates the EV3 an angle left (counter-clockwise).
	 * @param degrees The angle in degrees to rotate.
	 * @throws IOException
	 */
	public void left(String degrees) throws IOException{
		boolean done = false;

		Socket socket = new Socket (IP, EV3Server.port);	
		OutputStream out = socket.getOutputStream();
		DataOutputStream dOut = new DataOutputStream(out);

		InputStream in = socket.getInputStream();
		DataInputStream dIn = new DataInputStream(in);

		dOut.writeUTF("turn");
		dOut.writeUTF("left");
		dOut.writeUTF(degrees);

		while(!done){

			String wait = dIn.readUTF();

			if (wait.equals("done")){
				done = true;
			}
		}

		socket.close();
	}


	/**
	 * This function updates the listbox containing
	 * the list of instructions.
	 */
	public void updateCommands(){
		list.setModel(new AbstractListModel<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public int getSize() {
				return values.size();
			}
			public String getElementAt(int index) {
				return values.get(index);
			}
		});
	}

	/**
	 * This function processes the list of instructions 
	 * entered by the user and calls on the appropriate 
	 * functions (opposite in this case, in order to 
	 * return to a "home") to execute the instructions.
	 */
	public void goHome(){

		for(int x = 0; x < values.size(); x++){
			String command = values.get(x);
			String[] temp = command.split(" ");
			String action = temp[0];
			switch (action){
			case "forward":
				try {
					backward(temp[1]);
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				break;
			case "backward":
				try {
					forward(temp[1]);
				} catch (IOException e2) {
					e2.printStackTrace();
					break;
				}
			case "right":
				try {
					left(temp[1]);
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				break;
			case "left":
				try {
					right(temp[1]);
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				break;
			}

		}
	}
}
