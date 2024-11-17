import java.io.*;
import java.net.*;

// Client
public class ChatClient {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Alamat server
        int port = 8080; // Port server

        try (Socket socket = new Socket(serverAddress, port)) {
            System.out.println("Terhubung ke server");

            // Membuat thread untuk mengirim dan menerima pesan
            Thread senderThread = new Thread(new MessageSender(socket));
            Thread receiverThread = new Thread(new MessageReceiver(socket));

            senderThread.start();
            receiverThread.start();

            // Menunggu kedua thread selesai
            senderThread.join();
            receiverThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// Class untuk menangani pengiriman pesan ke server
class MessageSender implements Runnable {
    private Socket socket;

    public MessageSender(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            String message;
            while ((message = consoleInput.readLine()) != null) {
                out.println(message); // Mengirim pesan ke server
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Class untuk menangani penerimaan pesan dari server
class MessageReceiver implements Runnable {
    private Socket socket;

    public MessageReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Server: " + message); // Menampilkan pesan dari server
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
