package cok.hour ;

import java.io.*;
import java.net.*;



public class ClientSide {

    private Socket socket ;
    private DataInputStream input ;
    private DataOutputStream output ;



    public ClientSide (String ip ,Integer port){
        try {
            this.socket = new Socket(ip , port) ;
            this.output = new DataOutputStream(socket.getOutputStream()) ;
            this.input = new DataInputStream(System.in) ;
            System.out.println("client connceted !");
        }
        catch (UnknownHostException u) {
 
            System.out.println(u);
        }
 
        catch (IOException i) {
 
            System.out.println(i);
        }

        String line = "";

        while (!line.equals("end")) {

            // try to read lines and
            try {
                
                try {
                    line = input.readLine();
                    output.writeUTF(line);
                    output.flush();
                    
                } catch (java.io.EOFException e) {
                    closeConnections() ;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }


        //! close connction
        
        try {
            // close the streams 
            closeConnections() ;

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    


    public static void main(String[] args) {
        ClientSide client = new ClientSide("localhost", 5000) ;
    }


    private void closeConnections() {
        try {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}