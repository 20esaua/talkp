import java.io.*;
import java.util.*;

public class Client {
    private Client client = null;
    private Socket socket = null;
    private BufferedReader reader = null;
    private PrintWriter writer = null;

    public Client(Client client, Socket socket) throws Exception {
        this.client = client;
        this.socket = socket;

        this.reader = new PrintWriter(this.getSocket().getOutputStream(), true)
    }

    public Socket getSocket() {
        return this.socket;
    }

    public Client getClient() {
        return this.client;
    }

    public InputStream getInputStream() {
        return this.getSocket().getInputStream();
    }

    public OutputStream getOutputStream() {
        return this.getSocket().getOutputStream();
    }
}
