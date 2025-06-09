package org.cb.simplifia.domain.model;

public record Credits(int amount) {

    public static Credits zero() {
        return new Credits(0);
    }

    public Credits add(int amount) {
        return new Credits(this.amount + amount);
    }

    public Credits subtract(int amount) {
        return new Credits(this.amount - amount);
    }

    public boolean isGreaterThanOrEqual(Credits credits) {
        return this.amount >= credits.amount;
    }

}
