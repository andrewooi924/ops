package com.optcg.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.optcg.app.data.repository.UserRepository;
import com.optcg.app.di.ServiceLocator;
import com.optcg.app.sync.AuthManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainMenuAdapter mainMenuAdapter;
    private List<String> menuItems;   // Assuming you have data for menu items
    private List<Integer> menuImages; // Assuming you have data for menu images
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private CircleIndicator3 indicator;
    private List<Integer> imageResources;
    private List<Card> cards;
    private UserRepository userRepository;
    private TextView berriesText;
    private CardViewModel cardViewModel;
    private AuthManager authManager;
    private TextView accountText;
    private Button signInButton;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authManager = ServiceLocator.get(requireContext()).authManager();
        // Must be registered before the fragment is started.
        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> authManager.handleSignInResult(result.getData(), (success, error) -> {
                    if (!isAdded()) {
                        return;
                    }
                    if (success) {
                        Toast.makeText(requireContext(), "Signed in", Toast.LENGTH_SHORT).show();
                        // Pull/push the user's data now that we have an account.
                        ServiceLocator.get(requireContext()).syncManager().syncNow();
                    } else {
                        Toast.makeText(requireContext(), error != null ? error : "Sign-in failed", Toast.LENGTH_LONG).show();
                    }
                }));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Prepare data for the adapter (menuItems and menuImages need to be initialized here or passed in)
        menuItems = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            menuItems.add("OP0" + i);
        }

        for (int i = 0; i < 1; i++) {
            menuItems.add("OP1" + i);
        }

        menuImages = new ArrayList<>();
        menuImages.add(R.drawable.op01_box);
        menuImages.add(R.drawable.op02_box);
        menuImages.add(R.drawable.op03_box);
        menuImages.add(R.drawable.op04_box);
        menuImages.add(R.drawable.op05_box);
        menuImages.add(R.drawable.op06_box);
        menuImages.add(R.drawable.op07_box);
        menuImages.add(R.drawable.op08_box);
        menuImages.add(R.drawable.op09_box);
        menuImages.add(R.drawable.op10_box);

        // Initialize and set the adapter
        mainMenuAdapter = new MainMenuAdapter(requireActivity().getSupportFragmentManager(), getContext(), menuItems, menuImages);
        recyclerView.setAdapter(mainMenuAdapter);

        viewPager = view.findViewById(R.id.viewPager);
        indicator = view.findViewById(R.id.indicator);
        berriesText = view.findViewById(R.id.berriesText);

        userRepository = ServiceLocator.get(requireContext()).userRepository();
        berriesText.setText(String.valueOf(userRepository.getBerries()));

        // Account / sign-in control.
        accountText = view.findViewById(R.id.accountText);
        signInButton = view.findViewById(R.id.signInButton);
        authManager.session().observe(getViewLifecycleOwner(), s -> {
            boolean signedIn = s != null && s.signedIn;
            if (signedIn) {
                String who = s.displayName != null ? s.displayName : (s.email != null ? s.email : "Signed in");
                accountText.setText(who);
                signInButton.setText(R.string.sign_out);
            } else {
                accountText.setText("Not signed in");
                signInButton.setText(R.string.sign_in);
            }
        });
        signInButton.setOnClickListener(v -> {
            if (authManager.isSignedIn()) {
                authManager.signOut(() -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Intent intent = authManager.getSignInIntent();
                if (intent != null) {
                    signInLauncher.launch(intent);
                } else {
                    Toast.makeText(requireContext(), "Sign-in unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageResources = Arrays.asList(
                R.drawable.op01_001, R.drawable.op01_002, R.drawable.op01_003, R.drawable.op01_004, R.drawable.op01_005
        );

        // Card data now comes from the repository (Room cache, seeded from cards.json) —
        // no JSON parsing in the UI. Observe and wire the pager once data is available.
        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);
        cardViewModel.getAllCards().observe(getViewLifecycleOwner(), cardList -> {
            if (cardList == null || cardList.isEmpty() || viewPager.getAdapter() != null) {
                return;
            }
            cards = cardList;
            viewPagerAdapter = new ViewPagerAdapter(cards, imageResources);
            viewPager.setAdapter(viewPagerAdapter);

            viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
                @Override
                public void transformPage(@NonNull View page, float position) {
                    page.setScaleY(0.85f + (1 - Math.abs(position)) * 0.15f);
                }
            });

            indicator.setViewPager(viewPager);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        berriesText.setText(String.valueOf(userRepository.getBerries()));
    }
}
