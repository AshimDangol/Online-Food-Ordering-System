package com.library.command;

import com.library.model.Fine;
import java.util.Map;

public class PayFineCommand implements Command {
    private final String fineId;
    private final Map<String, Fine> fines;
    private boolean wasPaid;

    public PayFineCommand(String fineId, Map<String, Fine> fines) {
        this.fineId = fineId;
        this.fines = fines;
    }

    @Override
    public boolean execute() {
        Fine fine = fines.get(fineId);
        if (fine == null) {
            System.out.println("  ERROR: Fine " + fineId + " not found.");
            return false;
        }
        wasPaid = fine.isPaid();
        fine.setPaid(true);
        System.out.println("  Paid Fine: $" + String.format("%.2f", fine.getAmount()));
        return true;
    }

    @Override
    public void undo() {
        Fine fine = fines.get(fineId);
        if (fine != null) {
            fine.setPaid(wasPaid);
            System.out.println("  UNDO: Reverted payment of fine " + fineId);
        }
    }

    @Override
    public String getDescription() {
        return "Pay Fine: " + fineId;
    }
}
