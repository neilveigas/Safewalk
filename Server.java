/**
 * Project 6
 * @author nveigas
 * @author jfoeh
 */

package safewalk;
import java.util.*;

import edu.purdue.cs.cs180.channel.*;

public class Server implements MessageListener {

	LinkedList<String> vols = new LinkedList<String>();
	LinkedList<String> reqs = new LinkedList<String>();
	LinkedList<String> urges = new LinkedList<String>();
	LinkedList<Integer> reqid = new LinkedList<Integer>();
	LinkedList<Integer> volid = new LinkedList<Integer>();
	Channel forlan = null;
	int inter;
	Object lock = new Object();

	public Server(Channel forlan, int inter)
	{
		this.inter = inter;
		this.forlan = forlan;
		forlan.setMessageListener(this);
	}

	@Override
	public void messageReceived(String message, int clientID) {
		System.out.println("the message received by the server is" + message + " " + clientID);
		synchronized (lock) {
			if (message.indexOf("REQUEST") != -1) {
				int space1 = message.indexOf(" ");
				int space2 = message.lastIndexOf(" ");
				String rbldg = message.substring(space1 + 1, space2);
				String urg = message.substring(space2 + 1);

				reqs.add(rbldg);
				reqid.add(clientID);
				urges.add(urg);
				System.out.println("after adding the size of the reqs and vols is" +
						reqs.size() + " " + vols.size());

			}
			else {
				String vbldg = message.substring(10);
				vols.add(vbldg);
				volid.add(clientID);
				System.out.println("after adding the size of the reqs and vols is" +
						reqs.size() + " " + vols.size());


			}
		}
	}
	public void run() {
		while (true) {
			try {
				Thread.sleep(inter);
				while (reqs.size() != 0 && vols.size() != 0) {
					if (urges.size() > 0 && vols.size() > 0) {
						synchronized (lock) {
							int emergInd = 0;
							if (urges.contains("EMERGENCY")) {
								System.out.println("its going in here");
								for (int k = 0; k < urges.size(); k++) {
									if (urges.get(k).equals("EMERGENCY")) { 
										emergInd = k;
										System.out.println("its getting requester" + reqs.get(emergInd) +
												"with emergency of" + urges.get(emergInd));
										break;

									}
								}
							}
							else if (urges.contains("URGENT")) {
								for (int k = 0; k < urges.size(); k++) {
									if (urges.get(k).equals("URGENT")) { 
										emergInd = k;
										break;
									}
								}
							}
							else if (urges.contains("NORMAL")) {
								for (int k = 0; k < urges.size(); k++) {
									if (urges.get(k).equals("NORMAL")) { 
										emergInd = k;
										break;
									}
								}
							}

							if (vols.size() > 0) {
								int[] vdists = new int[vols.size()];
								int shortIndex = 0;
								for (int i = 0; i < vols.size(); i++) {
									vdists[i] = getDist(reqs.get(emergInd), vols.get(i));
								}
								for (int j = 0; j < vdists.length; j++) {
									if (vdists[shortIndex] > vdists[j]) {
										shortIndex = j;
									}
								}
								try {
									forlan.sendMessage("VOLUNTEER " + volid.get(shortIndex) + " " +
											vdists[shortIndex], reqid.get(emergInd)); //to requester
									forlan.sendMessage("LOCATION " + reqs.get(emergInd) + " " +
											urges.get(emergInd), volid.get(shortIndex)); //to volunteer
									System.out.println("2req VOLUNTEER " + volid.get(shortIndex) +
											" " + vdists[shortIndex] + " " + reqid.get(emergInd));
									System.out.println("2vol LOCATION " + reqs.get(emergInd) + " " +
											urges.get(emergInd) + " " + volid.get(shortIndex));
								} catch (ChannelException e) {
									e.printStackTrace();
								}
								vols.remove(shortIndex);
								volid.remove(shortIndex);
								reqs.remove(emergInd);
								reqid.remove(emergInd);
								urges.remove(emergInd);
							}

						}				
					}
					System.out.println("after the alg and removing the size of the reqs and vols is" +
							reqs.size() + " " + vols.size());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
	public int getNumberRequesters() {
		synchronized (lock) {
			return reqs.size();
		}
	}
	public int getNumberVolunteers() {
		synchronized (lock) {
			return vols.size();
		}
	}

	//	public void fcfs (String message, int clientID) {
	//		if (message.indexOf("REQUEST") != -1) {
	//			int space1 = message.indexOf(" ");
	//			int space2 = message.lastIndexOf(" ");
	//			String rbldg = message.substring(space1 + 1, space2);
	//			String urg = message.substring(space2 + 1);
	//			if (vols.size() > 0) {
	//				try {
	//					forlan.sendMessage("LOCATION " + rbldg + " " + urg , volid.get(0)); //sent to volun
	//					forlan.sendMessage("VOLUNTEER " + volid.get(0) + " " + getDist(rbldg, vols.get(0)), clientID);
	//					System.out.println("req fcfs2vol LOCATION " + rbldg + " " + urg + " " + volid.get(0));
	//					System.out.println("req fcfs2req VOLUNTEER " + volid.get(0) + " " +
	//							getDist(rbldg, vols.get(0)) + " " + clientID);
	//
	//				} catch (ChannelException e) {
	//					e.printStackTrace();
	//				}
	//				vols.remove(0);
	//				volid.remove(0);
	//			}
	//			else {
	//				reqs.add(rbldg);
	//				reqid.add(clientID);
	//				urges.add(urg);
	//			}
	//		}
	//		else if (message.indexOf("VOLUNTEER") != -1) {
	//
	//			String vbldg = message.substring(10);
	//			if (reqs.size() > 0) {
	//				String bldg = reqs.get(0);
	//				try {
	//					forlan.sendMessage("LOCATION " + bldg + " " + urges.get(0), clientID);
	//					forlan.sendMessage("VOLUNTEER " + clientID + " " + getDist(bldg, vbldg), reqid.get(0));
	//					System.out.println("vol fcfs2vol LOCATION " + bldg + " " + urges.get(0) + " " + clientID);
	//					System.out.println("vol fcfs2req VOLUNTEER " + clientID + " " +
	//							getDist(bldg, vbldg) + " " + reqid.get(0));
	//				} catch (ChannelException e) {
	//					e.printStackTrace();
	//				}
	//				reqs.remove(0);
	//				reqid.remove(0);
	//				urges.remove(0); ////correction here
	//			}
	//			else {
	//				vols.add(vbldg);
	//				volid.add(clientID);
	//			}
	//		}
	//	}
	//
	//	public void closest (String message, int clientID) {
	//		if (message.indexOf("REQUEST") != -1) {
	//			int space1 = message.indexOf(" ");
	//			int space2 = message.lastIndexOf(" ");
	//			String rbldg = message.substring(space1 + 1, space2);
	//			String urg = message.substring(space2 + 1);
	//			System.out.println(rbldg);
	//			if (vols.size() > 0) {
	//				int[] vdists = new int[vols.size()];
	//				int shortIndex = 0;
	//				for (int i = 0; i < vols.size(); i++) {
	//					vdists[i] = getDist(rbldg, vols.get(i));
	//				}
	//				for (int j = 0; j < vdists.length; j++) {
	//					if (vdists[shortIndex] > vdists[j]) {
	//						shortIndex = j;
	//					}
	//				}
	//				try {
	//					forlan.sendMessage("VOLUNTEER " + volid.get(shortIndex) + " " +
	//							vdists[shortIndex], clientID); 
	//					forlan.sendMessage("LOCATION " + rbldg + " " + urg, volid.get(shortIndex));
	//					System.out.println("req clos2req VOLUNTEER " + volid.get(shortIndex) +
	//							" " + vdists[shortIndex] + " " + clientID);
	//					System.out.println("req close2vol LOCATION " + rbldg + " " + urg + " " + volid.get(shortIndex));
	//				} catch (ChannelException e) {
	//					e.printStackTrace();
	//				}
	//				vols.remove(shortIndex);
	//				volid.remove(shortIndex);
	//			}
	//			else {
	//				reqs.add(rbldg);
	//				reqid.add(clientID);
	//				urges.add(urg);
	//				/////
	//			}
	//		}
	//		else if (message.indexOf("VOLUNTEER") != -1) {
	//			int space1 = message.indexOf(" ");
	//			String vbldg = message.substring(space1 + 1);
	//			System.out.println(vbldg);
	//			if (reqs.size() > 0) {
	//				int[] rdists = new int[reqs.size()];
	//				int shortIndex = 0;
	//				for (int i = 0; i < reqs.size(); i++) {
	//					rdists[i] = getDist(vbldg, reqs.get(i));
	//				}
	//				for (int j = 0; j < rdists.length; j++) {
	//					if (rdists[shortIndex] > rdists[j]) {
	//						shortIndex = j;
	//					}
	//				}
	//				try {
	//					forlan.sendMessage("VOLUNTEER " + clientID + " " +
	//							rdists[shortIndex], reqid.get(shortIndex));
	//					forlan.sendMessage("LOCATION " + reqs.get(shortIndex) +
	//							" " + urges.get(shortIndex), clientID);
	//					System.out.println("vol close2req VOLUNTEER " + clientID + " " +
	//							rdists[shortIndex] + " " + reqid.get(shortIndex));
	//					System.out.println("vol close2vol LOCATION " + reqs.get(shortIndex) +
	//							" " + urges.get(shortIndex) + " " + clientID);
	//				} catch (ChannelException e) {
	//					e.printStackTrace();
	//				}
	//				reqs.remove(shortIndex);
	//				reqid.remove(shortIndex);
	//				urges.remove(shortIndex);
	//			}
	//			else {
	//				vols.add(vbldg);
	//				volid.add(clientID);
	//			}
	//
	//
	//		}
	//	}
	//
	//	public void urgency (String message, int clientID) {
	//		if (message.indexOf("REQUEST") != -1) {
	//			int space1 = message.indexOf(" ");
	//			int space2 = message.lastIndexOf(" ");
	//			String rbldg = message.substring(space1 + 1, space2);
	//			String urg = message.substring(space2 + 1);
	//			if (vols.size() > 0) {
	//				try {
	//					forlan.sendMessage("LOCATION " + rbldg + " " + urg , volid.get(0)); 
	//					forlan.sendMessage("VOLUNTEER " + volid.get(0) + " " +
	//							getDist(rbldg, vols.get(0)), clientID);
	//					System.out.println("req urg2vol LOCATION " + rbldg + " " + urg + " " + volid.get(0));
	//					System.out.println("req urg2req VOLUNTEER " + volid.get(0) +
	//							" " + getDist(rbldg, vols.get(0)) + " " + clientID);
	//				} catch (ChannelException e) {
	//					e.printStackTrace();
	//				}
	//				vols.remove(0);
	//				volid.remove(0);
	//			}
	//			else {
	//				reqs.add(rbldg);
	//				reqid.add(clientID);
	//				urges.add(urg);
	//			}
	//		}
	//		else if (message.indexOf("VOLUNTEER") != -1) {
	//			String vbldg = message.substring(10);
	//			if (reqs.size() > 0) {
	//				if (urges.contains("EMERGENCY")) { //made it uppercase
	//					int emergInd = 0;
	//					for (int k = 0; k < urges.size(); k++) {
	//						if (urges.get(k).equals("EMERGENCY")) { //made it uppercase
	//							emergInd = k;
	//							break;
	//						}
	//					}
	//					try {
	//						forlan.sendMessage("LOCATION " + reqs.get(emergInd) + " EMERGENCY", clientID);
	//						forlan.sendMessage("VOLUNTEER " + clientID + " " +
	//								getDist(reqs.get(emergInd), vbldg), reqid.get(emergInd));
	//						System.out.println("vol urg2vol LOCATION " + reqs.get(emergInd) +
	//								" EMERGENCY" + " " + clientID);
	//						System.out.println("vol urg2req VOLUNTEER " + clientID + " " +
	//								getDist(reqs.get(emergInd), vbldg) + " " + reqid.get(0));
	//					} catch (ChannelException e) {
	//						e.printStackTrace();
	//					}
	//					reqs.remove(emergInd);
	//					reqid.remove(emergInd);
	//					urges.remove(emergInd);
	//				}
	//				else if (urges.contains("URGENT")) { //made it uppercase
	//					int emergInd = 0;
	//					for (int k = 0; k < urges.size(); k++) {
	//						if (urges.get(k).equals("URGENT")) { // made it uppercase
	//							emergInd = k;
	//							break;
	//						}
	//					}
	//					try {
	//						forlan.sendMessage("LOCATION " + reqs.get(emergInd) + " URGENT", clientID);
	//						forlan.sendMessage("VOLUNTEER " + clientID + " " +
	//								getDist(reqs.get(emergInd), vbldg), reqid.get(emergInd));
	//						System.out.println("vol urg2vol LOCATION " + reqs.get(emergInd) +
	//								" URGENT" + " " + clientID);
	//						System.out.println("vol urg2req VOLUNTEER " + clientID +
	//								" " + getDist(reqs.get(emergInd), vbldg) + " " + reqid.get(0));
	//					} catch (ChannelException e) {
	//						e.printStackTrace();
	//					}
	//					reqs.remove(emergInd);
	//					reqid.remove(emergInd);
	//					urges.remove(emergInd);
	//				}
	//				else if (urges.contains("NORMAL")) { //made it uppercase
	//					int emergInd = 0;
	//					for (int k = 0; k < urges.size(); k++) {
	//						if (urges.get(k).equals("NORMAL")) {
	//							emergInd = k;
	//							break;
	//						}
	//					}
	//					try {
	//						forlan.sendMessage("LOCATION " + reqs.get(emergInd) + " NORMAL", clientID);
	//						forlan.sendMessage("VOLUNTEER " + clientID + " " +
	//								getDist(reqs.get(emergInd), vbldg), reqid.get(emergInd));
	//						System.out.println("vol urg2vol LOCATION " +
	//reqs.get(emergInd) + " NORMAL" + " " + clientID);
	//						System.out.println("vol urg2req VOLUNTEER " + clientID +
	//								" " + getDist(reqs.get(emergInd), vbldg) + " " + reqid.get(0));
	//					} catch (ChannelException e) {
	//						e.printStackTrace();
	//					}
	//					reqs.remove(emergInd);
	//					reqid.remove(emergInd);
	//					urges.remove(emergInd);
	//				}
	//			}
	//			else {
	//				vols.add(vbldg);
	//				volid.add(clientID);
	//			}
	//		}
	//	}


	public int getDist(String loc1, String loc2) {
		if (loc1.equals("CL50") && loc2.equals("CL50")) {
			return 0;
		}
		else if ((loc1.equals("EE") && loc2.equals("CL50")) || (loc1.equals("CL50") && loc2.equals("EE")) ) {
			return 8;
		}
		else if ((loc1.equals("LWSN") && loc2.equals("CL50")) || (loc1.equals("CL50") && loc2.equals("LWSN")) ) {
			return 6;
		}
		else if ((loc1.equals("PMU") && loc2.equals("CL50")) || (loc1.equals("CL50") && loc2.equals("PMU")) ) {
			return 5;
		}
		else if ((loc1.equals("PUSH") && loc2.equals("CL50")) || (loc1.equals("CL50") && loc2.equals("PUSH")) ) {
			return 4;
		}
		else if (loc1.equals("EE") && loc2.equals("EE")) {
			return 0;
		}
		else if ((loc1.equals("EE") && loc2.equals("LWSN")) || (loc1.equals("LWSN") && loc2.equals("EE")) ) {
			return 4;
		}
		else if ((loc1.equals("EE") && loc2.equals("PMU")) || (loc1.equals("PMU") && loc2.equals("EE")) ) {
			return 2;
		}
		else if ((loc1.equals("EE") && loc2.equals("PUSH")) || (loc1.equals("PUSH") && loc2.equals("EE")) ) {
			return 5;
		}
		else if (loc1.equals("LWSN") && loc2.equals("LWSN")) {
			return 0;
		}
		else if ((loc1.equals("LWSN") && loc2.equals("PMU")) || (loc1.equals("PMU") && loc2.equals("LWSN")) ) {
			return 3;
		}
		else if ((loc1.equals("LWSN") && loc2.equals("PUSH")) || (loc1.equals("PUSH") && loc2.equals("LWSN")) ) {
			return 1;
		}
		else if (loc1.equals("PMU") && loc2.equals("PMU")) {
			return 0;
		}
		else if ((loc1.equals("PMU") && loc2.equals("PUSH")) || (loc1.equals("PUSH") && loc2.equals("PMU")) ) {
			return 7;
		}
		else if (loc1.equals("PUSH") && loc2.equals("PUSH")) {
			return 0;
		}


		return -1;
	}


	public static void main(String[] args) {        

		Channel myCh = null;
		int port = Integer.parseInt(args[0]);

		try
		{
			myCh = new TCPChannel(port);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		Server s1 = new Server(myCh, Integer.parseInt(args[1]));
		s1.run();
		System.out.println("the server is " + " " + port + " " + Integer.parseInt(args[1])); 

	}

}
