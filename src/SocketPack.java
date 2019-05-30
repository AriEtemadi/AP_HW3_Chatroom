import java.net.Socket;

class SocketPack {
    private Socket socket;
    private ChatLineWriter chatLineWriter;
    private ChatLineReader chatLineReader;

    SocketPack(Socket socket, ChatLineWriter chatLineWriter,
               ChatLineReader chatLineReader) {
        this.socket = socket;
        this.chatLineWriter = chatLineWriter;
        this.chatLineReader = chatLineReader;
    }

    Socket getSocket() {
        return socket;
    }

    ChatLineWriter getChatLineWriter() {
        return chatLineWriter;
    }

    ChatLineReader getChatLineReader() {
        return chatLineReader;
    }

    void setChatLineReader(ChatLineReader chatLineReader) {
        this.chatLineReader = chatLineReader;
    }
}
