package com.example.shopy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

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
}
