public interface ICycleRepository {
    Cycle getCurrentCycle() throws Exception;
    void insertCycle(Cycle cycle) throws Exception;
    double getTotalBudgetForCycle(Cycle cycle) throws Exception;
}