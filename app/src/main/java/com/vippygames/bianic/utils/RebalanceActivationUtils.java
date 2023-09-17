package com.vippygames.bianic.utils;

import android.content.Context;

import com.vippygames.bianic.rebalancing.schedule.RebalancingAlarm;

/*
Logic of when to start foreground service, when to start alar, when to cancel them, change settings and so on
may be pretty complex. The aim of this class is to handle it.
 */
public class RebalanceActivationUtils {
    private final Context context;

    public RebalanceActivationUtils(Context context) {
        this.context = context;
    }

    /*
    if was working as expected before redirect to regular function, if not redirect to function of handling first time.
     */
    public void changeRebalancerIfNeeded(int previousValidationInterval, int previousIsRebalancingActivated,
                                         int newValidationInterval, int newIsRebalancingActivated) {
        BootUtils bootUtils = new BootUtils(context);
        if (bootUtils.haveCurrentBootTime()) {
            changeRebalancer(previousValidationInterval, previousIsRebalancingActivated, newValidationInterval, newIsRebalancingActivated);
        } else {
            bootUtils.setCurrentBootTime();
            changeRebalancerAfterBoot(newValidationInterval, newIsRebalancingActivated);
        }
    }

    /*
    First handling, currently rebalancer deactivated. So just activate it if it should be according to new settings.
    For public call only from BootReceiver.
    */
    public void changeRebalancerAfterBoot(int newValidationInterval, int newIsRebalancingActivated) {
        RebalancingAlarm rebalancingAlarm = new RebalancingAlarm(context);

        if (newIsRebalancingActivated == 1) {
            rebalancingAlarm.startAlarm(newValidationInterval);
            rebalancingAlarm.startRebalancingService();
        }
    }

    /*
    Was handled before, so alarm may run or not run according to settings.
    If changed just the validation interval restart alarm with new value. If activated start alarm and service.
    If deactivated cancel alarm and service.
    */
    private void changeRebalancer(int previousValidationInterval, int previousIsRebalancingActivated,
                                  int newValidationInterval, int newIsRebalancingActivated) {
        RebalancingAlarm rebalancingAlarm = new RebalancingAlarm(context);

        if (newIsRebalancingActivated == previousIsRebalancingActivated) {
            // changed validation interval only
            if (newIsRebalancingActivated == 1 && previousValidationInterval != newValidationInterval) {
                rebalancingAlarm.cancelAlarm();
                rebalancingAlarm.startAlarm(newValidationInterval);
            }
        } else {
            if (newIsRebalancingActivated == 1) { // activated
                rebalancingAlarm.startAlarm(newValidationInterval);
                rebalancingAlarm.startRebalancingService();
            } else { // deactivated
                rebalancingAlarm.cancelAlarm();
                rebalancingAlarm.stopRebalancingService();
            }
        }
    }
}
