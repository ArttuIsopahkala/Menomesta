package com.ardeapps.menomesta.services;

import com.ardeapps.menomesta.fragments.WriteCommentFragment;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.Comment;
import com.ardeapps.menomesta.objects.Event;
import com.ardeapps.menomesta.objects.User;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Arttu on 18.6.2017.
 */

public class FragmentListeners {

    public final static int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static FragmentListeners instance;
    private PermissionHandledListener permissionHandledListener;
    private FragmentRefreshListener fragmentRefreshListener;
    private FragmentChangeListener fragmentChangeListener;
    private PageAdapterRefreshListener pageAdapterRefreshListener;

    public static FragmentListeners getInstance() {
        if(instance == null) {
            instance = new FragmentListeners();
        }
        return instance;
    }

    public PageAdapterRefreshListener getPageAdapterRefreshListener() {
        return pageAdapterRefreshListener;
    }

    public void setPageAdapterRefreshListener(PageAdapterRefreshListener pageAdapterRefreshListener) {
        this.pageAdapterRefreshListener = pageAdapterRefreshListener;
    }

    public FragmentChangeListener getFragmentChangeListener() {
        return fragmentChangeListener;
    }

    public void setFragmentChangeListener(FragmentChangeListener fragmentChangeListener) {
        this.fragmentChangeListener = fragmentChangeListener;
    }

    public PermissionHandledListener getPermissionHandledListener() {
        return permissionHandledListener;
    }

    public void setPermissionHandledListener(PermissionHandledListener permissionHandledListener) {
        this.permissionHandledListener = permissionHandledListener;
    }

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    public interface PageAdapterRefreshListener {
        void refreshEventsFragment();

        void refreshBarsFragment();

        void refreshChatFragment();
    }

    public interface FragmentChangeListener {
        void goToWelcomeFragment();

        void goToLoaderFragment();

        void goToReplyFragment(Comment comment);

        void goToBarDetailsFragment(Bar bar);

        void goToAddEventFragment(Event event);

        void goToShowLocationFragment(LatLng position, String address);

        void goToPrivateFragment(User recipient, String sessionId, ArrayList<Comment> messages);

        void goToProfileFragment();

        void goToStatisticsFragment();

        void goToCompanyFragment();

        void goToInfoFragment();

        void goToMapFragment(Bar bar);

        void goToBarRequestFragment();

        void goToEditBarFragment(Bar bar);

        void goToWriteCommentFragment(WriteCommentFragment.Listener listener);
    }

    public interface PermissionHandledListener {
        void onPermissionGranted(int MY_PERMISSION);
        void onPermissionDenied(int MY_PERMISSION);
    }

    public interface FragmentRefreshListener {
        void onRefreshBarChat();
        void onRefreshStats();
        void onRefreshDrinks();
        void onRefreshBarDetails();
        void onRefreshBar();
    }
}
