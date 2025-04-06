package com.emsi.projetws;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.emsi.projetws.adapter.Adapter;
import com.emsi.projetws.beans.Etudiant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListEtudiantActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter adapter;
    private List<Etudiant> etudiants = new ArrayList<>();
    private static final String URL = "http://10.0.2.2/projet/ws/getAllEtudiant.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_etudiant);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new Adapter(etudiants,this);
        adapter.setOnItemClickListener(this::afficherOptions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        chargerEtudiants();
    }

    private void chargerEtudiants() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                response -> {
                    etudiants.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Etudiant e = new Etudiant(
                                    obj.getInt("id"),
                                    obj.getString("nom"),
                                    obj.getString("prenom"),
                                    obj.getString("ville"),
                                    obj.getString("sexe")
                            );
                            etudiants.add(e);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {

                    if (error.networkResponse != null) {
                        int status = error.networkResponse.statusCode;
                        String body = new String(error.networkResponse.data);
                        Log.e("chargerEtudiants", "Erreur réseau – Status: " + status + " Body: " + body);
                    } else {
                        Log.e("chargerEtudiants", "Erreur réseau", error);
                    }
                    Toast.makeText(this, "Erreur de chargement des étudiants", Toast.LENGTH_LONG).show();
                }
        );

        queue.add(request);
    }

    private void afficherOptions(Etudiant etudiant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choisissez une action")
                .setItems(new CharSequence[]{"Modifier", "Supprimer"}, (dialog, which) -> {
                    if (which == 0) {
                        afficherFormulaireModification(etudiant);
                    } else {
                        confirmerSuppression(etudiant);
                    }
                })
                .show();
    }

    private void confirmerSuppression(Etudiant etudiant) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmer la suppression")
                .setMessage("Voulez-vous vraiment supprimer cet étudiant ?")
                .setPositiveButton("Oui", (dialog, which) -> supprimerEtudiant(etudiant))
                .setNegativeButton("Non", null)
                .show();
    }

    private void supprimerEtudiant(Etudiant etudiant) {
        String url = "http://10.0.2.2/projet/ws/supprimerEtudiant.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Supprimé avec succès", Toast.LENGTH_SHORT).show();
                    chargerEtudiants();
                },
                error -> Toast.makeText(this, "Erreur suppression", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(etudiant.getId()));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void afficherFormulaireModification(Etudiant etudiant) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_modifier_etudiant, null);

        EditText editNom = view.findViewById(R.id.editNom);
        EditText editPrenom = view.findViewById(R.id.editPrenom);
        EditText editVille = view.findViewById(R.id.editVille);
        EditText editSexe = view.findViewById(R.id.editSexe);

        editNom.setText(etudiant.getNom());
        editPrenom.setText(etudiant.getPrenom());
        editVille.setText(etudiant.getVille());
        editSexe.setText(etudiant.getSexe());

        new AlertDialog.Builder(this)
                .setTitle("Modifier Étudiant")
                .setView(view)
                .setPositiveButton("Modifier", (dialog, which) -> {
                    modifierEtudiant(
                            etudiant.getId(),
                            editNom.getText().toString(),
                            editPrenom.getText().toString(),
                            editVille.getText().toString(),
                            editSexe.getText().toString()
                    );
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void modifierEtudiant(int id, String nom, String prenom, String ville, String sexe) {
        String url = "http://10.0.2.2/projet/ws/modifierEtudiant.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Modifié avec succès", Toast.LENGTH_SHORT).show();
                    chargerEtudiants();
                },
                error -> Toast.makeText(this, "Erreur modification", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                params.put("nom", nom);
                params.put("prenom", prenom);
                params.put("ville", ville);
                params.put("sexe", sexe);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}