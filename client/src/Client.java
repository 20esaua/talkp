import java.io.*;
import java.util.*;
import java.net.*;
import org.json.*;

public class Client {
    private String username = System.getProperty("user.name");
    private String host = "localhost";
    private int port = 25678;
    private boolean running = false;

    private Scanner scanner = null;
    private Socket socket = null;
    private BufferedReader reader = null;
    private PrintWriter writer = null;
    private Stack<String> stack = new Stack<>();

    public Client(String user, String host, int port) {
        Logger.info("Connecting to " + host + " on port " + port + " as " + user + "...");

        this.username = user;
        this.host = host;
        this.port = port;
    }

    public void start() {
        if(!this.isRunning()) {
            this.running = true;
            try {
                scanner = new Scanner(System.in);
                socket = new Socket(this.getHost(), this.getPort());
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                bar();

                new Thread(new Runnable() {public void run() {try {while(Client.this.isRunning()) {
                    // read
                    String input = reader.readLine();
                    if(input != null)
                        handle(input);
                    else
                        stop();
                }} catch(Exception e) {Logger.err("Unknown error encountered. Exiting!" + Color.RESET); e.printStackTrace(); stop();}}}).start();

                new Thread(new Runnable() {public void run() {try {while(Client.this.isRunning()) {
                    // write
                    while(stack.empty() && running)
                        Thread.sleep(10);

                    if(running) {
                        writer.println(stack.pop());
                        writer.flush();
                    }
                }} catch(Exception e) {Logger.err("Unknown error encountered. Exiting!" + Color.RESET); e.printStackTrace(); stop();}}}).start();

                // login
                write(new JSONObject().put("type", 0).put("username", this.getUsername()).put("online", true));

                new Thread(new Runnable() {public void run() {try {while(Client.this.isRunning()) {
                    // keyboard
                    String line = scanner.nextLine();
                    if(line != null && line.trim().length() != 0) {
                        if(line.toLowerCase().trim().startsWith("/exit"))
                            stop();
                        sendMessage(line);
                    }
                }} catch(Exception e) {Logger.err("Unknown error encountered. Exiting!"); e.printStackTrace(); stop();}}}).start();

                this.running = true;
            } catch(Exception e) {
                Logger.err("Failed to connect to server.");
            }
        }
    }

    public void clear() {
        System.out.print("\u001b[2J\u001b[H");
        System.out.flush();
    }

    public void bar() {
        System.out.println(Color.BOLD + Color.BLUE + new String(new char[80]).replace("\0", "="));
    }

    public void stop() {
        try {
            this.socket.close();
            //this.scanner.close();
        } catch(Exception e) {
            Logger.err("Failed to properly shut down sockets. Exiting!" + Color.RESET);
            e.printStackTrace();
            System.exit(1);
        }

        this.running = false;

        bar();

        System.exit(0);
    }

    public void handle(String input) {
        JSONObject obj = new JSONObject(input);

        switch(obj.getInt("type")) {
            case 0:
                Logger.broadcast(obj.getString("username") + " " + (obj.getBoolean("online") ? "joined" : "left") + " the chat.");
                break;
            case 1:
                if(obj.getString("message").equalsIgnoreCase("/clear")) {
                    clear();
                    Logger.msg(obj.getString("username"), Color.ITALIC + "Cleared the screen." + Color.RESET);
                } else if(obj.getString("message").toLowerCase().startsWith("/notify")) {
                    try {
                        String msg = obj.getString("message").substring(7).trim();
                        Runtime.getRuntime().exec(new String[] {"notify-send", obj.getString("username") + ": " + (msg.length() == 0 ? "New message" : msg)});
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                } else
                    Logger.msg(obj.getString("username"), obj.getString("message"));
                break;
            case 2:
                Logger.warn(obj.getString("message"));
                stop();
                break;
            case 3:
                // parameter `public` is key
                break;
        }
    }

    public void sendMessage(Object message) {
        write(new JSONObject().put("type", 1).put("message", message.toString()));
    }

    public void write(Object o) {
        this.stack.push(o.toString());
    }

    public boolean isRunning() {
        return this.running;
    }

    public String getUsername() {
        return this.username;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }
}
