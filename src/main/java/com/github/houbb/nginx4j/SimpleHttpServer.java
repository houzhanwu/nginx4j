package com.github.houbb.nginx4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class SimpleHttpServer {

    //http://localhost:8080/1.txt
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: java SimpleHttpServer <port> <basicPath>");
            System.exit(1);
        }
        int port = 8080;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + args[0]);
            System.exit(1);
        }

        String basicPath = args[1];
        //int port = 8080; // 服务器监听的端口号
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Listening on port " + port);
        //String basicPath = "D:\\workspace_sx\\nginx4j\\src\\test\\resources";
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Accepted connection from " + socket.getRemoteSocketAddress());
            handleClient(socket, basicPath);
        }
    }

    private static void handleClient(Socket socket, String basicPath) {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String header = reader.readLine();
            String[] parts = header.split(" ");
            String method = parts[0];
            String path = parts[1];
            String protocol = parts[2];

            //final String basicPath = "D:\\workspace_sx\\nginx4j\\src\\test\\resources/";
            // 只处理GET请求
            if ("GET".equalsIgnoreCase(method)) {
                File file = new File(basicPath + path);
                if (file.exists()) {
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    sendResponse(socket, 200, "OK", fileContent);
                } else {
                    sendResponse(socket, 404, "Not Found", "File not found.".getBytes());
                }
            } else {
                sendResponse(socket, 405, "Method Not Allowed", "Method not allowed.".getBytes());
            }
        } catch (IOException e) {
            try {
                sendResponse(socket, 500, "Internal Server Error", "Internal server error.".getBytes());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendResponse(Socket socket, int statusCode, String statusMessage, byte[] content) throws IOException {
        OutputStream output = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);

        // 发送HTTP响应头
        writer.println("HTTP/1.1 " + statusCode + " " + statusMessage);
        writer.println("Content-Type: text/plain");
        writer.println("Content-Length: " + content.length);
        writer.println("Connection: close");
        writer.println();

        // 发送HTTP响应体
        output.write(content);
        output.flush();
    }

}
