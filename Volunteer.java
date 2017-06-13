//package edu.purdue.cs.cs180.channel;
package safewalk;
import java.util.Scanner;
import edu.purdue.cs.cs180.channel.*;

public class Volunteer implements MessageListener {

	//int id;
	Channel st;
	boolean iz = false;

	public Volunteer(Channel st) {
		this.st = st;
		st.setMessageListener(this);
		action();
		
	}

	@Override
	public void messageReceived(String message, int clientID) {
		String bldg = message.substring(9);
		System.out.printf("Proceed to %s\n", bldg);
		action();
		//System.out.println(" in received " + iz);

	}
	
	public void action() {

		System.out.println("Press ENTER when ready:");

		if (new Scanner(System.in).nextLine().equals("")) 
		{
			//System.out.println("Waiting")
			String messg = "VOLUNTEER " + st.getID();
			//System.out.printf(" before send " + iz);
			try {
				System.out.println("Waiting for assignment...");
				st.sendMessage(messg);
			}
			catch (ChannelException e) {
				e.printStackTrace();
			}
			//System.out.println(" after send " + iz);

			//	                try {
			//	                    Thread.sleep(2000);
			//	                } catch (InterruptedException e) {
			//	                    e.printStackTrace();
			//	                }
		}
	}
	public static void main(String[] args) {
		try {
			Channel ne = new TCPChannel(args[0], Integer.parseInt(args[1]));
			new Volunteer(ne);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
