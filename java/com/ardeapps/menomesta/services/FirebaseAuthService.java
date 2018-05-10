package com.ardeapps.menomesta.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.FbRes;
import com.ardeapps.menomesta.PrefRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.handlers.EditSuccessListener;
import com.ardeapps.menomesta.handlers.FirebaseLoginHandler;
import com.ardeapps.menomesta.handlers.GetUserHandler;
import com.ardeapps.menomesta.objects.User;
import com.ardeapps.menomesta.resources.UsersResource;
import com.ardeapps.menomesta.utils.Logger;
import com.ardeapps.menomesta.utils.StringUtils;
import com.ardeapps.menomesta.views.Loader;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import static com.ardeapps.menomesta.PrefRes.EMAIL;
import static com.ardeapps.menomesta.PrefRes.TOKEN;

/**
 * Created by Arttu on 4.5.2017.
 */
public class FirebaseAuthService {
    private static final String FACEBOOK = "facebook.com";
    private static final String PASSWORD = "password";

    private static FirebaseAuthService instance;

    public static FirebaseAuthService getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthService();
        }
        return instance;
    }

    private static void onNetworkError() {
        if (Loader.isVisible()) {
            Loader.hide();
        }
        Logger.toast(R.string.error_network);
    }

    private static void onUserNotFoundError() {
        if (Loader.isVisible()) {
            Loader.hide();
        }
        Logger.toast(R.string.error_profile);
    }

    private static void onAuthenticationError() {
        if (Loader.isVisible()) {
            Loader.hide();
        }
        Logger.toast(R.string.error_authentication);
    }

    private static void logAction() {
        int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
        String callingMethod = Thread.currentThread().getStackTrace()[3].getMethodName();
        Logger.log(FirebaseAuthService.class.getSimpleName() + ":" + lineNumber + " - " + callingMethod);
    }

    private static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppRes.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * LOGIN TO FIREBASE DATABASE
     */
    public void logInToFirebase(final FirebaseLoginHandler handler) {
        logAction();
        if (isNetworkAvailable()) {
            Loader.show();
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithCustomToken(PrefRes.getString(TOKEN)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(final @NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        final FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Logger.log(AppRes.getContext().getString(R.string.log_login_success_token_valid, user.getUid()));
                            handler.onFirebaseLoginSuccess(user.getUid());
                        } else {
                            onUserNotFoundError();
                        }
                    } else {
                        // Token expired
                        if (FbRes.isUserLoggedIn()) {
                            loginWithFacebook(new LoginWithFacebookHandler() {
                                @Override
                                public void onLoginWithFacebookSuccess() {
                                    final FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        saveIdToken(user, new GetIdTokenHandler() {
                                            @Override
                                            public void onGetIdTokenSuccess() {
                                                Loader.hide();
                                                Logger.log(AppRes.getContext().getString(R.string.log_login_success_facebook, user.getUid()));
                                                handler.onFirebaseLoginSuccess(user.getUid());
                                            }
                                        });
                                    } else {
                                        onUserNotFoundError();
                                    }
                                }

                                @Override
                                public void onLoginWithFacebookFailed() {
                                    onUserNotFoundError();
                                }
                            });
                        } else {
                            loginWithEmailPassword(new LoginToFirebaseHandler() {
                                @Override
                                public void onLoginToFirebaseSuccess() {
                                    Loader.hide();
                                    final FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        handler.onFirebaseLoginSuccess(user.getUid());
                                    } else {
                                        onUserNotFoundError();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        } else onNetworkError();
    }

    /**
     * REGISTER USER AUTHENTICATION
     */

    public interface RegisterToFirebaseHandler {
        void registerToFirebaseSuccess(String userId);
    }

    public void registerToFirebase(final RegisterToFirebaseHandler handler) {
        logAction();
        if (isNetworkAvailable()) {
            Loader.show();
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            // Shared preferences poistettu. Jos käyttäjä kirjautuu uudelleen Facebookilla, ei luoda uutta auth käyttäjää.
            if (FbRes.isUserLoggedIn()) {
                loginWithFacebook(new LoginWithFacebookHandler() {
                    @Override
                    public void onLoginWithFacebookSuccess() {
                        final FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Jos käyttäjä ei ole linkitetty Email/Password, linkitetään se tässä
                            if (StringUtils.isEmptyString(user.getEmail()) || !user.getEmail().contains("@menomesta.fi")) {
                                linkWithEmailPassword(user, new LinkWithCredentialsHandler() {
                                    @Override
                                    public void onLinkWithCredentialsSuccess() {
                                        Loader.hide();
                                        handler.registerToFirebaseSuccess(user.getUid());
                                    }
                                });
                            } else {
                                Loader.hide();
                                handler.registerToFirebaseSuccess(user.getUid());
                            }
                        } else {
                            onUserNotFoundError();
                        }
                    }

                    @Override
                    public void onLoginWithFacebookFailed() {
                        loginAnonymously(new LoginToFirebaseHandler() {
                            @Override
                            public void onLoginToFirebaseSuccess() {
                                Loader.hide();
                                final FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    handler.registerToFirebaseSuccess(user.getUid());
                                } else {
                                    onUserNotFoundError();
                                }
                            }
                        });
                    }
                });
            } else {
                loginAnonymously(new LoginToFirebaseHandler() {
                    @Override
                    public void onLoginToFirebaseSuccess() {
                        Loader.hide();
                        final FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            handler.registerToFirebaseSuccess(user.getUid());
                        } else {
                            onUserNotFoundError();
                        }
                    }
                });
            }
        } else onNetworkError();
    }

    /**
     * THIS METHODS ARE FOR LOGIN IN TO FIREBASE DATABASE
     */
    protected interface LoginToFirebaseHandler {
        void onLoginToFirebaseSuccess();
    }

    private void loginAnonymously(final LoginToFirebaseHandler handler) {
        logAction();
        // Sing in anonymous to get token and userId
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                final FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    linkWithEmailPassword(user, new LinkWithCredentialsHandler() {
                        @Override
                        public void onLinkWithCredentialsSuccess() {
                            Logger.log(AppRes.getContext().getString(R.string.log_register_success, PrefRes.getString(EMAIL), PrefRes.getString(PASSWORD)));
                            if (FbRes.isUserLoggedIn()) {
                                linkWithFacebook(user, new LinkWithCredentialsHandler() {
                                    @Override
                                    public void onLinkWithCredentialsSuccess() {
                                        handler.onLoginToFirebaseSuccess();
                                    }
                                });
                            } else {
                                handler.onLoginToFirebaseSuccess();
                            }
                        }
                    });
                } else {
                    onUserNotFoundError();
                }
            }
        });
    }

    private void loginWithEmailPassword(final LoginToFirebaseHandler handler) {
        logAction();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (!StringUtils.isEmptyString(PrefRes.getString(EMAIL)) && !StringUtils.isEmptyString(PrefRes.getString(PASSWORD))) {
            mAuth.signInWithEmailAndPassword(PrefRes.getString(EMAIL), PrefRes.getString(PASSWORD)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        final FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null) {
                            saveIdToken(user, new GetIdTokenHandler() {
                                @Override
                                public void onGetIdTokenSuccess() {
                                    Logger.log(AppRes.getContext().getString(R.string.log_login_success_email, user.getUid()));
                                    handler.onLoginToFirebaseSuccess();
                                }
                            });
                        } else {
                            onUserNotFoundError();
                        }
                    } else {
                        onAuthenticationError();
                    }
                }
            });
        } else {
            // Facebookilla kirjauduttu ulos, poistetaan Anonymous käyttäjä
            deleteAccount(new DeleteAccountHandler() {
                @Override
                public void onDeleteAccountSuccess() {
                    Loader.hide();
                    FragmentListeners.getInstance().getFragmentChangeListener().goToWelcomeFragment();
                }
            });
        }
    }

    public interface LoginWithFacebookHandler {
        void onLoginWithFacebookSuccess();

        void onLoginWithFacebookFailed();
    }

    private void loginWithFacebook(final LoginWithFacebookHandler handler) {
        logAction();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = FacebookAuthProvider.getCredential(AccessToken.getCurrentAccessToken().getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        handler.onLoginWithFacebookSuccess();
                    } else {
                        handler.onLoginWithFacebookFailed();
                    }
                } else {
                    handler.onLoginWithFacebookFailed();
                }
            }
        });
    }

    /**
     * THIS METHODS ARE FOR LINKING USER WITH CREDENTIALS(EMAIL OR FACEBOOK) TO FIREBASE DATABASE
     */
    private interface LinkWithCredentialsHandler {
        void onLinkWithCredentialsSuccess();
    }

    // Linkitetään Facebook -> tallennetaan TOKEN -> success
    private void linkWithFacebook(final FirebaseUser user, final LinkWithCredentialsHandler handler) {
        logAction();
        AuthCredential credential = FacebookAuthProvider.getCredential(AccessToken.getCurrentAccessToken().getToken());
        user.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    saveIdToken(user, new GetIdTokenHandler() {
                        @Override
                        public void onGetIdTokenSuccess() {
                            handler.onLinkWithCredentialsSuccess();
                        }
                    });
                } else {
                    // Käyttäjän Facebook on linkitetty toiseen auth tunnukseen.
                    // 1. Kirjaudutaan vanhaan käyttäjään
                    loginWithFacebook(new LoginWithFacebookHandler() {
                        @Override
                        public void onLoginWithFacebookSuccess() {
                            // 2. Poistetaan vanha käyttäjä
                            deleteAccount(new DeleteAccountHandler() {
                                @Override
                                public void onDeleteAccountSuccess() {
                                    // 3. Kirjaudutaan uuteen käyttäjään
                                    loginWithEmailPassword(new LoginToFirebaseHandler() {
                                        @Override
                                        public void onLoginToFirebaseSuccess() {
                                            // 4. Linkitetään Facebook
                                            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                            AuthCredential credential = FacebookAuthProvider.getCredential(AccessToken.getCurrentAccessToken().getToken());
                                            if(currentUser != null) {
                                                currentUser.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            saveIdToken(currentUser, new GetIdTokenHandler() {
                                                                @Override
                                                                public void onGetIdTokenSuccess() {
                                                                    handler.onLinkWithCredentialsSuccess();
                                                                }
                                                            });
                                                        } else {
                                                            onAuthenticationError();
                                                        }
                                                    }
                                                });
                                            } else {
                                                onUserNotFoundError();
                                            }
                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onLoginWithFacebookFailed() {
                            onUserNotFoundError();
                            LoginManager.getInstance().logOut();
                        }
                    });
                }
            }
        });
    }

    private static void linkWithEmailPassword(final FirebaseUser user, final LinkWithCredentialsHandler handler) {
        logAction();
        final String email = user.getUid() + "@menomesta.fi";
        final String password = user.getUid();
        // Tallennetaan email ja password, jota käytetään uudelleen kirjautumisessa
        PrefRes.putString(EMAIL, email);
        PrefRes.putString(PASSWORD, password);
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        user.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    saveIdToken(user, new GetIdTokenHandler() {
                        @Override
                        public void onGetIdTokenSuccess() {
                            handler.onLinkWithCredentialsSuccess();
                        }
                    });
                } else {
                    onAuthenticationError();
                }
            }
        });
    }

    /**
     * THIS METHOD SAVES TOKEN FOR LOGGING IN AGAIN WITHOUT ASKING
     */
    private interface GetIdTokenHandler {
        void onGetIdTokenSuccess();
    }

    private static void saveIdToken(FirebaseUser user, final GetIdTokenHandler handler) {
        logAction();
        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    // Tallennetaan token, jota käytetään uudelleen kirjautumisessa
                    String token = task.getResult().getToken();
                    PrefRes.putString(TOKEN, token);
                    handler.onGetIdTokenSuccess();
                } else {
                    PrefRes.putString(TOKEN, "");
                    onAuthenticationError();
                }
            }
        });
    }

    /**
     * WHEN SHARED PREFERENCES IS CLEARED, CALL THIS
     */
    public interface RestoreCredentialsHandler {
        void onRestoreCredentialsWithFacebookSuccess();

        void onRestoreCredentialsWithFacebookFailed();
    }

    public static void restoreCredentialsWithFacebook(final RestoreCredentialsHandler handler) {
        logAction();
        Loader.show();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = FacebookAuthProvider.getCredential(AccessToken.getCurrentAccessToken().getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(final @NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        UsersResource.getInstance().getUser(firebaseUser.getUid(), new GetUserHandler() {
                            @Override
                            public void onUserLoaded(final User user) {
                                UsersResource.getInstance().editUser(user, new EditSuccessListener() {
                                    @Override
                                    public void onEditSuccess() {
                                        // Tallennetaan email ja password uudelleen lokaalisti
                                        final String email = user.userId + "@menomesta.fi";
                                        final String password = user.userId;
                                        // Tallennetaan email ja password, jota käytetään uudelleen kirjautumisessa
                                        PrefRes.putString(EMAIL, email);
                                        PrefRes.putString(PASSWORD, password);

                                        saveIdToken(firebaseUser, new GetIdTokenHandler() {
                                            @Override
                                            public void onGetIdTokenSuccess() {
                                                Loader.hide();
                                                handler.onRestoreCredentialsWithFacebookSuccess();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    } else {
                        onUserNotFoundError();
                    }
                } else {
                    Loader.hide();
                    handler.onRestoreCredentialsWithFacebookFailed();
                }
            }
        });
    }

    /**
     * CALL THIS METHOD WHEN USER CLICKS FACEBOOK LOGIN BUTTON
     */
    public interface LinkFacebookIfNeededHandler {
        void onLinkFacebookIfNeededSuccess();
    }

    // User on aina != null, koska kirjauduttu jo password/email
    public void linkFacebookIfNeeded(final LinkFacebookIfNeededHandler handler) {
        logAction();
        Loader.show();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.getProviders() == null || !user.getProviders().contains(FACEBOOK)) {
                // linkitä facebook
                linkWithFacebook(user, new LinkWithCredentialsHandler() {
                    @Override
                    public void onLinkWithCredentialsSuccess() {
                        Loader.hide();
                        handler.onLinkFacebookIfNeededSuccess();
                    }
                });
            } else {
                Loader.hide();
                handler.onLinkFacebookIfNeededSuccess();
            }
        } else {
            onUserNotFoundError();
        }
    }

    /**
     * USE THIS METHOD FOR DELETE USER AUTHENTICATION
     */
    public interface DeleteAccountHandler {
        void onDeleteAccountSuccess();
    }

    public void deleteAccount(final DeleteAccountHandler handler) {
        logAction();
        Loader.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Loader.hide();
                    handler.onDeleteAccountSuccess();
                }
            });
        } else {
            onUserNotFoundError();
            Loader.hide();
            handler.onDeleteAccountSuccess();
        }
    }

    public interface UnlinkFacebookHandler {
        void onUnlinkFacebookSuccess();
    }

    public void unlinkFacebook(final UnlinkFacebookHandler handler) {
        logAction();
        Loader.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.unlink(FACEBOOK).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        loginWithEmailPassword(new LoginToFirebaseHandler() {
                            @Override
                            public void onLoginToFirebaseSuccess() {
                                Loader.hide();
                                handler.onUnlinkFacebookSuccess();
                            }
                        });
                    } else {
                        onNetworkError();
                    }
                }
            });
        } else {
            onUserNotFoundError();
        }
    }
}
