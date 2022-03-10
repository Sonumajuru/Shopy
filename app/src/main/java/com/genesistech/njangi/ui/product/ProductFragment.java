package com.genesistech.njangi.ui.product;

import android.annotation.SuppressLint;
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
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;
import com.genesistech.njangi.Controller;
import com.genesistech.njangi.R;
import com.genesistech.njangi.adapter.ImagePagerAdapter;
import com.genesistech.njangi.databinding.FragmentProductBinding;
import com.genesistech.njangi.helper.FirebaseApp;
import com.genesistech.njangi.interfaces.FragmentCallback;
import com.genesistech.njangi.model.Product;
import com.genesistech.njangi.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    private UploadTask uploadTask; // Formerly StorageTask
    private Product product;
    private DatabaseReference mDatabase;
    private  StorageReference storageReference;

    private final int PICK_IMAGE_REQUEST = 22;
    private long maxId;
    private ProgressBar progressBar;
    private String seller;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<String> newImageList;

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ProductViewModel productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        user = new User();
        firebaseApp = new FirebaseApp();
        storageReference = FirebaseStorage.getInstance().getReference();
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

        Bundle bundle = getArguments();
        productViewModel.getTextChoose().observe(getViewLifecycleOwner(), btnChoose::setText);
        btnChoose.setOnClickListener(v -> chooseImage());
        if(bundle !=null)
        {
            fileUris = new ArrayList<>();
            productViewModel.getTextUpload(false).observe(getViewLifecycleOwner(), btnUpload::setText);
            productViewModel.getTextStock(false).observe(getViewLifecycleOwner(), btnView::setText);
            btnView.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
            btnUpload.setOnClickListener(v -> updateProduct());

            assert getArguments() != null;
            product = getArguments().getParcelable("product");
            // TODO: Load images
            //    - Update product in DB using ProdID
            //    - Add Image (Choose) - Update (Upload) - Cancel (Stocks)

            ratingBar.setRating((float) product.getRating());
            ratingBar.setEnabled(false);
            inputTitle.setText(product.getTitle());

            ArrayAdapter<String> categoryAdapter = productViewModel.getAdapter();
            int categoryPosition = 0;
            for (int i = 0; i < categoryAdapter.getCount(); i++)
            {
                if (categoryAdapter.getItem(i).equals(product.getCategory()))
                {
                    categoryPosition = i;
                }
            }
            category.setAdapter(productViewModel.getAdapter());
            category.setSelection(categoryPosition);
            category.setEnabled(false);
            inputCurrency.setText(product.getCurrency());
            inputCurrency.setEnabled(false);
            inputDescription.setText(product.getDescription());
            inputPrice.setText(String.format("%.2f", product.getPrice()));
            for (int i = 0; i < product.getImages().size(); i++) {
                fileUris.add(Uri.parse(product.getImages().get(i)));
            }

            // Handle Object of list item here
            FragmentCallback callback = new FragmentCallback() {
                @Override
                public void doSomething() {
                }

                @Override
                public void onItemClicked(int position, Object object)
                {
                    newImageList = new ArrayList<>();
                    fileUris = new ArrayList<>();
                    for (int i = 0; i < product.getImages().size(); i++) {
                        if (i == position) {
                            product.getImages().remove(position);
                        }
                        newImageList.add(product.getImages().toString());
                    }
                    setViewPager(this);
                    for (int i = 0; i < product.getImages().size(); i++) {
                        fileUris.add(Uri.parse(product.getImages().get(i)));
                    }
                }

                @Override
                public void onItemClicked(int position, Object object, int id) {

                }
            };
            setViewPager(callback);
        }
        else
        {
            productViewModel.getTextUpload(true).observe(getViewLifecycleOwner(), btnUpload::setText);
            productViewModel.getTextStock(true).observe(getViewLifecycleOwner(), btnView::setText);
            btnUpload.setOnClickListener(v -> publishProduct());
            btnView.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_stock));

            ratingBar.setOnRatingBarChangeListener((ratingBar, rating, b) -> {
//                Toast.makeText(getActivity(),"Rating: " + rating, Toast.LENGTH_SHORT).show();
            });

            getUserData();
            productViewModel.getCurrency();
            category.setAdapter(productViewModel.getAdapter());
            productViewModel.getText().observe(getViewLifecycleOwner(), s -> {
                inputCurrency.setText(s);
            });
        }

        return root;
    }

    private void chooseImage()
    {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
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

        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(requireContext(), ProductFragment.this, targetList, null);
        viewPager.setAdapter(imagePagerAdapter);
        tabLayout.setupWithViewPager(viewPager, true);
    }

    private void publishProduct()
    {
        if (fieldCheck()) return;
        if (fileUris == null) return;
        if (fileUris.size() == 0) return;
        product = new Product();
        uploadedImages = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        String title = inputTitle.getText().toString().trim();
        String category = controller.getCategoryTranslation(this.category.getSelectedItem().toString());
        double price = Double.parseDouble(inputPrice.getText().toString().trim());
        double rating = ratingBar.getRating();
        String description = inputDescription.getText().toString().trim();
        String currency = inputCurrency.getText().toString().trim();

        mDatabase = FirebaseDatabase
                .getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("ProductDB");

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
            uploadTask = photoRef.putFile(file);

            uploadTask.addOnSuccessListener(taskSnapshot -> uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());

                }
                // Continue with the task to get the download URL
                return photoRef.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Uri downloadUrl = task.getResult();

                    assert downloadUrl != null;
                    uploadedImages.add(downloadUrl.toString());

                    String uuid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    product = new Product(String.valueOf(maxId+1), uuid, seller, title,
                            controller.getCategoryTranslation(category), price,
                            currency, description, uploadedImages, rating,"0", key, "", "0");

                    Map<String, Object> productValues = product.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/products/" + key, productValues);
                    childUpdates.put("/user-products/" + uuid + "/" + key, productValues);

                    mDatabase.updateChildren(childUpdates);
                }
            })).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateProduct()
    {
        if (fieldCheck()) return;
        if (fileUris == null) return;
        if (fileUris.size() == 0) return;
        product = new Product();
        uploadedImages = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        String title = inputTitle.getText().toString().trim();
        String category = controller.getCategoryTranslation(this.category.getSelectedItem().toString());
        double price = Double.parseDouble(inputPrice.getText().toString().trim());
        String description = inputDescription.getText().toString().trim();

        mDatabase = FirebaseDatabase
                .getInstance("https://shopy-a60b9-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("ProductDB");

        for (Uri file : fileUris)
        {
            StorageReference photoRef = storageReference.child("images/" + file.getLastPathSegment());
            uploadTask = photoRef.putFile(file);

            uploadTask.addOnSuccessListener(taskSnapshot -> uploadTask.continueWithTask(task ->
            {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());

                }
                // Continue with the task to get the download URL
                return photoRef.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Uri downloadUrl = task.getResult();

                    assert downloadUrl != null;
                    uploadedImages.add(downloadUrl.toString());

                    product = new Product(product.getId(), product.getUuid(),
                            product.getSeller(), title, controller.getCategoryTranslation(category),
                            price, product.getCurrency(), description, uploadedImages,
                            product.getRating(),"0", product.getProdID(),
                            "", "0");

                    Map<String, Object> productValues = product.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/products/" + product.getProdID(), productValues);
                    childUpdates.put("/user-products/" + product.getUuid() + "/" + product.getProdID(),
                            productValues);

                    mDatabase.updateChildren(childUpdates);
                }
            })).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private boolean fieldCheck()
    {
        return inputTitle.getText().toString().isEmpty()
                || inputPrice.getText().toString().isEmpty()
                || inputDescription.getText().toString().isEmpty();
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

    private void setViewPager(FragmentCallback callback)
    {
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(requireContext(),
                ProductFragment.this, product.getImages(), callback);
        viewPager.setAdapter(imagePagerAdapter);
        tabLayout.setupWithViewPager(viewPager, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}