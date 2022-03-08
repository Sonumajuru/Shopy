package com.genesistech.njangi;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
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

    private void getBadge()
    {
        if (getBottomNavigationMenuView() != null) {
            return;
        }
        setBottomNavigationMenuView((BottomNavigationMenuView) getNavView().getChildAt(0));
        setNotificationsBadge(LayoutInflater.from(getContext()).inflate(R.layout.custom_badge_layout, getBottomNavigationMenuView(),false));
    }

    public void addBadge(int count)
    {
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

    public String getTranslation(String text)
    {
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

    public String getCategoryTranslation(String text)
    {
        switch (text) {
            case "Livres":
                text = "Books";
                break;
            case "Produits de beauté":
                text = "Cosmetics";
                break;
            case "Vêtements":
                text = "Clothing";
                break;
            case "Accessoires":
                text = "Accessories";
                break;
            case "Bébé et Jouets":
                text = "Baby and Toys";
                break;
            case "Chaussures":
                text = "Shoes";
                break;
            case "Mèches":
                text = "Hairs";
                break;
            case "Moto":
                text = "Motorbike";
                break;
            case "Ordinateur":
                text = "Computers";
                break;
            case "Voitures":
                text = "Cars";
                break;
            case "Électronique":
                text = "Electronics";
                break;
            case "Jeux":
                text = "Games";
                break;
            case "Appareils Ménagers":
                text = "Home Appliances";
                break;
            case "Téléphone":
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

    public void setTextLength(TextView title)
    {
        int maxLength = 15;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        title.setFilters(fArray);
    }

    public String getDate()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate currentdate = LocalDate.now();
        return formatter.format(currentdate);
    }

    public boolean getIsFragVisible()
    {
        return IsFragVisible;
    }

    public DeviceType getDeviceType() {
        Display display = ((Activity)   mContext).getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        float widthInches = metrics.widthPixels / metrics.xdpi;
        float heightInches = metrics.heightPixels / metrics.ydpi;
        double diagonalInches = Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));
        if (diagonalInches >= 7.0) {
            return DeviceType.Tablet;
        }
        else
        {
            return DeviceType.Phone;
        }
    }

    public void setIsFragVisible(boolean visible) {
        IsFragVisible = visible;
    }
}
