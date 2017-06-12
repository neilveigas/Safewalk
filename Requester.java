
package safewalk;
import java.util.Scanner;

import edu.purdue.cs.cs180.channel.*;


public class Requester implements MessageListener {

	Channel st;
	boolean be = false;
	Scanner s = new Scanner(System.in);

	public Requester(Channel st)
	{
		this.st = st;
		st.setMessageListener(this);
		action();
		//int i = 0;
		//    String input = null;
		//    Scanner s = new Scanner(System.in);
		//    while (true) {
		//      if (be == false) {
		//        menu();
		//        System.out.printf("Enter your location (1-5): ");
		//        
		//        input = s.nextLine();
		//        
		//        try 
		//        {
		//          int numInput = Integer.parseInt(input);
		//          //System.out.println("IN TRY");
		//          if (numInput > 0 && numInput < 6)
		//          {
		//            String loc = locationAb(numInput);
		//            String messg = "REQUEST " + loc;
		//            
		//            System.out.printf("Waiting for volunteer...\n");
		//            be = true;
		//            try{
		//              st.sendMessage(messg); 
		//              //break;
		//            } catch(ChannelException e){
		//              e.printStackTrace();
		//            }
		////                    try {
		////                        Thread.sleep(2000);
		////                    } catch (InterruptedException e) {
		////                        e.printStackTrace();
		////                    }
		//            // recieve id from server, send id and message as parameters to the server recievmessage method
		//          }
		//          
		//          else
		//          {
		//            System.out.println("Invalid input. Please try again.");
		//            
		//          }
		//        }
		//        catch (Exception e) {
		//          System.out.println("Invalid number format. Please try again.");
		//        }
		//        
		//        
		//      }
		//      System.out.printf("");
		//    }
		//    


	}

	@Override
	public void messageReceived(String message, int clientID) {
		int id = Integer.parseInt(message.substring(10));
		System.out.printf("Volunteer %d assigned and will arrive shortly.\n" , id);
		action();
		// TODO Auto-generated method stub

	}

	static void menu()
	{
		String menu = String.format("1. CL50 - Class of 1950 Lecture Hall\n") + 
				String.format("2. EE - Electrical Engineering Building\n") +
				String.format("3. LWSN - Lawson Computer Science Building\n") +
				String.format("4. PMU - Purdue Memorial Union\n") +
				String.format("5. PUSH - Purdue University Student Health Center");
		System.out.println(menu);
	}

	static String locationAb(int input)
	{
		String loc = "";
		switch (input) {
			case 1:
				loc = "CL50";
				break;
			case 2: 
				loc = "EE";
				break;
			case 3: 
				loc = "LWSN";
				break;
			case 4: 
				loc = "PMU";
				break;
			case 5:
				loc = "PUSH";
				break;
		}
		return loc; 
	}

	//    public void sendMessage (String message) throws ChannelException {
	//        // send a message, since we did not specify a client ID, then the
	//        // message will be sent to the server.
	//        try {
	//            dr.sendMessage(message);
	//        } catch (ChannelException e) {
	//            e.printStackTrace();
	//        }
	//    }
	//
	public void action() {
		String input = null;
		while (true) {

			menu();
			System.out.printf("Enter your location (1-5): ");
			if (s.hasNextLine()) {

				input = s.nextLine();

				try 
				{
					int numInput = Integer.parseInt(input);
					//System.out.println("IN TRY");
					if (numInput > 0 && numInput < 6)
					{
						String loc = locationAb(numInput);
						String messg = "REQUEST " + loc;
						/////this needs to be a requester object, but where do we make the object?
						System.out.printf("Waiting for volunteer...\n");
						try {
							st.sendMessage(messg); 
							break;
						} catch (ChannelException e) {
							e.printStackTrace();
						}
						//	                    try {
						//	                        Thread.sleep(2000);
						//	                    } catch (InterruptedException e) {
						//	                        e.printStackTrace();
						//	                    }
						// recieve id from server, send id and message as parameters to the server recievmessage method
					}

					else
					{
						System.out.println("Invalid input. Please try again.");

					}
				}
				catch (Exception e) {
					System.out.println("Invalid number format. Please try again.");
					continue;
				}


			}
			else {
				break;
			}
		}
	}
	public static void main(String[] args)
	{
		try {
			Channel ne = new TCPChannel(args[0], Integer.parseInt(args[1]));
			new Requester(ne);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
