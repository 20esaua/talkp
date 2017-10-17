import java.io.*;
import java.util.*;
import java.net.*;
import org.json.*;

public class Client {
    private Server server = null;
    private Socket socket = null;

    private BufferedReader reader = null;
    private PrintWriter writer = null;
    private Stack<String> stack = new Stack<>();

    public boolean running = false;
    public String username = "";

    public Client(Server server, Socket socket) throws Exception {
        this.socket = socket;
        this.server = server;

        try {
            this.reader = new BufferedReader(new InputStreamReader(this.getSocket().getInputStream()));
            this.writer = new PrintWriter(this.getSocket().getOutputStream(), true);
        } catch(Exception e) {
            Logger.warn("Failed to accept new client!");
            e.printStackTrace();
        }
    }

    public void start() {
        if(!this.running) {
            this.running = true;
            // read
            new Thread(new Runnable() {public void run() {
                try {
                    while(running) {
                        String input = reader.readLine();
                        if(input != null)
                            handle(input);
                        else
                            stop();
                    }
                } catch(Exception e) {
                    Logger.warn("Kicking client " + username + "");
                    e.printStackTrace();
                    stop();
                }
            }}).start();

            // write
            new Thread(new Runnable() {public void run() {
                try {
                    while(running) {
                        while(stack.empty() && running)
                            Thread.sleep(2);

                        if(running) {
                            writer.println(stack.pop());
                            writer.flush();
                        }
                    }
                } catch(Exception e) {
                    Logger.warn("Kicking client " + username + "");
                    e.printStackTrace();
                    stop();
                }
            }}).start();
        }
    }

    /*
     * [Protocols for receiving]
     * 0 = online, username = status update on user
     * 1 = message = receive a message
     *
     * [Protocols for sending]
     * 0 = online, username = status update on user
     * 1 = username, message = send a message
     * 2 = message = server going down for [message] reason
     */

    public void handle(String input) {
        try {
            JSONObject o = new JSONObject(input);
            if(username.length() == 0 && o.getInt("type") == 0) {
                String tmp = o.getString("username").toLowerCase().replaceAll("[^A-Za-z0-9]", "").trim().substring(0, Math.min(Server.MAX_USERNAME_LENGTH, o.getString("username").trim().length())); // limit check string & remove nonalphanumeric chars
                if(tmp.length() != 0) {
                    boolean valid = true;
                    for(Client client : this.getServer().getClients()) // check if username is taken
                        if(tmp.equals(client.username))
                            valid = false;
                    if(!valid) {
                        int i = 0;
                        String tmp2 = tmp;

                        while(!valid) {
                            valid = true;
                            tmp2 = tmp + i;
                            for(Client client : this.getServer().getClients()) // check if username is taken
                                if(tmp2.equals(client.username))
                                    valid = false;
                            i++;
                        }

                        tmp = tmp2;
                    }

                    username = tmp;

                    Logger.broadcast(username + " joined the chat.");
                    this.getServer().writeAll(new JSONObject().put("type", 0).put("username", username).put("online", true));
                }
            } else {
                if(username.length() != 0) {
                    if(o.getInt("type") == 1) {
                        String msg = o.getString("message").trim().substring(0, Math.min(Server.MAX_MESSAGE_LENGTH, o.getString("message").trim().length()));
                        Logger.msg(username, msg);
                        this.getServer().writeAll(new JSONObject().put("type", 1).put("username", username).put(
                            "message", msg
                        ));
                    }
                }
            }
        } catch(Exception e) {
            Logger.warn("Encountered unknown error. Kicking client.");
            e.printStackTrace();
            stop();
        }
    }

    public void write(Object o) {
        this.stack.push(o.toString());
    }

    private boolean exit = false;

    public void stop() {
        try {
            this.getSocket().close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        this.running = false;
        this.getServer().getClients().remove(this);

        if(username.length() != 0 && !exit) {
            exit = true;
            this.getServer().writeAll(new JSONObject().put("type", 0).put("username", username).put("online", false));
            Logger.broadcast(username + " left the chat.");
        }
    }

    public Server getServer() {
        return this.server;
    }

    public Socket getSocket() {
        return this.socket;
    }
}
