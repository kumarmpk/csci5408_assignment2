import controller.InstanceController;

import java.util.Scanner;

public class StartApplication {

    public static void main(String[] args){

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the approach you want to try: ");
        System.out.println("    1. No updates will happen all code in one block. ");
        System.out.println("    2. Transaction 1 will pass through and 2 will fail. ");
        System.out.println("    3. Threads will be created for each transaction and will be executed in sequence." +
                " One will wait for another to complete. ");
        System.out.println("    4. Threads will be created for each transaction and will be executed in random." +
                " One will wait for another to complete. ");
        System.out.println("    5. Distributed transactions. ");
        int choice = scan.nextInt();

        InstanceController instanceController = new InstanceController();
        switch (choice) {
            case 1:
                try {
                    System.out.println("Type the zipcode of the user: ");
                    int zipCode = scan.nextInt();
                    System.out.println("Type the city name of the transaction 1: ");
                    String toCityT1 = scan.next();
                    System.out.println("Type the city name of the transaction 2: ");
                    String toCityT2 = scan.next();
                    instanceController.modifyValue1(zipCode, toCityT1, toCityT2);
                } catch (Exception e) {
                    System.out.println("Exception in approach 1" + e);
                }
                break;

            case 2:
                try {
                    System.out.println("Type the zipcode of the user: ");
                    int zipCode = scan.nextInt();
                    System.out.println("Type the city name of the transaction 1: ");
                    String toCityT1 = scan.next();
                    System.out.println("Type the city name of the transaction 2: ");
                    String toCityT2 = scan.next();
                    instanceController.modifyValue2(zipCode, toCityT1, toCityT2);
                } catch (Exception e) {
                    System.out.println("Exception in approach 2" + e);
                }
                break;

            case 3:
                try {
                    System.out.println("Type the zipcode of the user: ");
                    int zipCode = scan.nextInt();
                    System.out.println("Type the city name of the transaction 1: ");
                    String toCityT1 = scan.next();
                    System.out.println("Type the city name of the transaction 2: ");
                    String toCityT2 = scan.next();
                    instanceController.modifyValue3(zipCode, toCityT1, toCityT2);
                } catch (Exception e) {
                    System.out.println("Exception in approach 2" + e);
                }
                break;

            case 4:
                try {
                    System.out.println("Type the zipcode of the user: ");
                    int zipCode = scan.nextInt();
                    System.out.println("Type the city name of the transaction 1: ");
                    String toCityT1 = scan.next();
                    System.out.println("Type the city name of the transaction 2: ");
                    String toCityT2 = scan.next();
                    instanceController.threadClssLogic(zipCode, toCityT1, toCityT2);
                } catch (Exception e) {
                    System.out.println("Exception in approach 3" + e);
                    e.printStackTrace();
                }
                break;

            case 5:
                try {
                    instanceController.distributedTransaction();
                } catch (Exception e) {
                    System.out.println("Exception in approach 3" + e);
                    e.printStackTrace();
                }
                break;

            default:
                System.out.println("Wrong choice. Try again.");
                break;
        }
        System.out.println("Applicaiton completed.");
    }

}
