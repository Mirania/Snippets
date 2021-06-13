// example definition of a mixin

type Class = new (...args: any[]) => {};
type RecordClass = new (...args: any[]) => Record;

export interface Record {
    /** shallow field comparison */
    equals: (other: any) => boolean;
}

export function Record<T extends Class>(Class: T): T & RecordClass {
    return class extends Class {
        constructor(...args: any[]) {
            super(...args);
            Object.freeze(this);
        }

        equals(other: any): boolean {
            return this.constructor === other.constructor &&
                   Object.keys(this).every(key => this[key] === other[key]);
        }
    }
}