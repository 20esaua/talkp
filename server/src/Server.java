import java.util.*;
import java.io.*;
import java.net.*;
import org.json.*;

public class Server {
    public static final int MAX_USERNAME_LENGTH = 15;
    public static final int MAX_MESSAGE_LENGTH = 1000;

    private int port = 25678;
    private boolean running = false;
    private List<Client> clients = new ArrayList<>();

    private Scanner scanner = null;

    public Server(int port) {
        this.port = port;

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if(isRunning()) {
                    Server.this.stop();
                }
            }
        });
    }

    public void start() {
        if(!this.isRunning()) {
            this.running = true;

            new Thread(new Runnable() {
                public void run() {
                    try {
                        ServerSocket serverSocket = new ServerSocket(port);
                        Logger.info("Server starting...");
                        while(Server.this.isRunning()) {
                            try {
                                Socket socket = serverSocket.accept();
                                Client client = new Client(Server.this, socket);
                                Server.this.getClients().add(client);
                                // Logger.info("New client accepted at " + socket.getRemoteSocketAddress().toString() + ".");
                                client.start();
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            new Thread(new Runnable() {public void run() {try {while(isRunning()) {
                // keyboard
                try {
                    scanner = new Scanner(System.in);
                    String command = scanner.nextLine().toLowerCase().trim();
                    if(command.length() != 0) {
                        String[] split = command.split(" ");
                        switch(split[0]) {
                            case "help":
                            case "h":
                                Logger.info("Commands: help, exit, kick");
                                break;
                            case "exit":
                            case "x":
                                stop();
                                break;
                            case "kick":
                                if(split.length != 1) {
                                    for(Client client : getClients())
                                        if(client.username.equalsIgnoreCase(split[1])) {
                                            client.write(new JSONObject().put("type", 2).put("message", "You have been kicked from this server."));
                                            Thread.sleep(100);
                                            client.stop();
                                            break;
                                        }
                                } else
                                    Logger.info("Syntax: kick <user>");
                                break;
                        }
                    }
                } catch(Exception e2) {
                    e2.printStackTrace();
                }
            }} catch(Exception e) {Logger.err("Unknown error encountered. Exiting!"); e.printStackTrace(); stop();}}}).start();
        }
    }

    public void writeAll(Object e) {
        for(Client client : this.getClients())
            client.write(e.toString());
    }

    public void stop() {
        Logger.info("Stopping all services...");

        try {
            writeAll(new JSONObject().put("type", 2).put("message", "Server is going down NOW!"));
            Thread.sleep(100);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        for(Client client : this.getClients()) {
            try {
                client.stop();
            } catch(Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        Logger.info("Goodbye!");
    }

    public List<Client> getClients() {
        return this.clients;
    }

    public boolean isRunning() {
        return this.running;
    }
}
