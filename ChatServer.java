import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

// Server
public class ChatServer {
    private static final int PORT = 8080; // Port yang digunakan server
    private static Set<ClientHandler> clientHandlers = ConcurrentHashMap.newKeySet(); // Set untuk menyimpan client yang terhubung

    public static void main(String[] args) {
        System.out.println("Server dimulai pada port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Menerima koneksi client baru
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client baru terhubung: " + clientSocket.getRemoteSocketAddress());

                // Membuat thread baru untuk menangani client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Mengirim pesan ke semua client kecuali pengirim
    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // Menghapus client dari daftar client aktif
    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}

// Class untuk menangani setiap client
class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // Menyiapkan input dan output untuk komunikasi dengan client
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Pesan diterima: " + message);
                // Broadcast pesan ke semua client lainnya
                ChatServer.broadcast(message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close(); // Menutup koneksi socket
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatServer.removeClient(this); // Menghapus client dari daftar
            System.out.println("Client terputus: " + socket.getRemoteSocketAddress());
        }
    }

    // Mengirim pesan ke client ini
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}
