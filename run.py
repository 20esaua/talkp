import socket, select, string, sys, getpass, json

class colors:
    '''Colors class:
    reset all colors with colors.reset
    two subclasses fg for foreground and bg for background.
    use as colors.subclass.colorname.
    i.e. colors.fg.red or colors.bg.green
    also, the generic bold, disable, underline, reverse, strikethrough,
    and invisible work with the main class
    i.e. colors.bold
    '''
    reset='\033[0m'
    bold='\033[01m'
    disable='\033[02m'
    underline='\033[04m'
    reverse='\033[07m'
    strikethrough='\033[09m'
    invisible='\033[08m'
    italics='\033[3m'
    class fg:
        black='\033[30m'
        red='\033[31m'
        green='\033[32m'
        orange='\033[33m'
        blue='\033[34m'
        purple='\033[35m'
        cyan='\033[36m'
        lightgrey='\033[37m'
        darkgrey='\033[90m'
        lightred='\033[91m'
        lightgreen='\033[92m'
        yellow='\033[93m'
        lightblue='\033[94m'
        pink='\033[95m'
        lightcyan='\033[96m'
    class bg:
        black='\033[40m'
        red='\033[41m'
        green='\033[42m'
        orange='\033[43m'
        blue='\033[44m'
        purple='\033[45m'
        cyan='\033[46m'
        lightgrey='\033[47m'


def warn(string):
    sys.stdout.write(colors.fg.green + colors.bold + '[' + colors.fg.blue + '!' + colors.fg.green + '] ' + colors.reset + colors.fg.orange + string + '\n')

def info(string):
    sys.stdout.write(colors.fg.green + colors.bold + '[' + colors.fg.blue + '+' + colors.fg.green + '] ' + colors.reset + string + '\n')
    
def bar():
    sys.stdout.write(colors.fg.blue + colors.bold + '================================================================================' + colors.reset + '\n');

#main function
if __name__ == "__main__":
     
    if(len(sys.argv) < 2) :
        info('Usage : python run.py hostname')
        sys.exit()
     
    host = sys.argv[1]
    user = getpass.getuser()
     
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.settimeout(2)
     
    # connect to remote host
    try :
        s.connect((host, 25678))
    except :
        warn('Unable to connect to server.')
        sys.exit()
        
    # login
    data = {}
    data['type'] = 0
    data['online'] = True
    data['username'] = user
    s.send(json.dumps(data) + '\n')
     
    info('Connected to ' + host + ' on port 25678 as ' + user + '...')
    bar()
     
    while 1:
        socket_list = [sys.stdin, s]
        
        read_sockets, write_sockets, error_sockets = select.select(socket_list , [], [])
         
        for sock in read_sockets:
            if sock == s:
                data = sock.recv(4096)
                if not data :
                    sys.stdout.write('\n')
                    bar()
                    warn('Disconnected from chat server')
                    sys.exit()
                else :
                    data2 = json.loads(data)
                    if data2['type'] == 1:
                        if data2['message'] == '/clear':
                            sys.stdout.write('\033[2J\033[H')
                            sys.stdout.flush()
                            sys.stdout.write(colors.bold + colors.fg.green + '[' + colors.fg.blue + data2['username'] + colors.fg.green + '] ' + colors.reset + colors.italics + 'Cleared the screen.' + colors.reset)
                            sys.stdout.flush()
                            sys.stdout.write('\n')
                        else:
                            sys.stdout.write(colors.bold + colors.fg.green + '[' + colors.fg.blue + data2['username'] + colors.fg.green + '] ' + colors.reset + data2['message'])
                            sys.stdout.flush()
                            sys.stdout.write('\n')
                    else:
                        if data2['type'] == 0:
                            if data2['online']:
                                sys.stdout.write(colors.fg.green + colors.bold + data2['username'] + ' joined the chat.' + colors.reset)
                            else:
                                sys.stdout.write(colors.fg.green + colors.bold + data2['username'] + ' left the chat.' + colors.reset)
                            
                            sys.stdout.flush()
                            sys.stdout.write('\n')
                        else:
                            if data2['type'] == 2:
                                warn(data2['message'])
                                bar()
                                sys.exit()
             
            #user entered a message
            else :
                msg = sys.stdin.readline()
                data = {}
                data['type'] = 1
                data['message'] = msg
                s.send(json.dumps(data) + '\n')

