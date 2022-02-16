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
import com.example.shopy.Controller;
import com.example.shopy.databinding.FragmentProductBinding;
import com.example.shopy.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class ProductFragment extends Fragment {

    private ProductViewModel productViewModel;
    private FragmentProductBinding binding;
    private Controller controller;

    private Button btnChoose;
    private Button btnUpload;
    private Button btnView;
    private Spinner category;
    private RatingBar ratingBar;
    private TextView inputCurrency;
    private EditText inputTitle;
    private EditText inputPrice;
    private EditText inputDescription;

    private Uri imageUri;
    private ArrayList<Uri> fileUris;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;

    private Product product;
    private final int PICK_IMAGE_REQUEST = 22;
    private long maxId;
    private ProgressBar progressBar;
    private LinearLayout layout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mDatabase = FirebaseDatabase
                .getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Product");
//                .child("Njangi-Deals");

        mDatabase.addValueEventListener(new ValueEventListener() {
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

        // instance for firebase storage and StorageReference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        product = new Product();
        controller = Controller.getInstance(requireContext());

        btnChoose = binding.btnChoose;
        btnUpload = binding.btnUpload;
        btnView = binding.btnView;
        ratingBar = binding.ratingBar;
        inputTitle = binding.txtName;
        category = binding.category;
        inputCurrency = binding.txtCurrency;
        inputDescription = binding.txtDescription;
        inputPrice = binding.txtPrice;
        progressBar = binding.progressBar;
        layout = binding.imageLayout;

        btnChoose.setOnClickListener(v -> chooseImage());
        btnUpload.setOnClickListener(v -> uploadImage());

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, b) -> {
//                Toast.makeText(getActivity(),"Rating: " + rating, Toast.LENGTH_SHORT).show();
        });

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
                    imageSet();
                }
                else if(data.getData() != null) {
                    imageUri = data.getData();
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

    private void imageSet()
    {
        for(int i=0; i < fileUris.size(); i++)
        {
            if (i >= 4) return;
            else {
                ImageView imgView = new ImageView(requireContext());
                imgView.setAdjustViewBounds(true);
                layout.addView(imgView);
                Picasso.with(requireContext()).load(fileUris.get(i)).into(imgView);
            }
        }
    }

    private void uploadImage()
    {
        if(imageUri != null)
        {
            progressBar.setVisibility(View.VISIBLE);

            String title = inputTitle.getText().toString().trim();
            String category = this.category.getSelectedItem().toString();
            double price = Double.parseDouble(inputPrice.getText().toString().trim());
            double rating = ratingBar.getRating();
            String description = inputDescription.getText().toString().trim();
            String currency = inputCurrency.getText().toString().trim();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                        progressBar.setVisibility(View.GONE);

                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            Toast.makeText(requireContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                            product = new Product(String.valueOf(maxId+1), userId, title, controller.getDefaultTranslation(category), price,
                                    currency, description, uri.toString(), rating,"0");
                            mDatabase.push().setValue(product);
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int)progress);
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}