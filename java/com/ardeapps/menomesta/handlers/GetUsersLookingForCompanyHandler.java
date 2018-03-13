package com.ardeapps.menomesta.handlers;

import com.ardeapps.menomesta.objects.CompanyMessage;

import java.util.ArrayList;

/**
 * Created by Arttu on 12.10.2016.
 */
public interface GetUsersLookingForCompanyHandler {
    void onUsersLookingForCompanyLoaded(ArrayList<CompanyMessage> usersLookingForCompany);
}
