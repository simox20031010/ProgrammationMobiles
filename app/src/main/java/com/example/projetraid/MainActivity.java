package com.example.projetraid;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    // Adaptateur Bluetooth
    BluetoothAdapter bluetoothAdapter;
    // Thread de communication Bluetooth partagé
    public static TransferData transferData;
    // Boolean informant sur le rôle de l'appareil (serveur ou client)
    public static boolean isServer;
    // UUID partagé entre client et serveur
    private static final UUID MY_UUID = UUID.fromString("7b5a1316-e19e-04ec-8fea-0242ac120002");
    // Comme expliquer pendant la présentation, j'avais essayé au debut de connecter le client à travers l'addresse MAC Bluetooth du serveur
    /*private static final String APP_NAME = "BluetoothChat";
    //private static final String MAC_ADDRESS_RECEIVER = "18:87:40:78:8D:D9";
    */

    // Méthode onCreate appelée lors de la création de l'activité
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation de l'adaptateur Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Button clientBtn = findViewById(R.id.clientBtn);
        Button serverBtn = findViewById(R.id.serveurBtn);
        TextView attentetxt = findViewById(R.id.attenteTxt);
        attentetxt.setText("");

        // Bouton "Serveur" : lance l'écoute pour une connexion entrante
        serverBtn.setOnClickListener(v -> {
            clientBtn.setVisibility(View.INVISIBLE);
            serverBtn.setText("Serveur en attente...");
            serverBtn.setClickable(false);
            attentetxt.setText("*En attente de connexion du client*");

            isServer = true;
            new serverThread().start();
        });

        // Bouton "Client" : propose de choisir un appareil appairé
        clientBtn.setOnClickListener(v -> {
            serverBtn.setVisibility(View.INVISIBLE); // Masque le bouton serveur
            clientBtn.setText("Client en attente...");// Affichage dans le bouton client pour indiquer qu'il est en attente
            clientBtn.setClickable(false);
            attentetxt.setText("*Sélectionnez un appareil Bluetooth*");

            // Récupère la liste des appareils déjà appairés
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.isEmpty()) {
                Toast.makeText(MainActivity.this, "Aucun appareil appairé trouvé", Toast.LENGTH_SHORT).show();
                return;
            }

            final BluetoothDevice[] devices = pairedDevices.toArray(new BluetoothDevice[0]);
            String[] deviceNames = new String[devices.length];
            for (int i = 0; i < devices.length; i++) {
                deviceNames[i] = devices[i].getName() + "\n" + devices[i].getAddress();
            }

            // Affichage d'une boîte de dialogue listant les appareils appairés Permet à l'utilisateur de sélectionner un appareil Bluetooth pour lancer la connexion client
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Choisissez un appareil")
                    .setItems(deviceNames, (dialog, which) -> {
                        // Récupère l'appareil sélectionné par l'utilisateur
                        BluetoothDevice selectedDevice = devices[which];
                        isServer = false;
                        // Lance un thread client pour tenter de se connecter au serveur sélectionné */
                        new clientThread(selectedDevice).start();
                        attentetxt.setText("*Connexion à " + selectedDevice.getName() + "*");
                    }).show();
        });
    }
    /*
    // Méthode de connexion directe à un appareil Bluetooth via son adresse MAC
    private void connectToReceiverByMac() {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(MAC_ADDRESS_RECEIVER);
        new Thread(() -> {
            try {
                socket = device.createRfcommSocketToServiceRecord(Mon_UUID);
                bluetoothAdapter.cancelDiscovery();
                socket.connect();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                listenForMessages();
            } catch (IOException e) {
                handler.post(() -> messageBox.setText("Connexion échouée"));
            }
        }).start();
    }
    */

    // Thread serveur : écoute et accepte une connexion Bluetooth
    private class serverThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public serverThread() {
            BluetoothServerSocket tmp = null;
            try {
                // Crée un socket RFCOMM vers l’appareil sélectionné
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("SmartHome", MY_UUID);
            } catch (IOException e) {
                Log.e("serverThread", "Erreur création socket serveur", e);
            }
            mmServerSocket = tmp;
        }

        @Override
        public void run() {
            BluetoothSocket socket;
            try {
                // Accepte une seule connexion entrante
                socket = mmServerSocket.accept();
                // Si le socket n'est pas nul (connexion réussie)
                if (socket != null) {
                    Log.d("serverThread", "Client connecté !");

                    // Initialise la communication
                    transferData = new TransferData(socket);
                    transferData.start();

                    // Crée un intent pour lancer l'activité MainActivity2
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("role", "server");
                    startActivity(intent);// Lance l'activité MainActivity2


                    mmServerSocket.close();
                }
            } catch (IOException e) {
                Log.e("serverThread", "Erreur accept()", e);
            }
        }
    }

    // Thread client : tente de se connecter à un serveur Bluetooth
    private class clientThread extends Thread {
        private final BluetoothSocket mmSocket;

        public clientThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e("clientThread", "Erreur création socket client", e);
            }
            mmSocket = tmp;
        }

        @Override
        public void run() {
            bluetoothAdapter.cancelDiscovery();
            try {
                // Tente de se connecter à l'appareil serveur
                mmSocket.connect();
                Log.d("clientThread", "Connecté au serveur !");
                // Initialise la communication
                transferData = new TransferData(mmSocket);
                transferData.start();
                // Lance l'activité MainActivity2 avec rôle client
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("role", "client");
                startActivity(intent);
            } catch (IOException e) {
                Log.e("clientThread", "Échec connexion", e);
                try {
                    // Ferme le socket client en cas d'échec de connexion
                    mmSocket.close();
                } catch (IOException ex) {
                    Log.e("clientThread", "Erreur fermeture socket", ex);
                }
            }
        }
    }

    // Thread de communication Bluetooth entre client et serveur
    public class TransferData extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;// Flux d'entrée pour lire les données
        private final OutputStream outputStream;// Flux de sortie pour écrire les données
        private final StringBuilder bufferBuilder = new StringBuilder();

        public TransferData(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = bluetoothSocket.getInputStream();// Prendre le flux d'entrée du socket
                tmpOut = bluetoothSocket.getOutputStream();// Prendre le flux de sortie du socket
            } catch (IOException e) {
                Log.e("TransferData", "Erreur récupération streams", e);
            }
            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        @Override
        public void run() {// Méthode exécutée lorsque le thread démarre
            byte[] buffer = new byte[1024];// Buffer pour stocker les données lues
            int bytes;

            // Boucle de lecture des données depuis le flux d'entrée
            while (true) {
                try {
                    // Lecture de données entrantes
                    bytes = inputStream.read(buffer);
                    String received = new String(buffer, 0, bytes);
                    bufferBuilder.append(received);


                    // Recherche de fin de message (\n)
                    int newlineIndex;
                    while ((newlineIndex = bufferBuilder.indexOf("\n")) != -1) {
                        String completeMessage = bufferBuilder.substring(0, newlineIndex).trim();
                        bufferBuilder.delete(0, newlineIndex + 1);

                        Log.d("BluetoothRecv", "Message complet reçu : " + completeMessage);
                        // Envoi du message à l'activité principale pour traitement
                        if (MainActivity2.instance != null) {
                            MainActivity2.instance.processReceivedMessage(completeMessage);
                        } else {
                            Log.e("BluetoothRecv", "⚠MainActivity2.instance est null !");
                        }
                    }
                } catch (IOException e) {
                    Log.e("TransferData", "Erreur lecture socket", e);
                    break; // En cas d'erreur, on sort de la boucle
                }
            }
        }

        // Méthode pour écrire des données dans le flux de sortie
        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);// Ecrire les données dans le flux de sortie
                Log.d("BluetoothSend", "Message envoyé : " + new String(bytes));
            } catch (IOException e) {
                Log.e("TransferData", "Erreur écriture socket", e);
            }
        }
    }
}
