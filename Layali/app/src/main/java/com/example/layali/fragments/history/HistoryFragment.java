package com.example.layali.fragments.history;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layali.R;
import com.example.layali.adapters.CartAdapter;
import com.example.layali.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

    public class HistoryFragment extends Fragment {

        private RecyclerView recyclerView;
        private TextView emptyView;
        private List<CartItem> historyItems;
        private CartAdapter adapter;
        private FirebaseFirestore db;
        private String userId;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_history, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            recyclerView = view.findViewById(R.id.historyRecyclerView);
            emptyView = view.findViewById(R.id.emptyHistoryView);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            historyItems = new ArrayList<>();
            adapter = new CartAdapter(getContext(), historyItems, () -> {});
            recyclerView.setAdapter(adapter);

            db = FirebaseFirestore.getInstance();

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                loadHistory();
            } else {
                Toast.makeText(getContext(), "Veuillez vous connecter pour voir l'historique.", Toast.LENGTH_SHORT).show();
                emptyView.setVisibility(View.VISIBLE);
            }
        }

        private void loadHistory() {
            db.collection("history").document(userId).collection("items")
                    .get()
                    .addOnSuccessListener(query -> {
                        historyItems.clear();
                        for (var doc : query) {
                            CartItem item = doc.toObject(CartItem.class);
                            historyItems.add(item);
                        }
                        adapter.notifyDataSetChanged();
                        emptyView.setVisibility(historyItems.isEmpty() ? View.VISIBLE : View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
