import java.time.LocalDate;
import java.util.Scanner;
 
  public class cycleUI {
    private cycleController cC=new cycleController();
      public double logDailyLimit() throws Exception{
        return   cC.logDailyLimit();
      }
  }

  public void setupCycle() throws Exception {

    Scanner sc = new Scanner(System.in);

    System.out.print("Budget: ");
    double budget = sc.nextDouble();

    sc.nextLine();

    System.out.print("Start Date (2026-05-07): ");
    LocalDate start =
            LocalDate.parse(sc.nextLine());

    System.out.print("End Date (2026-06-07): ");
    LocalDate end =
            LocalDate.parse(sc.nextLine());

    cC.createCycle(
            budget,
            start,
            end
    );
}