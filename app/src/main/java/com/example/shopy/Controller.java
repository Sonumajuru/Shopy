package com.example.shopy;

import android.content.Context;
import android.content.res.Configuration;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class Controller {

    private FirebaseAuth mAuth;
    private View notificationsBadge;
    private BottomNavigationMenuView bottomNavigationMenuView;
    private static Context mContext;
    private static volatile Controller instance;
    private int badgeCount;
    private BottomNavigationView navView;

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void setmAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

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
            case "Clothing":
                text = mContext.getString(R.string.clothing);
                break;
            case "Computers":
                text = mContext.getString(R.string.computer);
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
            case "Phones":
                text = mContext.getString(R.string.phones);
                break;
        }
        return text;
    }

    public String translate(Locale locale, int resId) {
        Configuration config = new Configuration(mContext.getResources().getConfiguration());
        config.setLocale(locale);
        return (String) mContext.createConfigurationContext(config).getText(resId);
    }

    public String getDefaultTranslation(String text)
    {
        switch (text) {
            case "Livres":
                text = "Books";
                break;
            case "Vêtements":
                text = "Clothing";
                break;
            case "Ordinateur":
                text = "Computers";
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

    public void setTextLength(TextView title)
    {
        int maxLength = 15;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        title.setFilters(fArray);
    }
}
