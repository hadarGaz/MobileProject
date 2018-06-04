package com.project.hadar.AcadeMovie.Model;

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

public class BillingManager implements PurchasesUpdatedListener
{
    private static final String TAG = "BillingManager";
    private BillingClient m_BillingClient;
    private boolean m_IsServiceConnected;
    private final BillingUpdatesListener m_BillingUpdatesListener;
    private final Activity m_Activity;
    private final List<Purchase> m_Purchases = new ArrayList<>();
    private Set<String> m_TokensToBeConsumed;
    private static final String BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlk8CoPqdnwpRmDDD9yq0UCBWcZvgrNZ9HCDMsDBfX2MihZMRRB06ehOUzIXoNtK244R+31AV0BC7zcJnInRvZ2Qd3iOk0u1ogiI83INTVZVJVPfmFS1X2R0Hz3aosRkKxQ1+I/yNLr98HB/3atMGhAvo6B7OXpVgrCMStyMZXWJ4OBMQrvsVhkp+oEK3je7WM2iD766BItKhuyqO18fouQ5qzjhoLv427DRACHQ+KyJ0HQG87Ex1CxbUsXtlY5EmyCDSqE+yFJjZHtgxQs1bYGXHZg7cIO+XN/AEJEV55XxM1gNL3/r+Ytp0HtmBm0uMz1Jbskp9JaiuB7wFkfA7dQIDAQAB";

    public interface BillingUpdatesListener
    {
        void onBillingClientSetupFinished();
        void onConsumeFinished(String token, @BillingResponse int result);
        void onPurchasesUpdated(int resultCode,List<Purchase> purchases);
    }

    public BillingClient getBillingClient()
    {
        return m_BillingClient;
    }

    public BillingManager(Activity activity, final BillingUpdatesListener updatesListener)
    {
        Log.d(TAG, "Creating Billing client.");
        m_Activity = activity;
        m_BillingUpdatesListener = updatesListener;
        m_BillingClient = BillingClient.newBuilder(m_Activity).setListener(this).build();

        Log.d(TAG, "Starting setup.");

        startServiceConnection(new Runnable()
        {
            @Override
            public void run()
            {
                m_BillingUpdatesListener.onBillingClientSetupFinished();
                Log.d(TAG, "Setup successful. Querying inventory.");
            }
        });
    }

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

    public void initiatePurchaseFlow(final String skuId, final @SkuType String billingType)
    {
        initiatePurchaseFlow(skuId, null, billingType);
    }

    @SuppressWarnings("all")
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

    public void consumeAsync(final String purchaseToken)
    {
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

        final ConsumeResponseListener onConsumeListener = new ConsumeResponseListener()
        {
            @Override
            public void onConsumeResponse(@BillingResponse int responseCode, String purchaseToken)
            {
                m_BillingUpdatesListener.onConsumeFinished(purchaseToken, responseCode);
            }
        };

        Runnable consumeRequest = new Runnable()
        {
            @Override
            public void run()
            {
                m_BillingClient.consumeAsync(purchaseToken, onConsumeListener);
            }
        };

        executeServiceRequest(consumeRequest);
    }

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
            startServiceConnection(runnable);
        }
    }

    private boolean verifyValidSignature(String signedData, String signature)
    {
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