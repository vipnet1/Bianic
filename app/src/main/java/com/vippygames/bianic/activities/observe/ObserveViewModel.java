package com.vippygames.bianic.activities.observe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ObserveViewModel extends ViewModel {
    private final MutableLiveData<ObserveInfo> taskFinishedLiveData = new MutableLiveData<>();

    public LiveData<ObserveInfo> getTaskFinishedLiveData() {
        return taskFinishedLiveData;
    }

    public void setObserveInfo(ObserveInfo observeInfo) {
        taskFinishedLiveData.setValue(observeInfo);
    }

    public void postObserveInfo(ObserveInfo observeInfo) {
        taskFinishedLiveData.postValue(observeInfo);
    }
}
