import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    ServerSocket serverSocket = null;
    int port = 6379;
    
    try {
      serverSocket = new ServerSocket(port);
      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors
      serverSocket.setReuseAddress(true);
      System.out.println("Server started on port " + port);
      
      // Keep the server running to accept multiple connections
      while (true) {
        // Wait for connection from client
        Socket clientSocket = serverSocket.accept();
        //System.out.println("Client connected: " + clientSocket.getInetAddress());
        
        // Handle client connection in a separate thread
        new Thread(() -> handleClient(clientSocket)).start();
        // handleClient(clientSocket);
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    } finally {
      try {
        if (serverSocket != null) {
          serverSocket.close();
        }
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
    }
  }
  
  private static void handleClient(Socket clientSocket) {
    try(OutputStream outputStream = clientSocket.getOutputStream()) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      
      
      String line;
      // Keep reading commands until the client closes the connection
      while ((line = reader.readLine()) != null) {
        System.out.println("Received command: " + line);
        if(line.equalsIgnoreCase("PING")){
          outputStream.write("+PONG\r\n".getBytes());
        }
        outputStream.flush();
      }
    } catch (IOException e) {
      System.out.println("IOException in client handler: " + e.getMessage());
    } finally {
      try {
        clientSocket.close();
        System.out.println("Client disconnected");
      } catch (IOException e) {
        System.out.println("IOException while closing client socket: " + e.getMessage());
      }
    }
  }
}
