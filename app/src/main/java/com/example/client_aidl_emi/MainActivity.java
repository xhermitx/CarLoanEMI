package com.example.client_aidl_emi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import server_package.EMIInterface;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText principalAmountInput, downPaymentInput, interestRateInput, loanTermInput;
    private Button calculateButton, clearButton;
    private TextView emiResult;

    private EMIInterface emiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        principalAmountInput = findViewById(R.id.principalAmountInput);
        downPaymentInput = findViewById(R.id.downPaymentInput);
        interestRateInput = findViewById(R.id.interestInput);
        loanTermInput = findViewById(R.id.loanTermInput);
        emiResult = findViewById(R.id.emiResult);

        calculateButton = findViewById(R.id.calculateButton);
        clearButton = findViewById(R.id.clearButton);

        calculateButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);


        bindToAIDLService();
    }

    private void bindToAIDLService() {

        Intent aidlServiceIntent = new Intent("connection_to_aidl_service");


        bindService(convertImplicitIntentToExplicitIntent(aidlServiceIntent, this),
                serviceConnectionObject, BIND_AUTO_CREATE);

    }

    ServiceConnection serviceConnectionObject = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            emiInterface = server_package.EMIInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}

    };

    public static Intent convertImplicitIntentToExplicitIntent(Intent implicitIntent,
                                                               Context context) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentServices(implicitIntent, 0);

        if (resolveInfoList == null || resolveInfoList.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfoList.get(0);
        ComponentName component = new ComponentName(serviceInfo.serviceInfo.packageName,
                serviceInfo.serviceInfo.name);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    @Override
    public void onClick(View v) {
        double principalAmount = Double.parseDouble(principalAmountInput.getText().toString());
        double downPayment = Double.parseDouble(downPaymentInput.getText().toString());
        double interestRate = Double.parseDouble(interestRateInput.getText().toString());
        double loanTerm = Double.parseDouble(loanTermInput.getText().toString());
        switch (v.getId()) {
            case R.id.calculateButton:
                calculateMethod(principalAmount, downPayment, interestRate, loanTerm);
                break;
            case R.id.clearButton:
                clearMethod();
                break;
            default:
                Log.i("Error", "Default Case");
        }
    }

    private void calculateMethod(double principalAmount, double downPayment, double interestRate,
                                 double loanTerm) {
        if (String.valueOf(principalAmount).isEmpty() ||
                String.valueOf(downPaymentInput).isEmpty() ||
                String.valueOf(interestRateInput).isEmpty() ||
                String.valueOf(loanTermInput).isEmpty()) {

            Toast.makeText(this, "Enter all the values", Toast.LENGTH_SHORT).show();
        } else {
            try {
                double emiFinal = emiInterface.calculateEMI(principalAmount, downPayment,
                        interestRate, loanTerm);
                emiResult.setText(String.valueOf(emiFinal));
            } catch (RemoteException re) {
                re.printStackTrace();
            }
        }
    }

    private void clearMethod() {
        principalAmountInput.setText(null);
        interestRateInput.setText(null);
        downPaymentInput.setText(null);
        loanTermInput.setText(null);
        emiResult.setText(null);
    }
}