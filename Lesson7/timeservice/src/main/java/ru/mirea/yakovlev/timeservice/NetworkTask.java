package ru.mirea.yakovlev.timeservice;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkTask extends AsyncTask<Void, Void, String> {
    private static final String SERVER_IP = "time.nist.gov";
    private static final int PORT = 13;
    private TextView timeTextView;

    public NetworkTask(TextView timeTextView) {
        this.timeTextView = timeTextView;
    }

    @Override
    protected String doInBackground(Void... voids) {
        Socket socket = null;
        try {
            socket = new Socket();

            socket.connect(new InetSocketAddress(SERVER_IP, PORT), 5000);

            socket.setSoTimeout(5000);
            BufferedReader reader = SocketUtils.getReader(socket);
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString();
        } catch (UnknownHostException e) {
            return "Ошибка: Неверный адрес сервера";
        } catch (IOException e) {
            return "Ошибка: Нет подключения к интернету";
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        String[] parts = result.split(" ");
        if (parts.length >= 3) {
            String date = parts[1];
            String time = parts[2];
            timeTextView.setText("Дата: " + date + "\nВремя: " + time);
        } else {
            timeTextView.setText(result);
        }
    }
}