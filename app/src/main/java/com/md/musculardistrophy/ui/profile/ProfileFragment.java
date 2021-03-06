package com.md.musculardistrophy.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.md.musculardistrophy.Adapter.TabAdapter;
import com.md.musculardistrophy.Flash_Screen;
import com.md.musculardistrophy.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    TabLayout tabLayout ;
    ViewPager viewPager ;
    TextView userName , Location , totalUser , totalPost ;
    CircleImageView profile ;
    FirebaseAuth auth ;
    String userID ;
    ImageView editProfile  , logOut;
    FirebaseFirestore firebaseFirestore ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        userName = view.findViewById(R.id.username);
        Location = view.findViewById(R.id.location);
        logOut = view.findViewById(R.id.logOut);
        profile = view.findViewById(R.id.circleImageView3);
        firebaseFirestore = FirebaseFirestore.getInstance() ;
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        editProfile= view.findViewById(R.id.imageView2);
        totalUser = view.findViewById(R.id.userCount);
        totalPost = view.findViewById(R.id.postCount);


        firebaseFirestore.collection("user").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    userName.setText(value.getString("userName"));
                    Location.setText(value.getString("location"));
                    Picasso.get().load(value.getString("Profile")).into(profile);
                }
            }
        });


        firebaseFirestore.collection("user").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                totalUser.setText( String.valueOf(value.getDocuments().size()));
            }
        });

        firebaseFirestore.collection("post").whereEqualTo("UID", userID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                totalPost.setText(String.valueOf(value.getDocuments().size()));
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext() , editProfile.class));
            }
        });


        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_dashboard_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_mms_24));


        TabAdapter adapter = new TabAdapter(getChildFragmentManager() , FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));



        viewPager.removeOnAdapterChangeListener(new ViewPager.OnAdapterChangeListener() {
            @Override
            public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.logo)
                        .setTitle("Alert")
                        .setMessage("Are you want to log out ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               auth.signOut();
                               startActivity(new Intent(getContext(), Flash_Screen.class));
                               getActivity().finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


        return  view ;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(getView());;
                navController.navigate(R.id.navigation_home);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }
}