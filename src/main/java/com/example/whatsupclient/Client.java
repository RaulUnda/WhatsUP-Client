package com.example.whatsupclient;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket){
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch (IOException e){
            System.out.println("Error creando el cliente");
            e.printStackTrace();
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMsgToServer(String msgToServer, int Key, int Pub_Key){
        try{
            if(Key > 0 && Pub_Key == 0)
            {
                String encryptedToMsg = Encrypt(msgToServer, Key);
                bufferedWriter.write(encryptedToMsg);
            }
            else if(Pub_Key != 0)
            {
                String encryptedToMsg = Encrypt(msgToServer, Pub_Key);
                bufferedWriter.write(encryptedToMsg);
            }
            else {
                bufferedWriter.write(msgToServer);
            }
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Error al enviar mensaje al servidor");
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void receiveMsgFromServer(VBox vBox, int Key, int Pub_Key){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()){
                    try {
                        String msgFromServer = bufferedReader.readLine();
                        if(Key > 0 && Pub_Key == 0)
                        {
                            String encryptedFromMsg = Encrypt(msgFromServer, Key);
                            Controller.addLabel(encryptedFromMsg, vBox);
                        }
                        else if(Pub_Key != 0)
                        {
                            String encryptedFromMsg = Encrypt(msgFromServer, Pub_Key);
                            Controller.addLabel(encryptedFromMsg, vBox);
                        }
                        else {
                            Controller.addLabel(msgFromServer, vBox);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                        System.out.println("Error al recibir mensaje del servidor");
                        closeAll(socket, bufferedReader, bufferedWriter);
                        break;
                    }
                }
            }
        }).start();
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String Encrypt(String msg, int key){
        String encrypted = "";
        for(int i=0; i<msg.length(); i++)
        {
            char letter = msg.charAt(i);
            if (letter >= 'a' && letter <= 'z'){
                letter = (char)(letter + key);
                if(letter > 'z') {
                    letter = (char)(letter - 'z' + 'a' - 1);
                }
                encrypted += letter;
            }
            else if (letter >= 'A' && letter <= 'Z'){
                letter = (char)(letter + key);
                if (letter > 'Z'){
                    letter = (char)(letter - 'Z' + 'A' -1);
                }
                encrypted += letter;
            }
            else{
                encrypted += letter;
            }
        }
        return encrypted;
    }
}
