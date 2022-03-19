package com.genesistech.njangi.ui.product;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;

public class ProductFragment extends Fragment {

    private FragmentProductBinding binding;
    private Controller controller;
    private FirebaseApp firebaseApp;
    private ProductViewModel productViewModel;

    private Spinner category;
    private RatingBar ratingBar;
    private TextView inputCurrency;
    private EditText inputTitle;
    private EditText inputPrice;
    private EditText inputDescription;
    private List<Uri> fileUris;
    private List<String> uploadedImages;
    private UploadTask uploadTask;
    private Product product;
    private  StorageReference storageReference;

    private long maxId;
    private ProgressBar progressBar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private boolean isUpdating = false;
    ActivityResultLauncher<Intent> someActivityResultLauncher;

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseApp = new FirebaseApp();
        storageReference = FirebaseStorage.getInstance().getReference();
        controller = Controller.getInstance(requireContext());

        Button btnChoose = binding.btnChoose;
        Button btnUpload = binding.btnUpload;
        Button btnBoutique = binding.btnBoutique;
        ratingBar = binding.ratingBar;
        inputTitle = binding.txtName;
        category = binding.category;
        inputCurrency = binding.txtCurrency;
        inputDescription = binding.txtDescription;
        inputPrice = binding.txtPrice;
        progressBar = binding.progressBar;
        viewPager = binding.imageLayout;
        tabLayout = binding.tabDots;

        setRegisterForActivity();
        Bundle bundle = getArguments();
        productViewModel.getTextChoose().observe(getViewLifecycleOwner(), btnChoose::setText);
        btnChoose.setOnClickListener(v -> launchImageIntent());
        if(bundle !=null) {
            fileUris = new ArrayList<>();
            isUpdating = true;
            productViewModel.getTextUpload(false).observe(getViewLifecycleOwner(), btnUpload::setText);
            productViewModel.getTextStock(false).observe(getViewLifecycleOwner(), btnBoutique::setText);
            btnBoutique.setOnClickListener(v -> {
                Navigation.findNavController(v).popBackStack();
                ratingBar.setRating(0);
                inputTitle.setText("");
                category.setSelection(0);
                inputCurrency.setText("");
                inputDescription.setText("");
                inputPrice.setText("");
            });
            btnUpload.setOnClickListener(v -> updateProduct());

            assert getArguments() != null;
            product = getArguments().getParcelable("product");

            ratingBar.setRating((float) product.getRating());
            ratingBar.setEnabled(false);
            inputTitle.setText(product.getTitle());

            ArrayAdapter<String> categoryAdapter = productViewModel.getAdapter();
            int categoryPosition = 0;
            for (int i = 0; i < categoryAdapter.getCount(); i++) {
                if (categoryAdapter.getItem(i).equals(product.getCategory())) {
                    categoryPosition = i;
                }
            }
            category.setAdapter(productViewModel.getAdapter());
            category.setSelection(categoryPosition);
            category.setEnabled(false);
            inputCurrency.setText(product.getCurrency());
            inputCurrency.setEnabled(false);
            inputDescription.setText(product.getDescription());

            DecimalFormat df = new DecimalFormat("###.#");
            inputPrice.setText(df.format(product.getPrice()));
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
                    fileUris = new ArrayList<>();
                    for (int i = 0; i < product.getImages().size(); i++) {
                        if (i == position) {
                            product.getImages().remove(position);
                        }
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
        else {
            productViewModel.getTextUpload(true).observe(getViewLifecycleOwner(), btnUpload::setText);
            productViewModel.getTextStock(true).observe(getViewLifecycleOwner(), btnBoutique::setText);
            btnUpload.setOnClickListener(v -> publishProduct());
            btnBoutique.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.navigation_boutique);
                ratingBar.setRating(0);
                inputTitle.setText("");
                category.setSelection(0);
                inputCurrency.setText("");
                inputDescription.setText("");
                inputPrice.setText("");
            });

            ratingBar.setOnRatingBarChangeListener((ratingBar, rating, b) -> {
//                Toast.makeText(getActivity(),"Rating: " + rating, Toast.LENGTH_SHORT).show();
            });

            productViewModel.getCurrency();
            category.setAdapter(productViewModel.getAdapter());
            productViewModel.getCurrencySign().observe(getViewLifecycleOwner(), s -> inputCurrency.setText(s));
        }

        return root;
    }

    private void setRegisterForActivity()
    {
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        assert result.getData() != null;
                        if (!isUpdating)
                        {
                            fileUris = new ArrayList<>();
                        }
                        if( result.getData().getClipData() != null)
                        {
                            int count = result.getData().getClipData().getItemCount();
                            for(int i = 0; i < count; i++) {
                                ClipData.Item item =  result.getData().getClipData().getItemAt(i);
                                Uri uri = item.getUri();
                                fileUris.add(uri);
                            }
                        }
                        else if(result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            fileUris.add(imageUri);
                        }
                        getImages();
                    }
                });
    }

    public void launchImageIntent() {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);
    }

    @SuppressLint("NewApi")
    private void getImages() {
        List<String> targetList = new ArrayList<>();
        fileUris.forEach(uri -> targetList.add(uri.toString()));

        // Handle Object of list item here
        FragmentCallback callback = new FragmentCallback() {
            @Override
            public void doSomething() {
            }

            @Override
            public void onItemClicked(int position, Object object)
            {
                fileUris = new ArrayList<>();
                for (int i = 0; i < targetList.size(); i++) {
                    if (i == position) {
                        targetList.remove(position);
                    }
                }
                setViewPager(this, targetList);
                for (String s : targetList) {
                    fileUris.add(Uri.parse(s));
                }
            }

            @Override
            public void onItemClicked(int position, Object object, int id) {

            }
        };
        setViewPager(callback, targetList);
    }

    private void publishProduct() {
        if (fieldCheck()) {
            Toast.makeText(requireActivity(), "Some fields are empty..", Toast.LENGTH_SHORT).show();
            return;
        }
        if (fileUris == null) return;
        if (fileUris.size() == 0) return;
        product = new Product();
        uploadedImages = new ArrayList<>();

        try {
            progressBar.setVisibility(View.VISIBLE);
            String title = inputTitle.getText().toString().trim();
            String category = controller.getCategoryTranslation(this.category.getSelectedItem().toString());
            double price = Double.parseDouble(inputPrice.getText().toString().trim());
            double rating = ratingBar.getRating();
            String description = inputDescription.getText().toString().trim();
            String currency = inputCurrency.getText().toString().trim();

            firebaseApp.getFirebaseDB().getReference("ProductDB").child("products").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        maxId = snapshot.getChildrenCount();
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

            String key = firebaseApp.getFirebaseDB().getReference("ProductDB").child("products").push().getKey();
            for (Uri file : fileUris) {
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
                        product = new Product(String.valueOf(maxId+1), uuid,
                                productViewModel.getSeller(), title,
                                controller.getCategoryTranslation(category), price,
                                currency, description, uploadedImages, rating,"0", key, "", "0");

                        Map<String, Object> productValues = product.toMap();

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/products/" + key, productValues);
                        childUpdates.put("/user-products/" + uuid + "/" + key, productValues);

                        firebaseApp.getFirebaseDB()
                                .getReference()
                                .child("ProductDB").updateChildren(childUpdates);
                    }
                })).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProduct() {
        if (fieldCheck()) {
            Toast.makeText(requireActivity(), "Some fields are empty..", Toast.LENGTH_SHORT).show();
            return;
        }
        if (fileUris == null) return;
        if (fileUris.size() == 0) return;
        uploadedImages = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);

        try {
            String title = inputTitle.getText().toString().trim();
            String category = controller.getCategoryTranslation(this.category.getSelectedItem().toString());
            double price = Double.parseDouble(inputPrice.getText().toString().trim()); /*Fix double */
            String description = inputDescription.getText().toString().trim();

            for (Uri file : fileUris)
            {
                final StorageReference ref = storageReference.child("images/" + file.getLastPathSegment());
                uploadTask = ref.putFile(file);
                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();

                        assert downloadUrl != null;
                        uploadedImages.add(downloadUrl.toString());

                        product = new Product(product.getId(), product.getUuid(), product.getSeller(), title,
                                category, price, product.getCurrency(), description,
                                uploadedImages, product.getRating(),product.getFavStatus(),
                                product.getProdID(), product.getStore(), product.getTrending());

                        Map<String, Object> productValues = product.toMap();

                        progressBar.setVisibility(View.GONE);
                        // adding a map to our database.
                        firebaseApp.getFirebaseDB()
                                .getReference("ProductDB")
                                .child("products")
                                .child(product.getProdID()).updateChildren(productValues);
                    }
                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    uploadedImages.add(file.toString());
                    product = new Product(product.getId(), product.getUuid(), product.getSeller(), title,
                            category, price, product.getCurrency(), description,
                            uploadedImages, product.getRating(),product.getFavStatus(),
                            product.getProdID(), product.getStore(), product.getTrending());

                    Map<String, Object> productValues = product.toMap();
                    firebaseApp.getFirebaseDB()
                            .getReference()
                            .child("ProductDB")
                            .child("products")
                            .child(product.getProdID()).updateChildren(productValues);

                });
            }
            Toast.makeText(requireActivity(), "Updated product..", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean fieldCheck() {
        return inputTitle.getText().toString().isEmpty()
                || inputPrice.getText().toString().isEmpty()
                || inputDescription.getText().toString().isEmpty();
    }

    private void setViewPager(FragmentCallback callback, List<String> targetList) {
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(requireContext(),
                ProductFragment.this, targetList, callback);
        viewPager.setAdapter(imagePagerAdapter);
        tabLayout.setupWithViewPager(viewPager, true);
    }

    private void setViewPager(FragmentCallback callback) {
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