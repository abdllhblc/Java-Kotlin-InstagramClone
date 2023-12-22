package com.abdullah.kotlinjavainstagram.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.abdullah.kotlinjavainstagram.adapter.FeedAdapter;
import com.abdullah.kotlinjavainstagram.databinding.FragmentFeedBinding;
import com.abdullah.kotlinjavainstagram.model.Post;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Feed_fragment extends Fragment {
    private FragmentFeedBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Post> postArrayList;
    private FeedAdapter feedAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        getData();
        postArrayList = new ArrayList<>();




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(getLayoutInflater(),container,false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add(view);
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        feedAdapter = new FeedAdapter(postArrayList);
        binding.recyclerView.setAdapter(feedAdapter);
    }

    public void add(View view){
        NavDirections action = Feed_fragmentDirections.actionFeedFragmentToUploadFragment();
        Navigation.findNavController(view).navigate(action);


    }
    private void  getData(){
        db.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(requireContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    if(value != null){
                        if(!value.isEmpty()){

                            List<DocumentSnapshot> documents =value.getDocuments();
                                postArrayList.clear();
                            for(DocumentSnapshot document : documents){
                                //casting
                                String comment = (String) document.get("comment");
                                String userEmail = (String) document.get("userEmail");
                                Timestamp time = (Timestamp) document.get("Time");
                                String downloadUrl = (String) document.get("downloadUrl");
                                Date date = time.toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                String formattedDate = sdf.format(date);
                                Post post = new Post(userEmail,comment,downloadUrl,formattedDate);
                                System.out.println(comment);

                                postArrayList.add(post);
                            }
                            feedAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

    }


}