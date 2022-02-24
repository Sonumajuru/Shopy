package com.example.shopy.interfaces;

import android.view.MenuItem;

public interface FragmentCallback {

      void doSomething();
      void onItemClicked(int position, Object object);
      void onItemClicked(int position, Object object, int id);
}