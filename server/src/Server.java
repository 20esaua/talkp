import java.util.*;
import java.io.*;

public class Server {
    private int port = 25678;
    private boolean running = false;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        if(!this.isRunning()) {
            this.running = true;

            new Thread(new Runnable() {
                public void run() {
                    try {
                        ServerSocket serverSocket = new ServerSocket(port);
                        while(this.isRunning()) {
                            try {
                                Socket socket = serverSocket.accept();

                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public boolean isRunning() {
        return this.running;
    }
}
