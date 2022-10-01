package com.pingu.driverapp.di.module;

import com.pingu.driverapp.ui.home.operation.deliverysignature.photo.CameraActivity;
import com.pingu.driverapp.ui.home.pending.arrival_at_hub.PendingArrivalAtHubActivity;
import com.pingu.driverapp.ui.profile.changepassword.ChangePasswordActivity;
import com.pingu.driverapp.ui.orders.completed.CompletedListActivity;
import com.pingu.driverapp.ui.forgotpassword.ForgotPasswordActivity;
import com.pingu.driverapp.ui.orders.history.HistoryListActivity;
import com.pingu.driverapp.ui.login.LoginActivity;
import com.pingu.driverapp.ui.main.MainActivity;
import com.pingu.driverapp.ui.main.MainActivityFragmentProvider;
import com.pingu.driverapp.ui.notification.NotificationActivity;
import com.pingu.driverapp.ui.home.operation.deliverysignature.DeliverySignatureActivity;
import com.pingu.driverapp.ui.home.operation.barcodescanner.ScanBarcodeActivity;
import com.pingu.driverapp.ui.home.operation.deliverysignature.signature.DigitalSignatureActivity;
import com.pingu.driverapp.ui.home.operation.pickup.PickupActivity;
import com.pingu.driverapp.ui.home.operation.problematicparcel.ProblematicParcelActivity;
import com.pingu.driverapp.ui.home.pending.delivery.PendingDeliveryListActivity;
import com.pingu.driverapp.ui.home.pending.delivery.details.DeliveryDetailsActivity;
import com.pingu.driverapp.ui.home.pending.pickup.PendingPickupListActivity;
import com.pingu.driverapp.ui.home.task.PickupListActivity;
import com.pingu.driverapp.ui.home.task.PickupListActivityFragmentProvider;
import com.pingu.driverapp.ui.search.tracking.TrackParcelActivity;
import com.pingu.driverapp.ui.splash.SplashActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = {MainActivityFragmentProvider.class})
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector()
    abstract NotificationActivity contributeNotificationActivity();

    @ContributesAndroidInjector()
    abstract PendingDeliveryListActivity contributePendingDeliveryListActivity();

    @ContributesAndroidInjector()
    abstract PendingArrivalAtHubActivity contributePendingArrivalAtHubActivity();

    @ContributesAndroidInjector()
    abstract PendingPickupListActivity contributePendingPickupListActivity();

    @ContributesAndroidInjector(modules = {PickupListActivityFragmentProvider.class})
    abstract PickupListActivity contributePickupListActivity();

    @ContributesAndroidInjector()
    abstract CompletedListActivity contributeCompletedListActivity();

    @ContributesAndroidInjector()
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector()
    abstract ForgotPasswordActivity contributeForgotPasswordActivity();

    @ContributesAndroidInjector()
    abstract ChangePasswordActivity contributeChangePasswordActivity();

    @ContributesAndroidInjector()
    abstract HistoryListActivity contributeHistoryListActivity();

    @ContributesAndroidInjector()
    abstract PickupActivity contributePickupActivity();

    @ContributesAndroidInjector()
    abstract DeliverySignatureActivity contributeDeliverySignatureActivity();

    @ContributesAndroidInjector()
    abstract ProblematicParcelActivity contributeProblematicParcelActivity();

    @ContributesAndroidInjector()
    abstract ScanBarcodeActivity contributeScanBarcodeActivity();

    @ContributesAndroidInjector()
    abstract DigitalSignatureActivity contributeDigitalSignatureActivity();

    @ContributesAndroidInjector()
    abstract DeliveryDetailsActivity contributeDeliveryDetailsActivity();

    @ContributesAndroidInjector()
    abstract TrackParcelActivity contributeParcelTrackingActivity();

    @ContributesAndroidInjector()
    abstract CameraActivity contributeCameraActivity();

    @ContributesAndroidInjector()
    abstract SplashActivity contributeSplashActivity();
}
