//package edu.purdue.cs.cs180.channel;
package safewalk;
import java.util.Scanner;
import edu.purdue.cs.cs180.channel.*;

public class Volunteer implements MessageListener {

	//int id;
	Channel sturridge;
	boolean izzat = false;

	public Volunteer(Channel sturridge) {
		this.sturridge = sturridge;
		sturridge.setMessageListener(this);
		action();
		//int i = 0;
		// while (true) {
		//      if (!izzat) {
		//        System.out.printf("Press ENTER when ready:");
		//        
		//        if (new Scanner(System.in).nextLine().equals("")) 
		//        {
		//          //System.out.println("Waiting")
		//          String messg = "VOLUNTEER " + sturridge.getID();
		//          //System.out.printf(" before send " + izzat);
		//          try{
		//            System.out.printf("Waiting for assignment...");
		//            izzat = true;
		//            sturridge.sendMessage(messg);
		//          }
		//          catch (ChannelException e) {
		//            e.printStackTrace();
		//          }
		//          //System.out.println(" after send " + izzat);
		//          
		////                try {
		////                    Thread.sleep(2000);
		////                } catch (InterruptedException e) {
		////                    e.printStackTrace();
		////                }
		//        }
		//      }
		//      System.out.printf("");
		//    }
	}

	@Override
	public void messageReceived(String message, int clientID) {
		String bldg = message.substring(9);
		System.out.printf("Proceed to %s\n", bldg);
		action();
		//System.out.println(" in received " + izzat);
		// TODO Auto-generated method stub

	}
	//    public void sendMessage (String message) throws ChannelException {
	//        // send a message, since we did not specify a client ID, then the
	//        // message will be sent to the server.
	//        String messg = message.substring(0,message.indexOf(" "));
	//        int id = Integer.parseInt(message.substring(message.indexOf(" ")));
	//        Server server = new Server();
	//        try {
	//            server.messageReceived(messg, id);
	//            //drogba.sendMessage(message);
	//        } catch (ChannelException e) {
	//            e.printStackTrace();
	//        }
	//    }
	public void action() {

		System.out.println("Press ENTER when ready:");

		if (new Scanner(System.in).nextLine().equals("")) 
		{
			//System.out.println("Waiting")
			String messg = "VOLUNTEER " + sturridge.getID();
			//System.out.printf(" before send " + izzat);
			try {
				System.out.println("Waiting for assignment...");
				sturridge.sendMessage(messg);
			}
			catch (ChannelException e) {
				e.printStackTrace();
			}
			//System.out.println(" after send " + izzat);

			//	                try {
			//	                    Thread.sleep(2000);
			//	                } catch (InterruptedException e) {
			//	                    e.printStackTrace();
			//	                }
		}
	}
	public static void main(String[] args) {
		try {
			Channel neymar = new TCPChannel(args[0], Integer.parseInt(args[1]));
			new Volunteer(neymar);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
