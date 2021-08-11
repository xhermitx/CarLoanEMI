package com.example.client_aidl_emi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
//import android.os.RemoteException;

import androidx.annotation.Nullable;

import server_package.EMIInterface;

public class MyService extends Service {
    public MyService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stubObject;
    }
    EMIInterface.Stub stubObject = new EMIInterface.Stub() {
        @Override
        public double calculateEMI(double principalAmount, double downPayment, double interestRate, double loanTerm) {
            double finalEMI;
            principalAmount = principalAmount - downPayment;
            interestRate = interestRate / (12 * 100);
            finalEMI = principalAmount * (interestRate * Math.pow((1 + interestRate), loanTerm)) /
                    (Math.pow((1 + interestRate), loanTerm) - 1);
            finalEMI = finalEMI / 12;
            System.out.println("Final EMI : " + finalEMI);

            return finalEMI;
        }
    };
}