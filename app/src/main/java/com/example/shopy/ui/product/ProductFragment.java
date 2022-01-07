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
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import com.example.shopy.R;
import com.example.shopy.databinding.FragmentProductBinding;
import com.example.shopy.model.Product;
import com.example.shopy.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static android.app.Activity.RESULT_OK;

public class ProductFragment extends Fragment {

    private ProductViewModel productViewModel;
    private FragmentProductBinding binding;

    private ImageView imageView;
    private Button btnChoose;
    private Button btnUpload;
    private Button btnView;
    private EditText inputName;
    private Spinner category;
    private RatingBar ratingBar;
    private EditText inputPrice;
    private TextView inputCurrency;
    private EditText inputDescription;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Product product;
    private NavHost navHostFragment;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

//    public static ProductFragment newInstance() {
//        return new ProductFragment();
//    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Product");

        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);


        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
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

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                Toast.makeText(getActivity(),"Rating: " + rating, Toast.LENGTH_SHORT).show();
            }
        });

        getCurrency();
        getCategory();

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

            String name = inputName.getText().toString().trim();
            String category = this.category.getSelectedItem().toString();
            double price = Double.parseDouble(inputPrice.getText().toString().trim());
            double rating = ratingBar.getRating();
            String description = inputDescription.getText().toString().trim();
            String currency = inputCurrency.getText().toString().trim();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();

                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                Toast.makeText(requireActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                                String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                                product = new Product(userId, name, category, price, currency, description, uri.toString(), rating);
                                mDatabase.push().setValue(product);
                            }
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

    private void getCategory()
    {
        // Make ENUM for Categories or Strings of ID R.id.String
        String[] ProductCategories = new String[]{getString(R.string.electronics), getString(R.string.computer),
                getString(R.string.home_appliance), getString(R.string.phones),
                getString(R.string.books), getString(R.string.games)};

        final List<String> countryList = new ArrayList<>(Arrays.asList(ProductCategories));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(), android.R.layout.simple_spinner_item, countryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
    }

    private void getCurrency()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = Objects.requireNonNull(user).getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("User");
        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot)
            {
                String country = Objects.requireNonNull(dataSnapshot.getValue(User.class)).getCountry();
                if (country.equals("Cameroon"))
                {
                    inputCurrency.setText("CFA");
                }
                else if (country.equals("Nigeria"))
                {
                    inputCurrency.setText("NGN");
                }
                else if (country.equals("Ghana"))
                {
                    inputCurrency.setText("NGN");
                }
                inputCurrency.setEnabled(false);
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