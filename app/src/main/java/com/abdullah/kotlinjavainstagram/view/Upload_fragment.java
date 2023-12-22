package com.abdullah.kotlinjavainstagram.view;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.abdullah.kotlinjavainstagram.databinding.FragmentUploadBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;


public class Upload_fragment extends Fragment {
    private FragmentUploadBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri ImageData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerLauncher();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        binding = FragmentUploadBinding.inflate(getLayoutInflater(),container,false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImage(v);
            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload(v);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    public void upload(View view){
        //universal unique id
        UUID uuid = UUID.randomUUID();
        String imageName = uuid+".jpg"; // her upload yapıldığı zaman storageye yüklenen her resim farklı isimlerle kaydolur.
        StorageReference reference = storage.getReference();
        StorageReference imageReference = reference.child("images").child(imageName);
        if(ImageData != null){
            imageReference.putFile(ImageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //download url -> firestore
                    StorageReference uploadPictureReference = storage.getReference().child("images").child(imageName);
                    uploadPictureReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String downloadUrl = uri.toString();
                            HashMap<String, Object> postMap = new HashMap<>();
                            postMap.put("downloadUrl",downloadUrl);
                            postMap.put("userEmail",auth.getCurrentUser().getEmail());
                            postMap.put("comment",binding.commentText.getText().toString());
                            postMap.put("Time", Timestamp.now());

                            firestore.collection("Posts").add(postMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    NavDirections action = Upload_fragmentDirections.actionUploadFragmentToFeedFragment();
                                    Navigation.findNavController(view).navigate(action);
                                }
                            });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(requireContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void selectedImage(View view){
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){

                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();

            }else{
                //request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        }else{
                //intent gallery
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }

    }

    public void registerLauncher(){

            activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) { // boolean vermedi
                    if(o.getResultCode() == RESULT_OK){
                        Intent intentFromResult = o.getData();
                        if(intentFromResult != null){
                            ImageData =  intentFromResult.getData(); // resim verisinin uris'i(dosya yolu)
                            if(ImageData != null){
                                binding.imageView.setImageURI(ImageData);
                            }

                           /* try {
                                if(Build.VERSION.SDK_INT >= 28){
                                    ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(),ImageData); // veriti decode ediyor
                                    selectedBitmap = ImageDecoder.decodeBitmap(source); // bitmapa çeviriyor
                                    binding.imageView.setImageBitmap(selectedBitmap);

                                }else{
                                    selectedBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),ImageData);
                                    binding.imageView.setImageBitmap(selectedBitmap);
                                }


                            }catch (Exception e){
                                e.setStackTrace();
                            }*/
                        }

                    }

                }
            });



            permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean o) {
                    if(o == true){
                        //permission granted
                        Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activityResultLauncher.launch(intentToGallery);
                    }else{
                        //permission denied
                        Toast.makeText(requireContext(), "Permission needed!", Toast.LENGTH_LONG).show();
                    }

                }
            });
    }
}




