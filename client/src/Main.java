public class Main {
    public static void main(String[] args) {
        if(args.length == 0)
            Logger.info("talkp user@host:port");
        else {
            String user = System.getProperty("user.name");
            String host = args[0];
            int port = 25678;

            try {
                if(host.contains("@")) {
                    user = host.substring(0, host.indexOf("@"));
                    host = host.substring(host.indexOf("@") + 1, host.length());
                }

                if(host.contains(":")) {
                    port = Integer.parseInt(host.substring(host.indexOf(":") + 1, host.length()));
                    host = host.substring(0, host.indexOf(":"));
                }
            } catch(Exception e) {
                Logger.err("Failed to parse command-line arguments.");
                System.exit(1);
            }

            user = user.replaceAll("[^A-Za-z0-9]", "").trim().toLowerCase();
            if(user.length() == 0)
                user = System.getProperty("user.name");

            Client client = new Client(user, host, port);
            client.start();
        }
    }
}
