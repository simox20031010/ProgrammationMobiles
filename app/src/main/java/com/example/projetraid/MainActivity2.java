package com.example.projetraid;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
// Imports nécessaires pour les requêtes HTTP et bibliothèque Volley
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.android.volley.AuthFailureError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    public static MainActivity2 instance;// instance statique pour permettre l'accès depuis un thread
    private static final String TAG = "MainActivity2";
    private static final String ID_MAISON = "28";// Attribution de mon id de maison
    private static final String URL_API_GET = "http://happyresto.enseeiht.fr/smartHouse/api/v1/devices/";
    private static final String URL_API_POST = "http://happyresto.enseeiht.fr/smartHouse/api/v1/devices/";

    private MainActivity.TransferData transferData;
    private LinearLayout ll;// conteneur des vues d'appareils
    private TextView refreshTime;// affichage de l'heure du dernier rafraîchissement
    private boolean isServerRole;// Boolean informant sur le rôle de l'appareil (serveur ou client)

    private Handler handler = new Handler();
    private Runnable refreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        instance = this;

        // récupération du rôle (client ou serveur) via l'intent
        isServerRole = "server".equals(getIntent().getStringExtra("role"));
        transferData = MainActivity.transferData;
        // initialisation des composants graphiques
        ll = findViewById(R.id.layout);


        /*JSONArray jsonArray = new JSONArray();

        try {
            jsonArray.put(createJsonObject(1, "MI", "DOOR", "Porte balcon ", "sensor_door", 82, 1, "Close"));
            jsonArray.put(createJsonObject(2, "MI", "Vaccum-Mop 2 Ultra", "Aspirateur ", "robot", 45, 1, "Working"));
            jsonArray.put(createJsonObject(3, "Bosch", "Indego XS 300", "Tondeuse ", "robot", 22, 0, ""));
            jsonArray.put(createJsonObject(4, "MI", "WINDOW", "Fenêtre salon ", "sensor_door", 56, 0, ""));
            jsonArray.put(createJsonObject(5, "Tefal", "Smart n", "Cafetière ", "storage", -1, 0, ""));
            jsonArray.put(createJsonObject(6, "Tefal", "Smart n", "Cafetière 2", "storage", -1, 0, ""));
            jsonArray.put(createJsonObject(7, "Amazon", "smart plug", "Prise TV ", "plug", -1, 1, "-0.05A"));
            jsonArray.put(createJsonObject(8, "Amazon", "smart plug", "Prise salon ", "plug", -1, 0, ""));
            jsonArray.put(createJsonObject(9, "MI", "WINDOW", "Fenêtre salon 2", "sensor_door", 11, 0, ""));
            jsonArray.put(createJsonObject(10, "Royal Gardineer", "SUN-141", "Plante ", "sensor_plant", 60, 1, "56.9%"));
            jsonArray.put(createJsonObject(11, "Phillips", "HUE", "Lumière cuisine ", "light", -1, 1, "int:43,r:216,g:197,b:78"));
            jsonArray.put(createJsonObject(12, "MI", "WINDOW", "Fenêtre chambre ", "sensor_door", 74, 1, "Open"));
            jsonArray.put(createJsonObject(13, "MI", "smart plug", "Prise TV 2", "plug", -1, 1, "1.25A"));
            jsonArray.put(createJsonObject(14, "Phillips", "HUE", "Lumière chambre ", "light", -1, 1, "int:40,r:20,g:158,b:131"));
            jsonArray.put(createJsonObject(15, "MI", "Vaccum-Mop 2 Ultra", "Aspirateur 2", "robot", 64, 0, ""));
            jsonArray.put(createJsonObject(16, "MI", "Vaccum-Mop 2 Ultra", "Aspirateur 3", "robot", 49, 1, "Waiting"));
            jsonArray.put(createJsonObject(17, "MI", "smart plug", "Prise machine à laver ", "plug", -1, 1, "2.25A"));
            jsonArray.put(createJsonObject(18, "PetSafe", "PFD19", "Distributeur de croquettes ", "storage", -1, 1, "Empty"));
            jsonArray.put(createJsonObject(19, "MI", "smart plug", "Prise TV ", "plug", -1, 1, "2.8A"));
            jsonArray.put(createJsonObject(20, "Royal Gardineer", "SUN-141", "Plante ", "sensor_plant", 70, 1, "60.85%"));
            jsonArray.put(createJsonObject(21, "RollScout", "Original", "Capteur WC ", "storage", 55, 0, ""));
            jsonArray.put(createJsonObject(22, "RollScout", "Original", "Capteur WC 2", "storage", 56, 0, ""));
            jsonArray.put(createJsonObject(23, "RollScout", "Original", "Capteur WC 3", "storage", 39, 0, ""));
            jsonArray.put(createJsonObject(24, "MI", "smart Bulb", "Lumière exterieur ", "light", -1, 1, "int:62,r:170,g:250,b:237"));
            jsonArray.put(createJsonObject(25, "MI", "WINDOW", "Fenêtre chambre ", "sensor_door", 69, 1, "Close"));
            jsonArray.put(createJsonObject(26, "Netatmo", "WS", "Station météo ", "sensor_temp", -1, 1, "21.9°C"));
            jsonArray.put(createJsonObject(27, "Bosch", "Indego XS 300", "Tondeuse ", "robot", 70, 1, "Charging"));
            jsonArray.put(createJsonObject(28, "PetSafe", "PFD19", "Distributeur de croquettes 2", "storage", -1, 0, ""));
            jsonArray.put(createJsonObject(29, "Tefal", "Smart n", "Cafetière ", "storage", -1, 1, "Empty"));
            jsonArray.put(createJsonObject(30, "PetSafe", "PFD19", "Distributeur de croquettes 3", "storage", -1, 0, ""));
            jsonArray.put(createJsonObject(31, "MI", "smart Bulb", "Lumière balcon ", "light", -1, 1, "int:62,r:88,g:223,b:229"));
            jsonArray.put(createJsonObject(32, "MI", "smart plug", "Prise TV 2", "plug", -1, 1, "1.75A"));
            jsonArray.put(createJsonObject(33, "Phillips", "HUE", "Lumière chambre ", "light", -1, 1, "int:90,r:130,g:238,b:160"));
            jsonArray.put(createJsonObject(34, "MI", "Vaccum-Mop 2 Ultra", "Aspirateur ", "robot", 71, 0, ""));
            jsonArray.put(createJsonObject(35, "Bosch", "Indego XS 300", "Tondeuse ", "robot", 89, 0, ""));
            jsonArray.put(createJsonObject(36, "MI", "smart Bulb", "Lumière balcon ", "light", -1, 0, ""));
            jsonArray.put(createJsonObject(37, "Phillips", "HUE", "Lumière salon ", "light", -1, 0, ""));
            jsonArray.put(createJsonObject(38, "MI", "smart plug", "Prise machine à laver ", "plug", -1, 0, ""));
            jsonArray.put(createJsonObject(39, "MI", "Vaccum-Mop 2 Ultra", "Aspirateur 2", "robot", 87, 0, ""));
            jsonArray.put(createJsonObject(40, "Bosch", "Indego XS 300", "Tondeuse 2", "robot", 15, 0, ""));
            jsonArray.put(createJsonObject(41, "Amazon", "smart plug", "Prise ventilateur ", "plug", -1, 0, ""));
            jsonArray.put(createJsonObject(42, "MI", "smart plug", "Prise bouilloir ", "plug", -1, 0, ""));
            jsonArray.put(createJsonObject(43, "MI", "WINDOW", "Fenêtre chambre ", "sensor_door", 96, 0, ""));
            jsonArray.put(createJsonObject(44, "Tefal", "Smart n", "Cafetière ", "storage", -1, 0, ""));
            jsonArray.put(createJsonObject(45, "MI", "Vaccum-Mop 2 Ultra", "Aspirateur 3", "robot", 29, 0, ""));
            jsonArray.put(createJsonObject(46, "Netatmo", "WS", "Station météo ", "sensor_temp", -1, 0, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject deviceObject = jsonArray.getJSONObject(i);
                String name = deviceObject.getString("NAME");
                String info = "Brand: " + deviceObject.getString("BRAND") + " Model: " + deviceObject.getString("MODEL") +
                        "\nType: " + deviceObject.getString("TYPE") + " Autonomy: " + deviceObject.getInt("AUTONOMY") + "%" +
                        " Data: " + deviceObject.getString("DATA");
                boolean turnOnOff = deviceObject.getInt("STATE") == 1;
                int id = deviceObject.getInt("ID");

                ll.addView(createDeviceView(name, info, turnOnOff, id));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
        refreshTime = findViewById(R.id.refreshTime);

        // si serveur, on lance la récupération API et le rafraîchissement automatique
        if (isServerRole) {
            fetchDevicesFromApi();
            startPeriodicRefresh();
        } else {
            refreshTime.setText("En attente des données du serveur...");
        }
    }

    // appelé quand l'activité revient au premier plan
    @Override
    protected void onResume() {
        super.onResume();
        if (isServerRole) {
            // efface les vues précédentes
            ll.removeAllViews();
            fetchDevicesFromApi();
            // relance le rafraîchissement automatique toutes les 10s
            handler.post(refreshRunnable);
        }
    }

    // stoppe les mises à jour périodiques lorsque l'activité est en pause
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRunnable);
    }
    // stoppe aussi les mises à jour quand l'activité est détruite
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refreshRunnable);
    }

    // démarre un rafraîchissement automatique toutes les 10 secondes (serveur uniquement)
    private void startPeriodicRefresh() {
        refreshRunnable = () -> {
            Log.d(TAG, "Rafraîchissement automatique");

            updateRefreshTime();
            ll.removeAllViews();
            fetchDevicesFromApi();
            handler.postDelayed(refreshRunnable, 10000);
        };

        handler.post(refreshRunnable);
    }

    // met à jour l'affichage de l'heure du dernier rafraîchissement
    private void updateRefreshTime() {
        String currentTime = DateFormat.getTimeInstance().format(new Date());
        refreshTime.setText("Dernier rafraîchissement : " + currentTime);
    }

    // effectue une requête GET à l'API REST pour récupérer la liste des appareils connectés
    private void fetchDevicesFromApi() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URL_API_GET + ID_MAISON;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        ll.removeAllViews();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject device = response.getJSONObject(i);
                            String name = device.getString("NAME");
                            String info = "Brand: " + device.getString("BRAND") +
                                    " Model: " + device.getString("MODEL") +
                                    "\nType: " + device.getString("TYPE") +
                                    " Autonomy: " + device.optInt("AUTONOMY", -1) + "%" +
                                    " Data: " + device.optString("DATA", "");
                            boolean turnOnOff = device.getInt("STATE") == 1;
                            int id = device.getInt("ID");

                            ll.addView(createDeviceView(name, info, turnOnOff, id));
                        }
                        // le serveur envoie la liste complète au client via Bluetooth
                        if (transferData != null) {
                            transferData.write((response.toString() + "\n").getBytes());
                            Log.d(TAG, "Données envoyées au client");
                        } else {
                            Log.e(TAG, "transferData est null, données non envoyées");
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "Erreur JSON", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Erreur GET", error);
                    Toast.makeText(this, "Erreur GET : " + error.toString(), Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }
    //Méthode permettant de génèrer un `LinearLayout` pour chaque dispositif, contenant un bouton et deux `TextView`.
    public View createDeviceView(String name, String info, boolean turnOnOff, int id) {
        LinearLayout deviceLayout = new LinearLayout(this);
        deviceLayout.setOrientation(LinearLayout.HORIZONTAL);
        deviceLayout.setPadding(24, 16, 24, 16);
        deviceLayout.setBackgroundColor(Color.parseColor("#EEEEEE"));

        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView nameText = new TextView(this);
        nameText.setText(name);
        nameText.setTextSize(16);
        nameText.setTypeface(null, Typeface.BOLD);

        TextView infoText = new TextView(this);
        infoText.setText(info);
        infoText.setTextSize(14);

        textLayout.addView(nameText);
        textLayout.addView(infoText);

        Button button = new Button(this);
        button.setText(turnOnOff ? "On" : "Off");
        button.setTextColor(turnOnOff ? Color.GREEN : Color.RED);
        button.setTag(id);
        button.setPadding(12, 8, 12, 8);
        button.setBackgroundColor(Color.LTGRAY);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(16, 0, 0, 0);
        button.setLayoutParams(buttonParams);

        if (isServerRole) {
            button.setEnabled(false);
        } else {
            button.setOnClickListener(v -> {
                boolean newState = !button.getText().equals("On");
                button.setText(newState ? "On" : "Off");
                button.setTextColor(newState ? Color.GREEN : Color.RED);
                sendMessageFromClient(id, newState ? "On" : "Off");
            });
        }

        deviceLayout.addView(textLayout);
        deviceLayout.addView(button);
        return deviceLayout;
    }

    //La méthode `update` envoie une requête POST au serveur pour mettre à jour l'état du dispositif via l'API REST.
    public void update(int deviceId, boolean turnOnOff) {
        if (!isServerRole) return;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_API_POST,
                response -> Log.d(TAG, "POST réussi"),
                error -> Log.e(TAG, "Erreur POST", error)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("deviceId", String.valueOf(deviceId));
                p.put("houseId", ID_MAISON);
                p.put("action", "turnOnOff");
                return p;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> h = new HashMap<>();
                h.put("Content-Type", "application/x-www-form-urlencoded");
                return h;
            }
        };

        queue.add(postRequest);
    }
    //Méthode permettant d'envoyer des messages via Bluetooth pour synchroniser les états entre le client et le serveur
    public void sendMessageFromClient(int id, String onOff) {
        if (transferData != null) {
            String msg = id + ":" + onOff + "\n";
            transferData.write(msg.getBytes());
            Log.d(TAG, "Commande envoyée au serveur : " + msg);
        }
    }

    // traite les messages reçus via Bluetooth : soit JSON, soit commande client
    public void processReceivedMessage(String message) {
        if (message.trim().startsWith("[")) {
            // CLIENT : réception du JSON
            runOnUiThread(() -> {
                updateRefreshTime(); // mise à jour de l'heure sur réception
                ll.removeAllViews();// supprime les anciennes vues
                try {
                    JSONArray devices = new JSONArray(message);
                    for (int i = 0; i < devices.length(); i++) {
                        JSONObject device = devices.getJSONObject(i);
                        String name = device.getString("NAME");
                        String info = "Brand: " + device.getString("BRAND") +
                                " Model: " + device.getString("MODEL") +
                                "\nType: " + device.getString("TYPE") +
                                " Autonomy: " + device.optInt("AUTONOMY", -1) + "%" +
                                " Data: " + device.optString("DATA", "");
                        boolean turnOnOff = device.getInt("STATE") == 1;
                        int id = device.getInt("ID");
                        ll.addView(createDeviceView(name, info, turnOnOff, id));// recrée la vue appareil

                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Erreur de parsing JSON client", e);
                }
            });
        } else if (message.contains(":")) {
            // SERVEUR : réception d'une commande du client
            String[] parts = message.split(":");
            if (parts.length == 2) {
                int id = Integer.parseInt(parts[0]);
                String onOff = parts[1];

                runOnUiThread(() -> {
                    for (int i = 0; i < ll.getChildCount(); i++) {
                        View view = ll.getChildAt(i);
                        if (view instanceof LinearLayout) {
                            LinearLayout layout = (LinearLayout) view;
                            for (int j = 0; j < layout.getChildCount(); j++) {
                                View child = layout.getChildAt(j);
                                if (child instanceof Button) {
                                    Button btn = (Button) child;
                                    if (btn.getTag() != null && btn.getTag().equals(id)) {
                                        btn.setText(onOff);
                                        btn.setTextColor(onOff.equals("On") ? Color.GREEN : Color.RED);
                                        update(id, onOff.equals("On"));
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}
