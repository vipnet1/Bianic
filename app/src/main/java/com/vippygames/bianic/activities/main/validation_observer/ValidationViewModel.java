package com.vippygames.bianic.activities.main.validation_observer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ValidationViewModel extends ViewModel {
    private final MutableLiveData<ValidationObserveInfo> taskFinishedLiveData = new MutableLiveData<>();

    public LiveData<ValidationObserveInfo> getTaskFinishedLiveData() {
        return taskFinishedLiveData;
    }

    public void setValidationObserveInfo(ValidationObserveInfo validationObserveInfo) {
        taskFinishedLiveData.postValue(validationObserveInfo);
    }
}
