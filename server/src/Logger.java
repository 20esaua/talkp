import java.util.*;
import java.util.regex.*;

public class Logger {
    public static char tchar = '/';
    public static boolean liveupdate = false;
    public static String livestring = "";
    public static String oldstring = livestring;

    public static void init() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                switch(tchar) {
                    case '/':
                        tchar = '\u2013';
                        break;
                    case '\u2013':
                        tchar = '\\';
                        break;
                    case '\\':
                        tchar = '|';
                        break;
                    case '|':
                        tchar = '/';
                        break;
                }

                if(liveupdate) {
                    if(!oldstring.equals(livestring)) {
                        oldstring = livestring;
                        report(Status.CLEARLINE, null);
                    }

                    report(Status.TCHAR, livestring);
                }
            }
        }, 150, 150);
    }

    public static void info(Object e) {
        report(Status.INFO, e);
    }

    public static void warn(Object e) {
        report(Status.WARNING, e);
    }

    public static void err(Object e) {
        report(Status.ERROR, e);
    }

    public static void msg(String user, Object e) {
        System.out.println(Color.GREEN + Color.BOLD + "[" + Color.BLUE + user + Color.GREEN + "] " + Color.RESET + e.toString());
    }

    public static void broadcast(Object e) {
        System.out.println(Color.BOLD + Color.GREEN + e.toString() + Color.RESET);
    }

    // reports a user-friendly error message
    public static void report(int status, Object e) {
        if(status == Status.NOLINE)
            System.out.print("\033[2K");

        String statustag = "*";
        String mcolor = "";
        switch(status) {
            case Status.INFO:
                statustag = "+";
                break;
            case Status.WARNING:
                mcolor = Color.YELLOW;
                statustag = "!";
                break;
            case Status.ERROR:
                statustag = "-";
                mcolor = Color.RED + Color.BOLD;
                break;
            case Status.STATUS:
                statustag = "*";
                break;
            case Status.EMPTY:
                System.out.println();
                return;
            case Status.NOLINE:
                statustag = "*";
                break;
            case Status.CLEARLINE:
                System.out.print("\033[2K");
                return;
            case Status.RESET:
                System.out.print("\033[H\033[2J");
                System.out.flush();
                return;
            case Status.RAW:
                System.out.println(e);
                return;
            case Status.RAWNL:
                System.out.print(e);
                return;
            case Status.TCHAR:
                statustag = "" + tchar;
                break;
            case Status.HELP:
                statustag = "+";
                break;
        }

        boolean color = (status == Status.HELP) || (status == Status.ERROR);

        System.out.print(Color.GREEN + Color.BOLD + "[" + Color.BLUE + statustag + Color.GREEN + "] " + Color.RESET + (color ? Color.RED : mcolor) + (e != null ? e.toString().replaceAll(Pattern.quote("{load}"), "[" + tchar + "]") : "Unknown error. Please report this.") + (status != Status.NOLINE && status != Status.TCHAR ? "\n" : "\r") + (color ? Color.RESET : ""));
    }
}
