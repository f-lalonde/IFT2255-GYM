package com.equipeDix.database;

public class FileTEF {

    private final String firstName;
    private final String lastName;
    private final String proID;
    private final int amountToTransfer;

    public FileTEF(String firstName, String lastName, String proID, int amountToTransfer){
        this.firstName = firstName;
        this.lastName = lastName;
        this.proID = proID;
        this.amountToTransfer = amountToTransfer;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProID() {
        return proID;
    }

    public int getAmountToTransfer() {
        return amountToTransfer;
    }
}
