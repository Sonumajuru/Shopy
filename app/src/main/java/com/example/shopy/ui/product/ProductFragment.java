package com.example.shopy.ui.product;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import com.example.shopy.Controller;
import com.example.shopy.adapter.ImagePagerAdapter;
import com.example.shopy.databinding.FragmentProductBinding;
import com.example.shopy.helper.FirebaseApp;
import com.example.shopy.model.Product;
import com.example.shopy.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static android.app.Activity.RESULT_OK;

public class ProductFragment extends Fragment {

    private FragmentProductBinding binding;
    private Controller controller;

    private User user;
    private FirebaseApp firebaseApp;

    private Spinner category;
    private RatingBar ratingBar;
    private TextView inputCurrency;
    private EditText inputTitle;
    private EditText inputPrice;
    private EditText inputDescription;

    private List<Uri> fileUris;
    private List<String> uploadedImages;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private StorageTask mUploadTask;

    private Product product;
    private final int PICK_IMAGE_REQUEST = 22;
    private long maxId;
    private ProgressBar progressBar;
    private String seller;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ProductViewModel productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // instance for firebase storage and StorageReference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = new User();
        product = new Product();
        uploadedImages = new ArrayList<>();
        firebaseApp = new FirebaseApp();
        controller = Controller.getInstance(requireContext());

        Button btnChoose = binding.btnChoose;
        Button btnUpload = binding.btnUpload;
        Button btnView = binding.btnView;
        ratingBar = binding.ratingBar;
        inputTitle = binding.txtName;
        category = binding.category;
        inputCurrency = binding.txtCurrency;
        inputDescription = binding.txtDescription;
        inputPrice = binding.txtPrice;
        progressBar = binding.progressBar;
        viewPager = binding.imageLayout;
        tabLayout = binding.tabDots;

        btnChoose.setOnClickListener(v -> chooseImage());
        btnUpload.setOnClickListener(v -> publishProduct());

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, b) -> {
//                Toast.makeText(getActivity(),"Rating: " + rating, Toast.LENGTH_SHORT).show();
        });

        getUserData();
        productViewModel.getCurrency();
        category.setAdapter(productViewModel.getAdapter());
        productViewModel.getText().observe(getViewLifecycleOwner(), s -> {
            inputCurrency.setText(s);
        });

        return root;
    }

    private void chooseImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            fileUris = new ArrayList<>();
            if(resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST)
            {
                if(data.getClipData() != null)
                {
                    int count = data.getClipData().getItemCount();
                    for(int i = 0; i < count; i++) {
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                        ClipData.Item item = data.getClipData().getItemAt(i);
                        Uri uri = item.getUri();
                        fileUris.add(uri); /// add the images to list
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), fileUris.get(i)); //pass    the images with position
//                        imageView.setImageBitmap(bitmap);
                    }
                    getImages();
                }
                else if(data.getData() != null) {
                    Uri imageUri = data.getData();
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
//                    imageView.setImageBitmap(bitmap);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void getImages()
    {
        List<String> targetList = new ArrayList<>();
        fileUris.forEach(uri -> targetList.add(uri.toString()));

        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(requireContext(), targetList);
        viewPager.setAdapter(imagePagerAdapter);
        tabLayout.setupWithViewPager(viewPager, true);
    }

    private void publishProduct()
    {
        progressBar.setVisibility(View.VISIBLE);

        String title = inputTitle.getText().toString().trim();
        String category = this.category.getSelectedItem().toString();
        double price = Double.parseDouble(inputPrice.getText().toString().trim());
        double rating = ratingBar.getRating();
        String description = inputDescription.getText().toString().trim();
        String currency = inputCurrency.getText().toString().trim();

        mDatabase = FirebaseDatabase
                .getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("ProductDB");
//                .child("Njangi-Deals");
        mDatabase.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    maxId = snapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        String key = mDatabase.child("products").push().getKey();

        for (Uri file : fileUris)
        {
            StorageReference photoRef = storageReference.child("images/" + file.getLastPathSegment());
            mUploadTask = photoRef.putFile(file);

            mUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();

                            }
                            // Continue with the task to get the download URL
                            return photoRef.getDownloadUrl();

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Uri downloadUrl = task.getResult();

                                assert downloadUrl != null;
                                uploadedImages.add(downloadUrl.toString());

                                String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                                product = new Product(String.valueOf(maxId+1), uuid, seller, title,
                                        controller.getDefaultTranslation(category), price,
                                        currency, description, uploadedImages, rating,"0");

                                Map<String, Object> productValues = product.toMap();

                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("/products/" + key, productValues);
                                childUpdates.put("/user-products/" + uuid + "/" + key, productValues);

                                mDatabase.updateChildren(childUpdates);
                            }
                        }
                    });

                }
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void getUserData()
    {
        if (firebaseApp.getAuth().getCurrentUser() == null) return;
        String userid = Objects.requireNonNull(firebaseApp.getAuth().getCurrentUser()).getUid();
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("User");
        ref.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot)
            {
                user = dataSnapshot.getValue(User.class);
                if (user != null)
                {
                    seller = user.getFirstName();
                }
                else
                {
                    getUserData();
                }
            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}