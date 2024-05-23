package cok.hour;

import java.io.BufferedinStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;




public class ServerSide {

    private Socket socket ;
    private ServerSocket server ;
    private DataInputStream in ;


    public ServerSide(int port){

        try {
            server = new ServerSocket(port) ;

            System.out.println("Server started");
 
            System.out.println("Waiting for a client ...");
 
            socket = server.accept();
 
            System.out.println("Client accepted");


            // take the input form the client 
            in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream())
            ) ;

            String line = "";
            boolean is_running = true ;

            while (is_running) {

                try {
 
                    line = in.readUTF();
                    System.out.println(line);
                }
 
                catch (IOException i) {
 
                    System.out.println(i);
                }
                
            }


            System.out.println("Closing connection");
 
            // close connection
            socket.close();
 
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    public static void main(String[] args) {
        ServerSide server = new ServerSide(5000);
    }



    private void closeConnections() {
        try {
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
