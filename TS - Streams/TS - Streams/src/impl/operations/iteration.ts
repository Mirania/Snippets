import { IteratorOperation } from "../operation";

/**
 * example operation that reads an iterable
 */
export class SimpleIteratorOperation<T> extends IteratorOperation<T> {

    private elements: T[];
    private index: number;

    public constructor(elements: Iterable<T>) {
        super();
        this.elements = Array.from(elements);
        this.index = 0;
    }

    public supply(): void {
        if (this.index >= this.elements.length) {
            this.informDone();
        } else {
            this.send(this.elements[this.index++]);
        }
    }

}

/**
 * example operation that uses a generator function
 */
export class InfiniteGeneratorOperation<T> extends IteratorOperation<T> {

    private generator: () => T;

    public constructor(generator: () => T) {
        super();
        this.generator = generator;
    }

    public supply(): void {
        this.send(this.generator());
    }

}

/**
 * example operation that produces a set amount of elements
 */
export class RangeIteratorOperation extends IteratorOperation<number> {

    private minInclusive: number;
    private maxInclusive: number;
    private step: number;

    public constructor(minInclusive: number, maxInclusive: number, step?: number) {
        super();
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
        this.step = step ?? 1;
    }

    public supply(): void {
        if (this.minInclusive > this.maxInclusive) {
            this.informDone();
        } else {
            this.send(this.minInclusive);
            this.minInclusive += this.step;
        }
    }

}