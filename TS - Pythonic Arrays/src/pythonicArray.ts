/**
 * An array with extra functions. It is capable of supporting negative indexes
 * to describe positions counting from the end of the list (e.g. `array[-1]`).
 * It also supports array slicing (e.g. `array["1:3"]`) - note that the slice has
 * to be passed as a `string`.
 * 
 * PythonicArrays extend the base `Array<T>` type and are considered to be of both 
 * the `T[]` and `PythonicArray<T>` types.
 */
export class PythonicArray<T> extends Array<T> {

    /**
     * Constructs a new PythonicArray from another PythonicArray or a regular array.
     * @param array A PythonicArray or JavaScript array.
     */
    constructor(array?: T[]) {
        super();

        // a 1-length array would trigger the Array(size) constructor
        if (array) this.push(...array);

        const translate = (key: any): any => {
            const index = typeof key === "symbol" ? NaN : Number(key);
            if (!isNaN(index)) return index < 0 ? this.length + index : index;
            else return key;
        };

        // "start?:stop?:step?"
        const slice = (key: string): PythonicArray<T> => {
            const args = key.split(":").map(arg => arg === "" ? undefined : Number(arg));
            const start = args[0] === undefined ? 0 : args[0] < 0 ? translate(args[0]) : args[0];
            const stop = args[1] === undefined ? this.length : args[1] < 0 ? translate(args[1]) : args[1];
            const step = args[2] === undefined ? 1 : args[2];
            
            const sliced = new PythonicArray<T>();
            if ((start > stop && step >= 0) || (start < stop && step <= 0)) return sliced; // return empty
            for (let i = start; (i < stop && step > 0) || (i > stop && step < 0); i += step)
                sliced.push(this[i]);
            return sliced;
        };

        return new Proxy(this, {
            // var = array[x]    ..or..    var = array["x:y:z"]
            get: function (target, key) {
                if (typeof key === "string" && key.includes(":")) return slice(key);
                else return target[translate(key)];
            },
            // array[x] = var
            set: function (target, key, value) {
                target[translate(key)] = value;
                return true;
            },
            // delete array[x]
            deleteProperty: function (target, key) {
                const _key = translate(key), exists = target[_key] !== undefined;
                delete target[key];
                return exists;
            }
        });
    }

    /**
     * Constructs a new PythonicArray containing all integers between a minimum and a maximum.
     * @param min Start of range, inclusive.
     * @param max End of range, exclusive.
     */
    static fromRange(min: number, max: number): PythonicArray<number> {
        const array = new PythonicArray<number>();
        for (let i = Math.min(min, max), limit = Math.max(min, max); i < limit; i++) 
            array.push(i);
        return array;
    }

    /**
     * Gets the last element of the list without removing it.
     */
    peek(): T {
        return this[this.length - 1];
    }

    /**
     * Checks if the list has no elements.
     */
    isEmpty(): boolean {
        return this.length === 0;
    }

    /**
     * Counts the amount of times the input element appears in the list.
     * @param element The element whose occurrences will be counted.
     */
    count(element: T): number {
        return this.reduce((prev, cur) => prev + (cur === element ? 1 : 0), 0);
    }

    /**
     * Clears the PythonicArray, leaving it empty.
     */
    clear(): void {
        while (this.length>0) this.pop();
    }

    /**
     * Performs a single-level comparison, where each element is directly compared.
     * Should be used to compare arrays, PythonicArrays or objects containing only
     * primitive values (e.g. numbers).
     * 
     * This equality test is capable of handling primitives, `null` and `undefined` values.
     * It will **not** compare the elements of nested arrays or objects.
     */
    equals(other: any[]): boolean {
        if (other === null || other === undefined || this.length !== other.length) 
            return false;

        return this.every((value, index) => value === other[index]);
    }

    /**
     * Performs a fully recursive comparison, where each element is recursively compared.
     * Should be used to compare arrays, PythonicArrays or objects containing nested arrays or objects.
     * 
     * This equality test is capable of handling primitives, `null`, `undefined`, nested
     * arrays and object values.
     */
    deepEquals(other: any[]): boolean {
        const testEquality = (a: any[], b: any[]): boolean => {
            if (a === null && b === null)
                return true;
            if (a === null || b === null || b === undefined || a.length !== b.length)
                return false;
            for (const key in a) {
                if ((typeof a[key] !== typeof b[key]) ||
                    (typeof a[key] === "object" && !testEquality(a[key], b[key])) ||
                    (typeof a[key] !== "object" && a[key] !== b[key]))
                    return false;
            }
            return true;
        }

        return testEquality(this, other);
    }
}