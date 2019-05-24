import java.io.IOException;

public class Main {
    private static Server server = null;

    public static void main(String[] args) throws IOException {
        server = new Server(0);
        System.out.println(server.serverSocket.getLocalPort());
        server.run();
    }
}
