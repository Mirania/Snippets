/**
 * A loot table holds a list of items and selects a random
 * one based on a given set of rules.
 * @param T Type of the items.
 */
export abstract class LootTable<T> {

    protected _keys: number[];
    protected _values: T[];

    protected constructor() {
        this._keys = [];
        this._values = [];
    }
    
    /**
     * Returns a randomly selected item.
     */
    public abstract roll(): T;

    protected locate(value: number): T {
        let idx = Math.floor(this._keys.length / 2);
        if (this._keys[idx] < value) {
            do { idx++; } while (this._keys[idx] < value);
        } else {
            while(this._keys[idx - 1] >= value) { idx--; }
        }
        return this._values[idx];
    }
}

/**
 * A loot table that supports unequal probability distributions.
 * Each item can have a *weight* assigned to them. The probability
 * of an item being rolled is its weight divided by the sum of all weights.
 * @param T Type of the items.
 */
export class WeightedLootTable<T> extends LootTable<T> {

    private _max: number;
    
    /**
    * Each item can have a *weight* assigned to them. The probability
    * of an item being rolled is its weight divided by the sum of all weights.
    * 
    * Weights must be values greater than 0.
    */
    public constructor(entries: {weight: number, loot: T}[]) {
        super();
        let sum = 0;
        entries.forEach(entry => {
            if (entry.weight <= 0)
                throw new Error(`Got weight ${entry.weight}. All weights must be greater than 0.`);

            sum += entry.weight;
            this._keys.push(sum);
            this._values.push(entry.loot);
        });
        this._max = sum;
    }

    public roll(): T {
        if (this._keys.length === 0)
            return null;

        return this.locate(randomInt(1, this._max));
    }

}

/**
 * A loot table that supports unequal probability distributions.
 * Each item can have a *probability* assigned to them. The probability
 * value of an item is its exact chance of being rolled.
 * @param T Type of the items.
 */
export class FractionalLootTable<T> extends LootTable<T> {

    /**
    * Each item can have a *probability* assigned to them. The probability
    * value of an item is its exact chance of being rolled.
    * 
    * Probabilities must be values between 0 (exclusive) and 1 (inclusive).
    * 
    * The sum of all probabilities must not exceed 1. If the sum is less than 1,
    * the remainder of the table will be filled with `null` (effectively an empty roll).
    */
    public constructor(entries: { probability: number, loot: T }[]) {
        super();
        let sum = 0;
        entries.forEach(entry => {
            if (entry.probability <= 0 || entry.probability > 1)
                throw new Error(`Got probability ${entry.probability}. Value must be between 0 (exclusive) and 1 (inclusive).`);

            sum += entry.probability;
            if (sum > 1)
                throw new Error("Sum of probabilities must not exceed 1.");

            this._keys.push(sum);
            this._values.push(entry.loot);
        });
        if (sum < 1) {
            this._keys.push(1);
            this._values.push(null);
        }
    }

    public roll(): T {
        if (this._keys.length === 0)
            return null;

        return this.locate(Math.random());
    }
}

/**
 * A loot table where every item is equally likely to be rolled.
 * @param T Type of the items.
 */
export class EqualLootTable<T> extends LootTable<T> {

    /**
    * Every item will be equally likely to be rolled.
    */
    public constructor(entries: T[]) {
        super();
        this._values = entries;
    }

    public roll(): T {
        if (this._values.length === 0)
            return null;

        return this._values[randomInt(0, this._values.length - 1)];
    }

}

// both inclusive
function randomInt(min: number, max: number) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}