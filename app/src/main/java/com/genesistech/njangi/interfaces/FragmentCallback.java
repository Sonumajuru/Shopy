package com.genesistech.njangi.interfaces;
public interface FragmentCallback {
      void doSomething();
      void onItemClicked(int position, Object object);
      void onItemClicked(int position, Object object, int id);
}