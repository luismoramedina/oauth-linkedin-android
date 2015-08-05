package com.findmeapps.findme.service;

import android.util.Log;

import com.findmeapps.findme.R;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Person;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;

/*  Api key: check secret.xml
 *
 *    Secret: check secret.xml
 *
 *    User OAuth credential example:
 *        65001a82-a269-4767-83ed-blabla
 *
 *    User OAuth credential secret example:
 *        5073828e-22ec-4b3b-a0d7-blablalba-Do not share!!!
 *
 * User: moral<br>
 * Date: 24/12/12<br>
 * Time: 0:21<br>
 */
public class LinkedInService {
    /**
     * Credentials (Key) provided to findme by linked in
     */
    private String consumerKey;
    /**
     * Credentials (Pass) provided to findme by linked in
     */
    private String consumerSecret;

    //CALLBACK_URL + /?oauth_token=4f7bc304-a616-43ff-8573-a2b09fd86f88&oauth_verifier=27091
    public static final String OAUTH_VERIFIER_PARAM = "oauth_verifier";
    public static final String CALLBACK_URL = "oauth://findme";
    public static final String KEY_AUTHORIZATION_URL = "authorizationUrl";

    //TODO make service singletons
    private LinkedInOAuthService linkedInOAuthService;
    private LinkedInRequestToken linkedInRequestToken;

    private LinkedInApiClient apiClient;
    private LinkedInAccessToken linkedInAccessToken;
    private String authorizationUrl;

    public LinkedInService() {
    }

    public LinkedInService(String consumerKey, String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    private LinkedInOAuthService createLinkedInOauthService() {
        if(linkedInOAuthService == null) {
            linkedInOAuthService = LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(consumerKey, consumerSecret);
        }
        return linkedInOAuthService;
    }

    private LinkedInApiClient createApiClient(LinkedInAccessToken accessToken, String consumerKey, String consumerSecret) {
        if(apiClient == null) {
            System.out.println("token = " + accessToken.getToken());
            System.out.println("tokenSecret = " + accessToken.getTokenSecret());

            System.out.println("consumerKey = " + consumerKey);
            System.out.println("consumerSecret = " + consumerSecret);
            System.out.println("accessToken = " + accessToken);

            final LinkedInApiClientFactory factory = LinkedInApiClientFactory.newInstance(consumerKey, consumerSecret);
            apiClient = factory.createLinkedInApiClient(accessToken);
        }
        return apiClient;
    }


    public Person getProfile() {
        Person profileWithUrl;
        createApiClient(linkedInAccessToken, consumerKey, consumerSecret);

        Person profileForCurrentUser = apiClient.getProfileForCurrentUser(EnumSet.of(ProfileField.ID));
//                    Person profileById = apiClient.getProfileById(profileForCurrentUser.getId());
//	   		        printPerson(profileById);
        ProfileField[] values = ProfileField.values();
        HashSet<ProfileField> profileFields = new HashSet<ProfileField>(Arrays.asList(values));

        profileWithUrl = apiClient.getProfileById(profileForCurrentUser.getId(), profileFields);

        return profileWithUrl;
    }

    /**
     * NEEDS network access
     * @return
     */
    public String getOAuthUrl() {
        createLinkedInOauthService();

        try {
            //LinkedInAccessToken must be get from li auth page
            linkedInRequestToken = linkedInOAuthService.getOAuthRequestToken(CALLBACK_URL);
            authorizationUrl = linkedInRequestToken.getAuthorizationUrl();

        } catch (Exception e) {
            Log.e("FINDME", "Error getting user info: " + e.getMessage(), e);
        }

        return authorizationUrl;
    }

    public LinkedInAccessToken getAccessTokenWithPinCode(String pinCode) {
        linkedInAccessToken = linkedInOAuthService.getOAuthAccessToken(linkedInRequestToken, pinCode);
        return linkedInAccessToken;
    }

    public void setLinkedInAccessToken(LinkedInAccessToken linkedInAccessToken) {
        this.linkedInAccessToken = linkedInAccessToken;
    }

    public void setLinkedInAccessToken(String token, String tokenSecret) {
        setLinkedInAccessToken(new LinkedInAccessToken(token, tokenSecret));
    }

    public String getAuthorizationUrl() {
        return authorizationUrl;
    }
}
