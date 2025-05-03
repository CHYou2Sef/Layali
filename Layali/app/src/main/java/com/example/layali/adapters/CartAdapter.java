package com.example.layali.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.layali.R;
import com.example.layali.activities.ReservationActivity;
import com.example.layali.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private OnCartUpdatedListener cartUpdatedListener;
    private boolean isHistory;
    public interface OnCartUpdatedListener {
        void onCartUpdated();  // pour recalculer le total
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartUpdatedListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartUpdatedListener = listener;
      //  this.isHistory = isHistory;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.title.setText(item.getTitre());
        holder.date.setText(item.getDateS());
        try {
            // Utiliser Picasso au lieu de Glide
            Picasso.get()
                    .load( item.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.image);
            Log.d("CartAdapter", "Loading image from URL: " + item.getImageUrl());
        } catch (Exception e) {
            Log.e("CartAdapter" , "Error loading image with Picasso", e);
            holder.image.setImageResource(R.drawable.error_image);
        }
        holder.quantity.setText("Quantité: " + item.getQuantite());
        holder.price.setText(String.format("%.2f TND", item.getTotal()));

        if (isHistory) {
            holder.paidBadge.setVisibility(View.VISIBLE);
        } else {
            holder.paidBadge.setVisibility(View.GONE);
        }

        Glide.with(context).load(item.getImageUrl()).placeholder(R.drawable.placeholder_image)
                .into(holder.image);

        holder.deleteButton.setOnClickListener(v -> {

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore.getInstance()
                    .collection("carts")
                    .document(userId)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Supprimé du panier", Toast.LENGTH_SHORT).show();
                        cartItems.remove(position);
                        notifyItemRemoved(position);
                        cartUpdatedListener.onCartUpdated();
                    });
             } else {
                Toast.makeText(holder.itemView.getContext(),
                        "Please log in to remove items from cart",
                        Toast.LENGTH_SHORT).show();
            }

        });

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, ReservationActivity.class);
            i.putExtra("spectacleId", item.getSpectacleId());
            i.putExtra("titre", item.getTitre());
            i.putExtra("typeBillet", item.getTypeBillet());
            i.putExtra("quantite", item.getQuantite());
            i.putExtra("imageUrl", item.getImageUrl());
            i.putExtra("prixUnitaire", item.getPrixUnitaire());
            i.putExtra("total", item.getTotal());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, date, quantity, price;
        ImageButton deleteButton;
        TextView paidBadge;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cartItemImageView);
            title = itemView.findViewById(R.id.cartItemTitle);
            date = itemView.findViewById(R.id.cartItemDate);
            quantity = itemView.findViewById(R.id.cartItemQuantity);
            price = itemView.findViewById(R.id.cartItemPrice);
            deleteButton = itemView.findViewById(R.id.deleteItemButton);
            paidBadge = itemView.findViewById(R.id.paidBadge);

        }
    }
}
