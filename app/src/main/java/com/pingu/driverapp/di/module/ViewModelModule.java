package com.pingu.driverapp.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pingu.driverapp.di.util.ViewModelKey;
import com.pingu.driverapp.ui.home.pending.arrival_at_hub.PendingArrivalAtHubViewModel;
import com.pingu.driverapp.ui.profile.changepassword.ChangePasswordViewModel;
import com.pingu.driverapp.ui.orders.completed.CompletedListViewModel;
import com.pingu.driverapp.ui.forgotpassword.ForgotPasswordViewModel;
import com.pingu.driverapp.ui.orders.history.HistoryListViewModel;
import com.pingu.driverapp.ui.home.HomeViewModel;
import com.pingu.driverapp.ui.login.LoginViewModel;
import com.pingu.driverapp.ui.main.MainViewModel;
import com.pingu.driverapp.ui.notification.NotificationViewModel;
import com.pingu.driverapp.ui.home.operation.deliverysignature.DeliverySignatureViewModel;
import com.pingu.driverapp.ui.home.operation.pickup.PickupViewModel;
import com.pingu.driverapp.ui.home.operation.problematicparcel.ProblematicParcelViewModel;
import com.pingu.driverapp.ui.orders.OrdersViewModel;
import com.pingu.driverapp.ui.home.pending.delivery.PendingDeliveryViewModel;
import com.pingu.driverapp.ui.home.pending.delivery.details.DeliveryDetailsViewModel;
import com.pingu.driverapp.ui.home.pending.pickup.PendingPickupViewModel;
import com.pingu.driverapp.ui.home.task.PickupListViewModel;
import com.pingu.driverapp.ui.home.task.all_tasks.PickupAllTasksViewModel;
import com.pingu.driverapp.ui.home.task.available_tasks.PickupAvailableTasksViewModel;
import com.pingu.driverapp.ui.home.task.my_tasks.PickupMyTasksViewModel;
import com.pingu.driverapp.ui.profile.ProfileViewModel;
import com.pingu.driverapp.ui.search.SearchViewModel;
import com.pingu.driverapp.ui.search.tracking.TrackParcelViewModel;
import com.pingu.driverapp.util.ViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel bindMainViewModel(MainViewModel mainViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    abstract ViewModel bindHomeViewModel(HomeViewModel homeViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel.class)
    abstract ViewModel bindSearchViewModel(SearchViewModel searchViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(OrdersViewModel.class)
    abstract ViewModel bindOrdersViewModel(OrdersViewModel ordersViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel.class)
    abstract ViewModel bindProfileViewModel(ProfileViewModel profileViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NotificationViewModel.class)
    abstract ViewModel bindNotificationViewModel(NotificationViewModel notificationViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PendingDeliveryViewModel.class)
    abstract ViewModel bindPendingDeliveryViewModel(PendingDeliveryViewModel pendingDeliveryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PendingArrivalAtHubViewModel.class)
    abstract ViewModel bindPendingArrivalAtHubViewModel(PendingArrivalAtHubViewModel pendingArrivalAtHubViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PendingPickupViewModel.class)
    abstract ViewModel bindPendingPickupViewModel(PendingPickupViewModel pendingPickupViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PickupListViewModel.class)
    abstract ViewModel bindPickupListViewModel(PickupListViewModel pickupListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ForgotPasswordViewModel.class)
    abstract ViewModel bindForgotPasswordViewModel(ForgotPasswordViewModel forgotPasswordViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ChangePasswordViewModel.class)
    abstract ViewModel bindChangePasswordViewModel(ChangePasswordViewModel changePasswordViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PickupAllTasksViewModel.class)
    abstract ViewModel bindPickupAllTasksViewModel(PickupAllTasksViewModel pickupMyTasksViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PickupAvailableTasksViewModel.class)
    abstract ViewModel bindPickupAvailableTasksViewModel(PickupAvailableTasksViewModel pickupAvailableTasksViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PickupMyTasksViewModel.class)
    abstract ViewModel bindPickupMyTasksViewModel(PickupMyTasksViewModel pickupMyTasksViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CompletedListViewModel.class)
    abstract ViewModel bindCompletedListViewModel(CompletedListViewModel completedListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(HistoryListViewModel.class)
    abstract ViewModel bindHistoryListViewModel(HistoryListViewModel historyListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PickupViewModel.class)
    abstract ViewModel bindPickupViewModel(PickupViewModel pickupViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DeliverySignatureViewModel.class)
    abstract ViewModel bindDeliverySignatureViewModel(DeliverySignatureViewModel deliverySignatureViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProblematicParcelViewModel.class)
    abstract ViewModel bindProblematicParcelViewModel(ProblematicParcelViewModel problematicParcelViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DeliveryDetailsViewModel.class)
    abstract ViewModel bindDeliveryDetailsViewModel(DeliveryDetailsViewModel deliveryDetailsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TrackParcelViewModel.class)
    abstract ViewModel bindParcelTrackingViewModel(TrackParcelViewModel parcelTrackingViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
