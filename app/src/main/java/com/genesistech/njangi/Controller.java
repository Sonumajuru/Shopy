package com.genesistech.njangi;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Insets;
import android.os.Build;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.genesistech.njangi.R;
import com.genesistech.njangi.helper.DeviceType;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Controller {

    private View notificationsBadge;
    private BottomNavigationMenuView bottomNavigationMenuView;
    private static Context mContext;
    private static volatile Controller instance;
    private int badgeCount;
    private BottomNavigationView navView;
    private boolean IsFragVisible;

    public View getNotificationsBadge() {
        return notificationsBadge;
    }

    public void setNotificationsBadge(View notificationsBadge) {
        this.notificationsBadge = notificationsBadge;
    }

    public BottomNavigationMenuView getBottomNavigationMenuView() {
        return bottomNavigationMenuView;
    }

    public void setBottomNavigationMenuView(BottomNavigationMenuView bottomNavigationMenuView) {
        this.bottomNavigationMenuView = bottomNavigationMenuView;
    }

    public int getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
    }

    public BottomNavigationView getNavView() {
        return navView;
    }

    public void setNavView(BottomNavigationView navView) {
        this.navView = navView;
    }

    public Context getContext() {
        return mContext;
    }

    public static Controller getInstance(Context context){
        if(instance==null){
            synchronized(Controller.class){
                if(instance==null){
                    instance = new Controller(context);
                    mContext = context;
                }
            }
        }
        return instance;
    }

    public Controller(Context context){
        mContext = context;
        notificationsBadge = LayoutInflater.from(mContext).inflate(R.layout.custom_badge_layout,
                bottomNavigationMenuView,false);
    }

    private void getBadge() {
        if (getBottomNavigationMenuView() != null) {
            return;
        }
        setBottomNavigationMenuView((BottomNavigationMenuView) getNavView().getChildAt(0));
        setNotificationsBadge(LayoutInflater.from(getContext()).inflate(R.layout.custom_badge_layout, getBottomNavigationMenuView(),false));
    }

    public void addBadge(int count) {
        getNavView().removeView(getNotificationsBadge());
        getBadge();
        TextView notifications_badge = getNotificationsBadge().findViewById(R.id.notifications_badge);
        notifications_badge.setText(String.valueOf(count));
        setBadgeCount(count);
        getNavView().addView(getNotificationsBadge());
    }

    public void removeBadge() {
        getNavView().removeView(getNotificationsBadge());
    }

    public String getTranslation(String text) {
        switch (text) {
            case "Books":
                text = mContext.getString(R.string.books);
                break;
            case "Cosmetics":
                text = mContext.getString(R.string.cosmetics);
                break;
            case "Clothing":
                text = mContext.getString(R.string.clothing);
                break;
            case "Shoes":
                text = mContext.getString(R.string.shoes);
                break;
            case "Hairs":
                text = mContext.getString(R.string.hairs);
                break;
            case "Motorbike":
                text = mContext.getString(R.string.motorbike);
                break;
            case "Computers":
                text = mContext.getString(R.string.computer);
                break;
            case "Cars":
                text = mContext.getString(R.string.cars);
                break;
            case "Electronics":
                text = mContext.getString(R.string.electronics);
                break;
            case "Games":
                text = mContext.getString(R.string.games);
                break;
            case "Home Appliances":
                text = mContext.getString(R.string.home_appliance);
                break;
            case "Accessories":
                text = mContext.getString(R.string.accessories);
                break;
            case "Baby and Toys":
                text = mContext.getString(R.string.babyandtoys);
                break;
            case "Phones":
                text = mContext.getString(R.string.phones);
                break;
        }
        return text;
    }

    public String getCategoryTranslation(String text) {
        switch (text) {
            case "Livres":
            case "Books":
                text = "Books";
                break;
            case "Produits de beauté":
            case "Cosmetics":
                text = "Cosmetics";
                break;
            case "Vêtements":
            case "Clothing":
                text = "Clothing";
                break;
            case "Accessoires":
            case "Accessories":
                text = "Accessories";
                break;
            case "Bébé et Jouets":
            case "Baby and Toys":
                text = "Baby and Toys";
                break;
            case "Chaussures":
            case "Shoes":
                text = "Shoes";
                break;
            case "Mèches":
            case "Hairs":
                text = "Hairs";
                break;
            case "Moto":
            case "Motorbike":
                text = "Motorbike";
                break;
            case "Ordinateur":
            case "Computers":
                text = "Computers";
                break;
            case "Voitures":
            case "Cars":
                text = "Cars";
                break;
            case "Électronique":
            case "Electronics":
                text = "Electronics";
                break;
            case "Jeux":
            case "Games":
                text = "Games";
                break;
            case "Appareils Ménagers":
            case "Home Appliances":
                text = "Home Appliances";
                break;
            case "Téléphone":
            case "Phones":
                text = "Phones";
                break;
        }
        return text;
    }

    public String translate(Locale locale, int resId) {
        Configuration config = new Configuration(mContext.getResources().getConfiguration());
        config.setLocale(locale);
        return (String) mContext.createConfigurationContext(config).getText(resId);
    }

    public void setTextLength(TextView title) {
        int maxLength = 15;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        title.setFilters(fArray);
    }

    public String getDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate currentdate = LocalDate.now();
        return formatter.format(currentdate);
    }

    public boolean getIsFragVisible()
    {
        return IsFragVisible;
    }

    public static DeviceType getDeviceType() {

        float widthInches;
        float heightInches;
        double diagonalInches;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = ((Activity)mContext).getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            widthInches = windowMetrics.getBounds().width() - insets.left - insets.right;
            heightInches = windowMetrics.getBounds().height() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            widthInches = displayMetrics.widthPixels / displayMetrics.xdpi;
            heightInches = displayMetrics.heightPixels / displayMetrics.ydpi;
        }
        diagonalInches = Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));
        if (diagonalInches >= 7.0) {
            return DeviceType.Tablet;
        }
        else {
            return DeviceType.Phone;
        }
    }

    public void setIsFragVisible(boolean visible) {
        IsFragVisible = visible;
    }
}