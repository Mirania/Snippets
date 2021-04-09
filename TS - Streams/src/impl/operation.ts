import { StreamWorker } from "./worker";

/**
 * A pipeline operation.
 * @param T type of the stream's elements before the operation
 * @param U type of the stream's elements after the operation
 */
export abstract class Operation<T, U> {

    protected isDone: boolean;

    public next: Operation<U, any>;

    /**
     * Append an operation to the end of the pipeline.
     */
    public append(op: Operation<U, any>): void {
        if (!this.next) {
            this.next = op;
        } else {
            this.next.append(op);
        }
    }

    /**
     * Send an element to the next operation in the chain.
     */
    public send(element: U): void {
        this.next.receive(element);
    }

    /**
     * Receive an element from the previous operation in the chain.
     */
    public abstract receive(element?: T): void;

    /**
     * Inform the next operation that the current operation has finished processing all elements.
     */
    public informDone(): void {
        this.isDone = true;
        this.next.acknowledgeDone();
    }

    /**
     * Acknowledge that the previous operation in the chain has finished processing all elements.
     */
    public abstract acknowledgeDone(): void;

}

/**
 * a terminal operation - this constitutes the end of the operation chain
 * @param T type of the stream's elements before the operation
 * @param R type of the final result
 */
export abstract class TerminalOperation<T, R> extends Operation<T, R> {

    protected pipelineIsDone: boolean;
    protected result: R;

    public isPipelineDone(): boolean {
        return this.pipelineIsDone;
    }

    public getResult(): R {
        return this.result;
    }

    public acknowledgeDone(): void {
        this.pipelineIsDone = true;
    }

    /**
     * receives an element from the previous operation.
     */
    public abstract receive(element: T): void;

}

/**
 * a terminal operation that always processes every element of the stream.
 * any class that extends this one only needs to implement:
 * 
 * - a constructor
 * - the `receive(element)` method
 * 
 * @param T type of the stream's elements before the operation
 * @param R type of the final result
 */
export abstract class CompleteTerminalOperation<T, R> extends TerminalOperation<T, R> { }

/**
 * a terminal operation that may short-circuit and finish execution without processing the entire stream.
 * any class that extends this one only needs to implement:
 *
 * - a constructor
 * - the `check(element)` method
 * 
 * @param T type of the stream's elements before the operation
 * @param R type of the final result 
 */
export abstract class ShortcutTerminalOperation<T, R> extends TerminalOperation<T, R> {

    public receive(element: T): void {
        if (this.check(element)) {
            this.acknowledgeDone();
        }
    }

    /**
     * handles stream elements one at a time. it should return `true` when the operation should 
     * end, and `false` otherwise. when `true`, it should also set the stream's result, if applicable.
     */
    protected abstract check(element: T): boolean;

}

/**
 * a terminal operation - this constitutes a member of the operation chain between the first and the last
 * 
 * @param T type of the stream's elements before the operation
 * @param U type of the stream's elements after the operation
 */
export abstract class IntermediateOperation<T, U> extends Operation<T, U> { 

    /**
     * receives an element from the previous operation.
     */
    public abstract receive(element: T): void;

}

/**
 * an intermediate operation that always processes elements of the stream one by one.
 * any class that extends this one only needs to implement:
 *
 * - a constructor
 * - the `receive(element)` method
 * 
 * @param T type of the stream's elements before the operation
 * @param U type of the stream's elements after the operation
 */
export abstract class IndividualIntermediateOperation<T, U> extends Operation<T, U> {

    public acknowledgeDone(): void {
        this.informDone();
    }

}

/**
 * an intermediate operation that only processes stream elements once it has received them all.
 * any class that extends this one only needs to implement:
 *
 * - a constructor
 * - the `receive(element)` method (optional) - defaults to pushing the element to `this.elements`
 * - the `processAll(elements)` method (optional) - defaults to doing nothing
 * 
 * @param T type of the stream's elements before the operation
 * @param U type of the stream's elements after the operation
 */
export abstract class BlockingIntermediateOperation<T, U> extends IntermediateOperation<T, U> {

    private worker: StreamWorker<T>;

    protected elements: any[];

    public constructor(stream: StreamWorker<T>) {
        super();
        this.worker = stream;
        this.elements = [];
    }

    public receive(element: T): void {
        this.elements.push(element);
    }

    public acknowledgeDone(): void {
        const processed = this.processAll ? this.processAll() : this.elements;
        this.worker.unblock(processed, this);
    }

    /**
     * handles all stream elements (`this.elements`) at once and returns the new stream elements.
     */
    protected processAll?(): Iterable<T>;

}

/**
 * an iterator operation - this constitutes the beginning of the operation chain.
 * any class that extends this one only needs to implement:
 *
 * - a constructor
 * - the `supply()` method
 * 
 * @param T type of the stream's elements
 */
export abstract class IteratorOperation<T> extends Operation<T, T> {

    public acknowledgeDone() { /* no-op */ };

    public receive(): void {
        this.supply();
    }

    /**
     * sends an element to the operation chain.
     */
    public abstract supply(): void;

}