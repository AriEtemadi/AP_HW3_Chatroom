import java.io.InputStream;
import java.util.Scanner;

class ChatLineReader extends Thread {
    private InputStream input;
    Scanner scanner;

    ChatLineReader(InputStream input) {
        this.input = input;
        this.scanner = new Scanner(input);
    }

}
