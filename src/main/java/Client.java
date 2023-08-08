import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    Socket socket;
    Scanner in;
    PrintStream out;
    ServerChat serverChat;
    String name;

    public Client(Socket socket, ServerChat serverChat) {
        this.socket = socket;
        this.serverChat = serverChat;
        // запускаем поток
        new Thread(this).start();
    }

    void receive(String message){
        out.println(message);
    }
    void setName(String name){
        this.name = name;
    }

    @Override
    public void run() {
        try {
            // получаем потоки ввода и вывода
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            // создаем удобные средства ввода и вывода
            in = new Scanner(is);
            out = new PrintStream(os);

            // читаем из сети и пишем в сеть
            out.println("Welcome to chat! To exit, write \"bye\"");
            out.println("What is your name?");
            setName(in.nextLine()); //Записываем имя для отображения в чате
            String input = in.nextLine();
            while (!input.equals("bye")) {
                serverChat.sendAll(name + ":" + "\n" + input);
                input = in.nextLine();
            }
            socket.close();
            serverChat.listClients.remove(this); //удаляем объект из списка при закрытии сессии
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
