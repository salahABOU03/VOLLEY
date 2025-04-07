package com.example.projetws;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projetws.beans.Etudiant;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class addEtudiant extends AppCompatActivity implements View.OnClickListener {

    private EditText nom;
    private EditText prenom;
    private Spinner ville;
    private RadioButton m;
    private RadioButton f;
    private Button add;
    private RequestQueue requestQueue;
    private static final String TAG = "addEtudiant";
    private final String insertUrl = "http://10.0.2.2/PhpProject1/ws/createEtudiant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_etudiant);

        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        ville = findViewById(R.id.ville);
        m = findViewById(R.id.m);
        f = findViewById(R.id.f);
        add = findViewById(R.id.add);

        add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d("ok", "ok");

        if (v == add) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());

            StringRequest request = new StringRequest(Request.Method.POST,
                    insertUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, response);

                            try {
                                // Vérifiez si la réponse est un tableau ou une chaîne
                                if (response.startsWith("[")) {
                                    // La réponse est un tableau JSON
                                    Type type = new TypeToken<Collection<Etudiant>>() {}.getType();
                                    Collection<Etudiant> etudiants = new Gson().fromJson(response, type);

                                    for (Etudiant e : etudiants) {
                                        Log.d(TAG, e.toString());
                                    }
                                } else {
                                    // La réponse est une chaîne, afficher directement
                                    Log.d(TAG, "Message: " + response);
                                }

                                // Afficher un Toast pour indiquer que l'ajout est réussi
                                Toast.makeText(addEtudiant.this, "Ajout réussi", Toast.LENGTH_SHORT).show();

                                // Fermer l'activité après 1 seconde
                                new Handler().postDelayed(() -> {
                                    finish(); // Fermer l'activité après 1 seconde
                                }, 1000);
                            } catch (JsonSyntaxException e) {
                                Log.e(TAG, "Erreur de parsing JSON : " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Erreur : " + error.getMessage());
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    String sexe = m.isChecked() ? "homme" : "femme";

                    HashMap<String, String> params = new HashMap<>();
                    params.put("nom", nom.getText().toString());
                    params.put("prenom", prenom.getText().toString());
                    params.put("ville", ville.getSelectedItem().toString());
                    params.put("sexe", sexe);
                    return params;
                }
            };

            requestQueue.add(request);
        }
    }
}
