package com.example.layali.fragments.cart;

import static java.sql.DriverManager.println;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.layali.R;
import com.example.layali.activities.LoginActivity;
import com.example.layali.activities.ReservationActivity;
import com.example.layali.adapters.CartAdapter;
import com.example.layali.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView totalView, emptyView;
    Button checkoutBtn;
    private List<CartItem> cartItems;
    private CartAdapter adapter;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseFirestore db;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.cartRecyclerView);
        totalView = view.findViewById(R.id.totalValue);
        emptyView = view.findViewById(R.id.emptyCartView);
        checkoutBtn = view.findViewById(R.id.checkoutButton);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartItems = new ArrayList<>();
        adapter = new CartAdapter(getContext(), cartItems, this::updateTotal);
        recyclerView.setAdapter(adapter);


        db = FirebaseFirestore.getInstance();

        // ✅ Utilise anonymousId stocké localement
        userId = getActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE)
                .getString("anonymousId", null);

        if (userId == null) {
            userId = java.util.UUID.randomUUID().toString();
            getActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE)
                    .edit().putString("anonymousId", userId).apply();
        }


/*      auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            // ✅ récupérer l'anonymousId depuis SharedPreferences
            userId = getActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE)
                    .getString("anonymousId", null);

            if (userId == null) {
                userId = java.util.UUID.randomUUID().toString();
                getActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE)
                        .edit().putString("anonymousId", userId).apply();
            }
        }

        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Check if the user is not null before getting the UID
        if (currentUser != null) {
            userId = currentUser.getUid();
            // Now you can safely use the UID
            println("User UID: $uid");
            // ... your code that depends on the UID ...
            authStateListener = firebaseAuth -> {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userId = user.getUid();
                    loadCartItems();
                }
            };

        } else {
            // Handle the case where the user is not logged in
            println("User is not logged in");
            // ... perhaps redirect to the login screen ...
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
*/
        loadCartItems();

        checkoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ReservationActivity.class);
            startActivity(intent);
        });
    }

    private void loadCartItems() {
        Log.d("CartFragment", "Chargement des items du panier pour userId: " + userId);

        db.collection("carts").document(userId).collection("items")
                .get()
                .addOnSuccessListener(query -> {
                    cartItems.clear();
                    for (var doc : query) {
                        CartItem item = doc.toObject(CartItem.class);
                        item.setId(doc.getId());
                        cartItems.add(item);
                    }
                    adapter.notifyDataSetChanged();
                    updateTotal();
                    emptyView.setVisibility(cartItems.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotal();
        }
        totalView.setText(String.format("%.2f TND", total));

        Log.d("CartFragment", "updateTotal: items = " + cartItems.size() + " | total = " + total);

        emptyView.setVisibility(cartItems.isEmpty() ? View.VISIBLE : View.GONE);
    }
}