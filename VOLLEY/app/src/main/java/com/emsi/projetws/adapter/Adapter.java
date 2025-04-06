package com.emsi.projetws.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emsi.projetws.R;
import com.emsi.projetws.beans.Etudiant;

import java.util.List;

public class Adapter  extends RecyclerView.Adapter<Adapter.EtudiantViewHolder> {

    private List<Etudiant> etudiants;
    private Context context;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(Etudiant etudiant);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public Adapter(List<Etudiant> etudiants, Context context) {
        this.etudiants = etudiants;
        this.context = context;
    }

    @NonNull
    @Override
    public EtudiantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_etudiant,parent,false);
        return new EtudiantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantViewHolder holder, int position) {
        Etudiant e = etudiants.get(position);
        holder.nom.setText( e.getNom());
        holder.prenom.setText(e.getPrenom());
        holder.ville.setText( e.getVille());
        holder.sexe.setText(e.getSexe());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return etudiants.size();
    }

    class EtudiantViewHolder extends RecyclerView.ViewHolder {
        TextView nom;
        TextView prenom;
        TextView ville;
        TextView sexe;
        public EtudiantViewHolder(@NonNull View itemView) {
            super(itemView);
            nom = itemView.findViewById(R.id.textNom);
            prenom = itemView.findViewById(R.id.textPrenom);
            ville = itemView.findViewById(R.id.textVille);
            sexe = itemView.findViewById(R.id.textSexe);
        }

    }
}
