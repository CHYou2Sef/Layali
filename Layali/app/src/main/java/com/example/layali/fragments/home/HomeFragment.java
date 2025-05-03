package com.example.layali.fragments.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layali.adapters.SpectacleAdapter;
import com.example.layali.models.Spectacle;
import com.google.android.material.chip.Chip;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import com.example.layali.R;

import java.util.ArrayList;
import java.util.Comparator;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private String currentSearchQuery = "";
    private SearchView searchView;
    private SpectacleAdapter adapter;
    private ArrayList<Spectacle> spectacleList;
    private FirebaseFirestore db;

    Chip btnFilterDate, btnFilterPrice, btnFilterCity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btnFilterDate = view.findViewById(R.id.chipDate);
        btnFilterPrice = view.findViewById(R.id.chipLieu);
        btnFilterCity = view.findViewById(R.id.chipPrix);
        searchView = view.findViewById(R.id.searchView);

        recyclerView = view.findViewById(R.id.spectaclesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        spectacleList = new ArrayList<>();
        adapter = new SpectacleAdapter(getContext(), spectacleList);
        recyclerView.setAdapter(adapter);

        // Initialisation sécurisée
        if(FirebaseApp.getApps(requireContext()).isEmpty()) {
            FirebaseApp.initializeApp(requireContext());
        }
        db = FirebaseFirestore.getInstance();



        // Setup SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query;
                loadSpectacles();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() && !currentSearchQuery.isEmpty()) {
                    currentSearchQuery = "";
                    loadSpectacles();
                }
                return true;
            }
        });

        loadSpectacles();

        btnFilterDate.setOnClickListener(v -> filterByDate());
        btnFilterPrice.setOnClickListener(v -> filterByPrice());
        btnFilterCity.setOnClickListener(v -> filterByCity());

        return view;

    }



    private void loadSpectacles() {
        db.collection("spectacles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        spectacleList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Spectacle spectacle = document.toObject(Spectacle.class);
                            spectacle.setIdSpec(String.valueOf(document.getId()));
                            spectacleList.add(spectacle);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void filterByDate() {
        // For example, sort by date ascending
        spectacleList.sort(Comparator.comparing(Spectacle::getDateS));
        adapter.notifyDataSetChanged();
    }

    private void filterByPrice() {
        // Suppose we sort by prix "Normal"
        spectacleList.sort(Comparator.comparingDouble(Spectacle::getPrix));
        adapter.notifyDataSetChanged();
    }

    private void filterByCity() {
        // Sort by city name
        spectacleList.sort(Comparator.comparing(s -> s.getLieu().getVille()));
        adapter.notifyDataSetChanged();
    }



}
/*


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
}
/*
public class HomeFragment extends Fragment implements SpectacleAdapter.OnSpectacleClickListener {

    private RecyclerView spectaclesRecyclerView;
    private TextView emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private ChipGroup filterChipGroup;
    private FloatingActionButton fabFilter;

    private SpectacleAdapter spectacleAdapter;
    private List<Spectacle> spectacleList;
    private ApiHandler apiHandler;

    private String currentSearchQuery = "";
    private String currentDateFilter = "";
    private String currentLieuFilter = "";
    private String currentPrixFilter = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        spectaclesRecyclerView = view.findViewById(R.id.spectaclesRecyclerView);
        emptyView = view.findViewById(R.id.emptyView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        searchView = view.findViewById(R.id.searchView);
        filterChipGroup = view.findViewById(R.id.filterChipGroup);
        fabFilter = view.findViewById(R.id.fabFilter);

        // Initialize API handler
        apiHandler = new ApiHandler(getContext());

        // Setup RecyclerView
        spectacleList = new ArrayList<>();
        spectacleAdapter = new SpectacleAdapter(getContext(), spectacleList, this);
        spectaclesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        spectaclesRecyclerView.setAdapter(spectacleAdapter);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadSpectacles);

        // Setup SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query;
                loadSpectacles();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() && !currentSearchQuery.isEmpty()) {
                    currentSearchQuery = "";
                    loadSpectacles();
                }
                return true;
            }
        });

        // Setup Filter FAB
        fabFilter.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FilterActivity.class);
            intent.putExtra("dateFilter", currentDateFilter);
            intent.putExtra("lieuFilter", currentLieuFilter);
            intent.putExtra("prixFilter", currentPrixFilter);
            startActivityForResult(intent, 100);
        });

        // Setup Chip Group
        setupChipGroup();

        // Load spectacles
        loadSpectacles();
    }

    private void setupChipGroup() {
        Chip chipDate = filterChipGroup.findViewById(R.id.chipDate);
        Chip chipLieu = filterChipGroup.findViewById(R.id.chipLieu);
        Chip chipPrix = filterChipGroup.findViewById(R.id.chipPrix);

        chipDate.setOnClickListener(v -> {
            // Show date picker dialog
            // For simplicity, we'll just toggle the chip
            if (chipDate.isChecked()) {
                currentDateFilter = "asc";
            } else {
                currentDateFilter = "";
            }
            loadSpectacles();
        });

        chipLieu.setOnClickListener(v -> {
            // Show lieu filter dialog
            // For simplicity, we'll just toggle the chip
            if (chipLieu.isChecked()) {
                currentLieuFilter = "asc";
            } else {
                currentLieuFilter = "";
            }
            loadSpectacles();
        });

        chipPrix.setOnClickListener(v -> {
            // Show prix filter dialog
            // For simplicity, we'll just toggle the chip
            if (chipPrix.isChecked()) {
                currentPrixFilter = "asc";
            } else {
                currentPrixFilter = "";
            }
            loadSpectacles();
        });
    }

    private void loadSpectacles() {
        swipeRefreshLayout.setRefreshing(true);

        apiHandler.getSpectacles(currentSearchQuery, currentDateFilter, currentLieuFilter, currentPrixFilter, new ApiHandler.ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                spectacleList.clear();
                try {
                    JSONArray spectaclesArray = response.getJSONArray("spectacles");
                    for (int i = 0; i < spectaclesArray.length(); i++) {
                        JSONObject spectacleObj = spectaclesArray.getJSONObject(i);
                        Spectacle spectacle = new Spectacle();
                        spectacle.setIdSpec(spectacleObj.getInt("idSpec"));
                        spectacle.setTitre(spectacleObj.getString("titre"));
                        spectacle.setDateS(spectacleObj.getString("dateS"));
                        spectacle.setH_DebutS(spectacleObj.getString("H_DebutS"));
                        spectacle.setDuree(spectacleObj.getString("duree"));
                        spectacle.setNbrSpectateur(spectacleObj.getInt("nbrSpectateur"));
                        spectacle.setDescription(spectacleObj.getString("description"));
                        spectacle.setSite_web(spectacleObj.getString("site_web"));
                        spectacle.setIdLieu(spectacleObj.getInt("idLieu"));
                        spectacle.setImg(spectacleObj.getString("img"));

                        // Add extra fields from joined tables if available
                        if (spectacleObj.has("nomLieu")) {
                            spectacle.setLieuName(spectacleObj.getString("nomLieu"));
                        }
                        if (spectacleObj.has("ville")) {
                            spectacle.setVille(spectacleObj.getString("ville"));
                        }

                        spectacleList.add(spectacle);
                    }

                    if (spectacleList.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                        spectaclesRecyclerView.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        spectaclesRecyclerView.setVisibility(View.VISIBLE);
                    }

                    spectacleAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Erreur de parsing JSON", Toast.LENGTH_SHORT).show();
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onSpectacleClick(Spectacle spectacle) {
        Intent intent = new Intent(getContext(), SpectacleDetailActivity.class);
        intent.putExtra("spectacle_id", spectacle.getIdSpec());
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == -1 && data != null) {
            currentDateFilter = data.getStringExtra("dateFilter");
            currentLieuFilter = data.getStringExtra("lieuFilter");
            currentPrixFilter = data.getStringExtra("prixFilter");
            loadSpectacles();
        }
    }
}

*/