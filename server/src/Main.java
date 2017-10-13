import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        if(args.length == 0)
            new Server(25678).start();
        else {
            if(args[0].equalsIgnoreCase("--port") || args[0].equalsIgnoreCase("-p")) {
                if(args.length == 2)
                    new Server(Integer.parseInt(args[1])).start();
                else {
                    Logger.warn("--port <number>");
                }
            } else if(args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) {
                Logger.info("talkp\n    --help/-h - Shows this help menu\n    --port/-p <number> - Starts the server on a specific port");
            } else {
                Logger.warn("Unknown option. Try --help.");
            }
        }
    }
}
