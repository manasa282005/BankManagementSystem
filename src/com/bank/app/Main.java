
package com.bank.app;
import java.util.Scanner;
import com.bank.app.AccountDAO;
import com.bank.app.BankAccount;
import com.bank.util.PinUtil;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        AccountDAO dao = new AccountDAO();
        int choice;

        do {
            System.out.println("\n===== BANK MANAGEMENT SYSTEM =====");
            System.out.println("1. Create Account");
            System.out.println("2. Check Balance");
            System.out.println("3. Deposit Money");
            System.out.println("4. Withdraw Money");
            System.out.println("5. Transaction History");
            System.out.println("6. Login/Verify PIN");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    sc.nextLine();

                    System.out.print("Enter account holder name: ");
                    String name = sc.nextLine();

                    System.out.print("Create a PIN: ");
                    String pin = sc.nextLine();

                    System.out.print("Enter initial deposit: ");
                    double balance = sc.nextDouble();
                    String hashedPin = PinUtil.hashPin(pin);
                    boolean created = dao.createAccount(name, hashedPin, balance);

                    if (created) {
                        System.out.println("Account created successfully!");
                    } else {
                        System.out.println("Account creation failed!");
                    }
                    break;


                case 2:
                    System.out.print("Enter account ID: ");
                    int id = sc.nextInt();

                    BankAccount account = dao.findAccount(id);

                    if (account != null) {
                        System.out.println("Account Holder: "
                                + account.getHolderName());
                        System.out.println("Current Balance: "
                                + account.getBalance());
                    } else {
                        System.out.println("Account not found!");
                    }
                    break;


                case 3:
                    System.out.print("Enter account ID: ");
                    int depositId = sc.nextInt();

                    System.out.print("Enter deposit amount: ");
                    double depositAmount = sc.nextDouble();

                    boolean deposited = dao.deposit(depositId, depositAmount);

                    if (deposited) {
                        System.out.println("Money deposited successfully!");
                    } else {
                        System.out.println("Deposit failed! Check account ID and amount.");
                    }
                    break;


                case 4:
                    System.out.print("Enter account ID: ");
                    int withdrawId = sc.nextInt();

                    System.out.print("Enter withdrawal amount: ");
                    double withdrawAmount = sc.nextDouble();

                    boolean withdrawn = dao.withdraw(withdrawId, withdrawAmount);

                    if (withdrawn) {
                        System.out.println("Money withdrawn successfully!");
                    } else {
                        System.out.println(
                                "Withdrawal failed! Check account ID, amount, and balance.");
                    }
                    break;


                case 5:
                    System.out.print("Enter account ID: ");
                    int historyId = sc.nextInt();

                    dao.showTransactions(historyId);
                    break;

                case 6:
                    System.out.print("Enter account ID: ");
                    int loginId = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter PIN: ");
                    String enteredPin = sc.nextLine();

                    String storedHash = dao.getStoredPinHash(loginId);

                    if (storedHash != null &&
                            PinUtil.verifyPin(enteredPin, storedHash)) {
                        System.out.println("Login successful!");
                    } else {
                        System.out.println("Invalid account ID or PIN.");
                    }
                    break;

                case 7:
                    System.out.println("Thank you for using our bank!");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 7);

        sc.close();
    }
}
