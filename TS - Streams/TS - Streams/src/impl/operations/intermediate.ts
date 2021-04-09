import { BlockingIntermediateOperation, IndividualIntermediateOperation } from "../operation";
import { StreamWorker } from "../worker";

/**
 * example operation that removes elements
 */
export class FilterOperation<T> extends IndividualIntermediateOperation<T, T> {

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

}

/**
 * example operation that modifies elements
 */
export class MapOperation<T, U> extends IndividualIntermediateOperation<T, U> {

    private mapper: (element: T) => U;

    public constructor(mapper: (element: T) => U) {
        super();
        this.mapper = mapper;
    }

    public receive(element: T): void {
        this.send(this.mapper(element));
    }

}

/**
 * example operation that removes elements
 */
export class LimitOperation<T> extends IndividualIntermediateOperation<T, T> {

    private amount: number;
    private limit: number;

    public constructor(limit: number) {
        super();
        this.amount = 0;
        this.limit = limit;
    }

    public receive(element: T): void {
        if (this.amount++ >= this.limit) {
            this.informDone();
        } else {
            this.send(element);
        } 
    }

}

/**
 * example operation that removes elements
 */
export class SkipOperation<T> extends IndividualIntermediateOperation<T, T> {

    private amount: number;
    private skip: number;

    public constructor(skip: number) {
        super();
        this.amount = 0;
        this.skip = skip;
    }

    public receive(element: T): void {
        if (this.amount++ >= this.skip) {
            this.send(element);
        }
    }

}

/**
 * example operation that waits for previous operation to be done
 */
export class SortOperation<T> extends BlockingIntermediateOperation<T, T> {

    private comparator?: (a: T, b: T) => number;

    public constructor(stream: StreamWorker<T>, comparator?: (a: T, b: T) => number) {
        super(stream);
        this.comparator = comparator;
    }

    public processAll(): Iterable<T> {
        return this.elements.sort(this.comparator);
    }

}

/**
 * example operation that adds elements to the stream
 */
export class FlatMapOperation<T, U> extends BlockingIntermediateOperation<T, U> {

    private unpacker: (element: T) => Iterable<U>;

    public constructor(stream: StreamWorker<T>, unpacker: (element: T) => Iterable<U>) {
        super(stream);
        this.unpacker = unpacker;
    }

    public receive(element: T): void {
        this.elements.push(...this.unpacker(element));
    }

}

/**
 * example operation that adds elements to the stream
 */
export class ConcatOperation<T> extends BlockingIntermediateOperation<T, T> {

    private newElements: Iterable<T>;

    public constructor(stream: StreamWorker<T>, elements: Iterable<T>) {
        super(stream);
        this.newElements = elements;
    }

    public processAll(): Iterable<T> {
        return this.elements.concat(this.newElements);
    }

}

/**
 * example operation that adds elements to the stream
 */
export class DistinctOperation<T> extends BlockingIntermediateOperation<T, T> {

    public constructor(stream: StreamWorker<T>) {
        super(stream);
    }

    public processAll(): Iterable<T> {
        return new Set(this.elements);
    }

}