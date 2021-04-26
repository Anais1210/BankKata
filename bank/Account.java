package bank;

class Account {

    // Attributes

    private String name;
    private int balance;
    private int threshold;
    private boolean suspension;

    // Constructor

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(int balance) { this.balance = balance; }

    public void setThreshold(int threshold) { this.threshold = threshold; }

    public void setSuspension(boolean suspension) { this.suspension = suspension; }

    // Methods

    public String getName() { return name; }

    public int getBalance() { return balance; }

    public int getThreshold() { return threshold; }

    public boolean isSuspension() { return suspension; }

    public String toString() {
        String status = "";
        status = status + this.name + " | " + this.balance + " | " + this.threshold + " | " + this.suspension + "\n";
        return status;
    }
}
