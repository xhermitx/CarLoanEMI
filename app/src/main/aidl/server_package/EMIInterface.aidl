// EMIInterface.aidl
package server_package;

interface EMIInterface {
   double calculateEMI(double principalAmount, double downPayment, double interestRate, double loanTerm);
}