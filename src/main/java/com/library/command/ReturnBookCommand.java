package com.library.command;

import com.library.model.Loan;
import java.time.LocalDate;
import java.util.Map;

public class ReturnBookCommand implements Command {
    private final String loanId;
    private final Map<String, Loan> loans;
    private String previousStatus;
    private Loan loan;

    public ReturnBookCommand(String loanId, Map<String, Loan> loans) {
        this.loanId = loanId;
        this.loans = loans;
    }

    @Override
    public boolean execute() {
        loan = loans.get(loanId);
        if (loan == null) {
            System.out.println("  ERROR: Loan " + loanId + " not found.");
            return false;
        }
        previousStatus = loan.getStatus();
        loan.setReturnDate(LocalDate.now());
        loan.setStatus("RETURNED");
        System.out.println("  Returned: " + loan.getBook().getTitle() +
                " on " + loan.getReturnDate());
        return true;
    }

    @Override
    public void undo() {
        if (loan != null) {
            loan.setReturnDate(null);
            loan.setStatus(previousStatus);
            System.out.println("  UNDO: Reverted return of " + loan.getBook().getTitle());
        }
    }

    @Override
    public String getDescription() {
        return "Return Book: Loan " + loanId;
    }
}
