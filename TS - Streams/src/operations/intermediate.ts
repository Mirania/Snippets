import { Operation } from "../operation";
import { Stream } from "../stream";
import { SimpleIteratorOperation } from "./iteration";

export abstract class IntermediateOperation<T, U> extends Operation<T, U> {
    /* empty class - for now */
}

/**
 * example operation that removes elements
 */
export class FilterOperation<T> extends IntermediateOperation<T, T> {

    private condition: (element: T) => boolean;

    public constructor(condition: (element: T) => boolean) {
        super();
        this.condition = condition;
    }

    public receive(element: T): void {
        if (this.condition(element)) {
            this.send(element);
        }
    }

    public acknowledgeDone(): void {
        this.informDone();
    }

}

/**
 * example operation that modifies elements
 */
export class MapOperation<T, U> extends IntermediateOperation<T, U> {

    private mapper: (element: T) => U;

    public constructor(mapper: (element: T) => U) {
        super();
        this.mapper = mapper;
    }

    public receive(element: T): void {
        this.send(this.mapper(element));
    }

    public acknowledgeDone(): void {
        this.informDone();
    }

}

/**
 * example operation that waits for previous operation to be done
 */
export class SortOperation<T> extends IntermediateOperation<T, T> {

    private comparator?: (a: T, b: T) => number;
    private elements: T[];

    public constructor(comparator?: (a: T, b: T) => number) {
        super();
        this.comparator = comparator;
        this.elements = [];
    }

    public receive(element: T): void {
        this.elements.push(element);
    }

    public acknowledgeDone(): void {
        this.elements.sort(this.comparator);
        this.insert(new SimpleIteratorOperation(this.elements));
        this.informDone();
    }

}

/**
 * example operation that adds elements to the stream
 */
export class FlatMapOperation<T, U> extends IntermediateOperation<T, U> {

    private elements: U[];

    public constructor() {
        super();
        this.elements = [];
    }

    public receive(element: T): void {
        if (element[Symbol.iterator]) {
            this.elements.push(...element as unknown as U[]);
        } else {
            this.elements.push(element as unknown as U);
        }
    }

    public acknowledgeDone(): void {
        this.insert(new SimpleIteratorOperation(this.elements));
        this.informDone();
    }

}