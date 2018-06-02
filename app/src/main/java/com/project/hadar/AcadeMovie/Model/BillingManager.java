package com.project.hadar.AcadeMovie.Model;

/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.util.Log;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles all the interactions with Play Store (via Billing library), maintains connection to
 * it through BillingClient and caches temporary states/data if needed
 */

public class BillingManager implements PurchasesUpdatedListener
{
    // Default value of mBillingClientResponseCode until BillingManager was not yet initialized
    private static final String TAG = "BillingManager";
    private BillingClient m_BillingClient;
    private boolean m_IsServiceConnected;
    private final BillingUpdatesListener m_BillingUpdatesListener;
    private final Activity m_Activity;
    private final List<Purchase> m_Purchases = new ArrayList<>();
    private Set<String> m_TokensToBeConsumed;

    /* BASE_64_ENCODED_PUBLIC_KEY should be YOUR APPLICATION'S PUBLIC KEY
     * (that you got from the Google Play developer console). This is not your
     * developer public key, it's the *app-specific* public key.
     *
     * Instead of just storing the entire literal string here embedded in the
     * program,  construct the key at runtime from pieces or
     * use bit manipulation (for example, XOR with some other string) to hide
     * the actual key.  The key itself is not secret information, but we don't
     * want to make it easy for an attacker to replace the public key with one
     * of their own and then fake messages from the server.
     */

    private static final String BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlk8CoPqdnwpRmDDD9yq0UCBWcZvgrNZ9HCDMsDBfX2MihZMRRB06ehOUzIXoNtK244R+31AV0BC7zcJnInRvZ2Qd3iOk0u1ogiI83INTVZVJVPfmFS1X2R0Hz3aosRkKxQ1+I/yNLr98HB/3atMGhAvo6B7OXpVgrCMStyMZXWJ4OBMQrvsVhkp+oEK3je7WM2iD766BItKhuyqO18fouQ5qzjhoLv427DRACHQ+KyJ0HQG87Ex1CxbUsXtlY5EmyCDSqE+yFJjZHtgxQs1bYGXHZg7cIO+XN/AEJEV55XxM1gNL3/r+Ytp0HtmBm0uMz1Jbskp9JaiuB7wFkfA7dQIDAQAB";

    /**
     * Listener to the updates that happen when purchases list was updated or consumption of the
     * item was finished
     */

    public interface BillingUpdatesListener
    {
        void onBillingClientSetupFinished();
        void onConsumeFinished(String token, @BillingResponse int result);
        void onPurchasesUpdated(int resultCode,List<Purchase> purchases);
    }

    /**
     * Listener for the Billing client state to become connected
     */
    public interface ServiceConnectedListener
    {
        void onServiceConnected(@BillingResponse int resultCode);
    }

    public BillingManager(Activity activity, final BillingUpdatesListener updatesListener)
    {
        Log.d(TAG, "Creating Billing client.");
        m_Activity = activity;
        m_BillingUpdatesListener = updatesListener;
        m_BillingClient = BillingClient.newBuilder(m_Activity).setListener(this).build();

        Log.d(TAG, "Starting setup.");

        // Start setup. This is asynchronous and the specified listener will be called
        // once setup completes.
        // It also starts to report all the new purchases through onPurchasesUpdated() callback.
        startServiceConnection(new Runnable() {
            @Override
            public void run() {
                // Notifying the listener that billing client is ready
                m_BillingUpdatesListener.onBillingClientSetupFinished();
                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
            }
        });
    }

    /**
     * Handle a callback that purchases were updated from the Billing library
     */
    @Override
    public void onPurchasesUpdated(int resultCode, List<Purchase> purchases)
    {
        if (resultCode == BillingResponse.OK)
        {
            for (Purchase purchase : purchases)
            {
                handlePurchase(purchase);
            }
        }
        else if (resultCode == BillingResponse.USER_CANCELED)
        {
            Log.i(TAG, "onPurchasesUpdated() - user cancelled the purchase flow - skipping");
        }
        else
        {
            Log.w(TAG, "onPurchasesUpdated() got unknown resultCode: " + resultCode);
        }

        m_BillingUpdatesListener.onPurchasesUpdated(resultCode, m_Purchases);
    }

    /**
     * Start a purchase flow
     */
    public void initiatePurchaseFlow(final String skuId, final @SkuType String billingType)
    {
        initiatePurchaseFlow(skuId, null, billingType);
    }

    /**
     * Start a purchase or subscription replace flow
     */
    public void initiatePurchaseFlow(final String skuId, final ArrayList<String> oldSkus, final @SkuType String billingType) {
        Runnable purchaseFlowRequest = new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "Launching in-app purchase flow. Replace old SKU? " + (oldSkus != null));
                BillingFlowParams purchaseParams = BillingFlowParams.newBuilder()
                        .setSku(skuId).setType(billingType).setOldSkus(oldSkus).build();
                m_BillingClient.launchBillingFlow(m_Activity, purchaseParams);
            }
        };

        executeServiceRequest(purchaseFlowRequest);
    }


    /**
     * Clear the resources
     */

    public void destroy()
    {
        Log.d(TAG, "Destroying the manager.");

        if (m_BillingClient != null && m_BillingClient.isReady())
        {
            m_BillingClient.endConnection();
            m_BillingClient = null;
        }
    }

    public void consumeAsync(final String purchaseToken)
    {
        // If we've already scheduled to consume this token - no action is needed (this could happen
        // if you received the token when querying purchases inside onReceive() and later from
        // onActivityResult()
        if (m_TokensToBeConsumed == null)
        {
            m_TokensToBeConsumed = new HashSet<>();
        }
        else if (m_TokensToBeConsumed.contains(purchaseToken))
        {
            Log.i(TAG, "Token was already scheduled to be consumed - skipping...");
            return;
        }
        m_TokensToBeConsumed.add(purchaseToken);

        // Generating Consume Response listener
        final ConsumeResponseListener onConsumeListener = new ConsumeResponseListener()
        {
            @Override
            public void onConsumeResponse(@BillingResponse int responseCode, String purchaseToken)
            {
                // If billing service was disconnected, we try to reconnect 1 time
                // (feel free to introduce your retry policy here).
                m_BillingUpdatesListener.onConsumeFinished(purchaseToken, responseCode);
            }
        };

        // Creating a runnable from the request to use it inside our connection retry policy below
        Runnable consumeRequest = new Runnable()
        {
            @Override
            public void run()
            {
                // Consume the purchase async
                m_BillingClient.consumeAsync(purchaseToken, onConsumeListener);
            }
        };

        executeServiceRequest(consumeRequest);
    }


    /**
     * Handles the purchase
     * Note: Notice that for each purchase, we check if signature is valid on the client.
     * It's recommended to move this check into your backend.
     * @param purchase Purchase to be handled
     */

    private void handlePurchase(Purchase purchase)
    {
        if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature()))
        {
            Log.i(TAG, "Got a purchase: " + purchase + "; but signature is bad. Skipping...");
            return;
        }

        Log.d(TAG, "Got a verified purchase: " + purchase);

        m_Purchases.add(purchase);
    }

    private void startServiceConnection(final Runnable executeOnSuccess)
    {
        m_BillingClient.startConnection(new BillingClientStateListener()
        {
            @Override
            public void onBillingSetupFinished(@BillingResponse int billingResponseCode)
            {
                Log.e(TAG, "Setup finished. Response code: " + billingResponseCode);

                if (billingResponseCode == BillingResponse.OK)
                {
                    m_IsServiceConnected = true;
                    if (executeOnSuccess != null)
                    {
                        executeOnSuccess.run();
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                m_IsServiceConnected = false;
            }
        });
    }

    private void executeServiceRequest(Runnable runnable) {
        if (m_IsServiceConnected)
        {
            runnable.run();
        }
        else
        {
            // If billing service was disconnected, we try to reconnect 1 time.
            // (feel free to introduce your retry policy here).
            startServiceConnection(runnable);
        }
    }

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     * </p>
     */

    private boolean verifyValidSignature(String signedData, String signature)
    {
        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (BASE_64_ENCODED_PUBLIC_KEY.contains("CONSTRUCT_YOUR"))
        {
            throw new RuntimeException("Please update your app's public key at: "
                    + "BASE_64_ENCODED_PUBLIC_KEY");
        }

        try
        {
            return Security.verifyPurchase(BASE_64_ENCODED_PUBLIC_KEY, signedData, signature);
        }
        catch (IOException e)
        {
            Log.e(TAG, "Got an exception trying to validate a purchase: " + e);
            return false;
        }
    }
}