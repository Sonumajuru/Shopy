package com.example.shopy.ui.product;

import android.app.ProgressDialog;
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
import com.example.shopy.databinding.FragmentProductBinding;
import com.example.shopy.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class ProductFragment extends Fragment {

    private ProductViewModel productViewModel;
    private FragmentProductBinding binding;

    private ImageView imageView;
    private ImageView btnChoose;
    private ImageView btnUpload;
    private ImageView btnView;
    private Spinner category;
    private RatingBar ratingBar;
    private TextView inputCurrency;
    private EditText inputName;
    private EditText inputPrice;
    private EditText inputDescription;

    private Uri filePath;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;

    private Product product;
    private final int PICK_IMAGE_REQUEST = 22;
    private long maxId;

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

        imageView = binding.imgViewProduct;
        btnChoose = binding.btnChoose;
        btnUpload = binding.btnUpload;
        btnView = binding.btnView;
        ratingBar = binding.ratingBar;
        inputName = binding.txtName;
        category = binding.category;
        inputCurrency = binding.txtCurrency;
        inputDescription = binding.txtDescription;
        inputPrice = binding.txtPrice;

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
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage()
    {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            String title = inputName.getText().toString().trim();
            String category = this.category.getSelectedItem().toString();
            double price = Double.parseDouble(inputPrice.getText().toString().trim());
            double rating = ratingBar.getRating();
            String description = inputDescription.getText().toString().trim();
            String currency = inputCurrency.getText().toString().trim();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();

                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            Toast.makeText(requireActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                            String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                            product = new Product(String.valueOf(maxId+1), userId, title, category, price,
                                    currency, description, uri.toString(), rating,"0");
                            mDatabase.push().setValue(product);
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(requireActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}