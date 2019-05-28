import java.net.Socket;

class SocketPack {
    private Socket socket;
    private ChatLineWriter chatLineWriter;
    private ChatLineReader chatLineReader;
    private User user;

    SocketPack(Socket socket, ChatLineWriter chatLineWriter,
               ChatLineReader chatLineReader, User user) {
        this.socket = socket;
        this.chatLineWriter = chatLineWriter;
        this.chatLineReader = chatLineReader;
        this.user = user;
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

    User getUser() {
        return user;
    }

    void setChatLineReader(ChatLineReader chatLineReader) {
        this.chatLineReader = chatLineReader;
    }
}
