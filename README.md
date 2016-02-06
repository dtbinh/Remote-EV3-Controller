# Remote-EV3-Controller
A client-server program to remotely control a LEGO EV3 robot.

The EV3Server class is run on an EV3 brick, in order to receive commands from EV3ControlGUI1 which includes 
several options for various movements of the brick, performed via use of <a href="http://www.lejos.org/ev3.php">leJos EV3.</a>

The user is able to either enter commands as they go, or enter in a list of commands in a 'Preset' mode,
which are then automatically executed consecutively.

Note: the EV3Server class should be run on the brick BEFORE running EV3ControlGUI1, and the 'close' button should be used after to terminate the connection.

![](/images/Interface1.png)

In 'Preset' mode, enter a list of commands to execute.
<br>
![](/images/Preset1.png)

Easily remove a command at any position in the list.
<br>
![](/images/Preset Remove.png)
