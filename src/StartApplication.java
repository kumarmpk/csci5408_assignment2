import controller.InstanceController;

import java.util.Scanner;

public class StartApplication {

    public static void main(String[] args){

        Scanner scan = new Scanner(System.in);
        System.out.println("Type the zipcode of the user: ");
        int zipCode = scan.nextInt();
        System.out.println("Type the city name of the transaction 1: ");
        String toCityT1 = scan.next();
        System.out.println("Type the city name of the transaction 2: ");
        String toCityT2 = scan.next();
        System.out.println("Enter the approach you want to try: ");
        System.out.println("    1. No updates will happen all code in one block. ");
        System.out.println("    2. Transaction 1 will pass through and 2 will fail. ");
        System.out.println("    2. Transaction 1 will pass through and 2 will fail. ");

        try {
            /*controller.Controller controller = new controller.Controller();
            db.DatabaseDetail databaseDetail = controller.getDatabaseInstance(zipCode);*/
            InstanceController instanceController = new InstanceController();
            try {
                //instanceController.modifyValue1(zipCode, toCityT1, toCityT2);
            } catch (Exception e){
                System.out.println("Exception in approach 1"+e);
            }

            try {
                //instanceController.modifyValue2(zipCode, toCityT1, toCityT2);
            } catch (Exception e){
                System.out.println("Exception in approach 2"+e);
            }

            try{
                //instanceController.modifyValue3(zipCode, toCityT1, toCityT2);
            } catch (Exception e){
                System.out.println("Exception in approach 3"+e);
                e.printStackTrace();
            }

            try{
                //instanceController.modifyValue4(zipCode, toCityT1, toCityT2);
            } catch (Exception e){
                System.out.println("Exception in approach 3"+e);
                e.printStackTrace();
            }

            try{
                instanceController.threadClssLogic(zipCode, toCityT1, toCityT2);
            } catch (Exception e){
                System.out.println("Exception in approach 3"+e);
                e.printStackTrace();
            }

        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
        }

        System.out.println("Applicaiton completed.");
    }

}
