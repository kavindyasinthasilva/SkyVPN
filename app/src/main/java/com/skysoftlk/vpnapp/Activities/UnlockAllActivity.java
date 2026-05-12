package com.skysoftlk.vpnapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.skysoftlk.vpnapp.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.SkuDetails;
import com.google.common.collect.ImmutableList;
import com.skysoftlk.vpnapp.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UnlockAllActivity extends BaseActivity {

    private Map<String, SkuDetails> skusWithSkuDetails = new HashMap<>();
    private final List<String> allSubs = new ArrayList<>(Arrays.asList(
            Config.all_month_id,
            Config.all_threemonths_id,
            Config.all_sixmonths_id,
            Config.all_yearly_id));

    private MutableLiveData<Integer> all_check = new MutableLiveData<>();
    @BindView(R.id.one_month)
    RadioButton oneMonth;
    @BindView(R.id.three_month)
    RadioButton threeMonth;
    @BindView(R.id.six_month)
    RadioButton sixMonth;
    @BindView(R.id.one_year)
    RadioButton oneYear;

    private BillingClient billingClient;
    ArrayList<TextView> tvPrice = new ArrayList<>();
    private TextView tvPrice1, tvPrice2, tvPrice3, tvPrice4;
    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (purchases != null && !purchases.isEmpty()) {

                    if (purchases.get(0) != null) {
                        Log.v("CHECKBILLING", purchases.get(0).toString());
                        handlePurchase(purchases.get(0).getPurchaseToken());
                    }
                } else {
                    Toast.makeText(UnlockAllActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }
            }
        }
    };


    public void MainActivity (View view){
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_unlock_all);


        tvPrice1 = findViewById(R.id.tvPrice1);
        tvPrice2 = findViewById(R.id.tvPrice2);
        tvPrice3 = findViewById(R.id.tvPrice3);
        tvPrice4 = findViewById(R.id.tvPrice4);

        tvPrice.add(tvPrice1);
        tvPrice.add(tvPrice2);
        tvPrice.add(tvPrice3);
        tvPrice.add(tvPrice4);

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        billingSetup();

        ButterKnife.bind(this);
        all_check.setValue(-1);
        all_check.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer) {
                    case 0:
                        threeMonth.setChecked(false);
                        sixMonth.setChecked(false);
                        oneYear.setChecked(false);
                        break;
                    case 1:
                        oneMonth.setChecked(false);
                        sixMonth.setChecked(false);
                        oneYear.setChecked(false);
                        break;
                    case 2:
                        threeMonth.setChecked(false);
                        oneMonth.setChecked(false);
                        oneYear.setChecked(false);
                        break;
                    case 3:
                        threeMonth.setChecked(false);
                        sixMonth.setChecked(false);
                        oneMonth.setChecked(false);
                        break;

                }
            }
        });

        oneMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) all_check.postValue(0);
            }
        });
        threeMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) all_check.postValue(1);
            }
        });
        sixMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) all_check.postValue(2);
            }
        });
        oneYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) all_check.postValue(3);
            }
        });
    }
    private void billingSetup() {
        if (billingClient.isReady()) {
            for (int i = 0; i < allSubs.size(); i++) {
                queryPrices(allSubs.get(i), tvPrice.get(i));
            }
            return;
        }
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    Log.v("CHECKBILLING", "ready");

                    for (int i = 0; i < allSubs.size(); i++) {
                        queryPrices(allSubs.get(i), tvPrice.get(i));
                    }
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                Log.v("CHECKBILLING", "disconnected");
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                if (!isFinishing()) {
                    finish();
                    Toast.makeText(UnlockAllActivity.this, "Service temporarily unavailable. Please check your Google Play account or try again after some time.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void queryProduct(String productId) {
        if (!billingClient.isReady()) {
            billingSetup();
            return;
        }
        Log.v("CHECKBILLING", "clicked");
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId(productId)
                                                .setProductType(BillingClient.ProductType.SUBS)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> productDetailsList) {

                        Log.v("CHECKBILLING", billingResult.toString());
                        if (!productDetailsList.isEmpty()) {

                            makePurchase(productDetailsList.get(0));

                        } else {
                            Log.e("CHECKBILLING", "onProductDetailsResponse: No products");

                            finish();
                            Toast.makeText(UnlockAllActivity.this, "Sorry, this subscription is currently unavailable", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void queryPrices(String productId, TextView tvPrice) {
        if (!billingClient.isReady()) {
            return;
        }
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId(productId)
                                                .setProductType(BillingClient.ProductType.SUBS)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    @Override
                    public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> productDetailsList) {
                        if (!productDetailsList.isEmpty()) {
                            ProductDetails productDetails = productDetailsList.get(0);

                            List<ProductDetails.SubscriptionOfferDetails> offerDetailsList = productDetails.getSubscriptionOfferDetails();
                            if (offerDetailsList != null && !offerDetailsList.isEmpty()) {
                                // Assume you want the first offer (you can iterate or filter if needed)
                                ProductDetails.SubscriptionOfferDetails offerDetails = offerDetailsList.get(0);
                                List<ProductDetails.PricingPhase> pricingPhases = offerDetails.getPricingPhases().getPricingPhaseList();

                                runOnUiThread(() -> {
                                    if (pricingPhases != null && !pricingPhases.isEmpty()) {
                                        ProductDetails.PricingPhase pricingPhase = pricingPhases.get(0);
                                        String price = pricingPhase.getFormattedPrice(); // 💰 The price string, e.g., "$4.99/month"

                                        tvPrice.setText(price);
                                    }
                                });
                            }
                        }
                    }
                }
        );
    }

    private void makePurchase(ProductDetails productDetails) {
        BillingFlowParams.ProductDetailsParams productDetailsParams = null;
        if (productDetails.getSubscriptionOfferDetails() != null && !productDetails.getSubscriptionOfferDetails().isEmpty()) {
            ProductDetails.SubscriptionOfferDetails offerDetails = productDetails.getSubscriptionOfferDetails().get(0);
            productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerDetails.getOfferToken())
                    .build();
        }

        BillingFlowParams billingFlowParams = null;
        if (productDetailsParams != null) {
            billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(ImmutableList.of(productDetailsParams))
                    .build();
        }

        if (billingFlowParams != null) {
            billingClient.launchBillingFlow(this, billingFlowParams);
        }
    }

    private void handlePurchase(String purchaseToken) {

        AcknowledgePurchaseParams acknowledgePurchaseParams =
                AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        .build();

        AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v("CHECKBILLING", "acknowledged");
                        Config.vip_subscription = true;
                        Config.all_subscription = true;

                        Toast.makeText(UnlockAllActivity.this, "Successfully subscribed!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        };

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
    }

    @OnClick(R.id.all_pur)
    void unlockAll() {
        if (all_check.getValue() != null) {

            switch (all_check.getValue()) {
                case 0:
                    queryProduct(Config.all_month_id);
                    break;
                case 1:
                    queryProduct(Config.all_threemonths_id);
                    break;
                case 2:
                    queryProduct(Config.all_sixmonths_id);
                    break;
                case 3:
                    queryProduct(Config.all_yearly_id);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (billingClient != null) {
            billingClient.endConnection();
        }
        super.onDestroy();
    }
}
