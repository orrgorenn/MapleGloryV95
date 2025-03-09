package mapleglory.database;

public interface ActiveMachineAccessor {
    int checkActiveInstances(String machineId);
    boolean addNewInstance(int accountId, String machineId, String ipAddress);
    boolean removeInstance(int accountId);
    void clearInstances();
}
