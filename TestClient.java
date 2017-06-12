
package safewalk;
import edu.purdue.cs.cs180.channel.*;
import java.util.*;
import java.io.*;

public class TestClient implements MessageListener {

    private final Channel channel;

    private TestClient (Channel channel) {
        this.channel = channel;
        channel.setMessageListener(this);
    }

    private static synchronized void printf (String str, Object... args) {
        System.out.printf(str, args);
    }

    public void sendMessage (String message) throws ChannelException {
        printf("[Client %3d]    Sending to server: %s\n", channel.getID(), message);
        channel.sendMessage(message);
    }

    @Override
    public void messageReceived (String message, int id) {
        printf("[Client %3d] Received from server: %s\n", id, message);
    }

    public static void main(String[] args) throws IOException {
        String addr = args[0];
        String port = args[1];
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.length() < 0)
                continue;
            try {
                TestClient tc = new TestClient(new TCPChannel(addr, Integer.parseInt(port)));
                tc.sendMessage(line);
                Thread.sleep(200);
            } catch (ChannelException e) {
                printf("Error connecting to server at %s:%s.\n", addr, port);
                System.exit(1);
            } catch (Exception e) {
                printf("Caught exception %s\n", e.getClass().getName());
                System.exit(1);
            }
        }
    }
}
