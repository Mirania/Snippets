import { Operation } from "../operation";

export abstract class IteratorOperation<T> extends Operation<T, T> {

    public acknowledgeDone() { /* no-op */ };

    /**
     * Read an element from the stream's source.
     */
    public abstract receive(): void;

}

export class SimpleIteratorOperation<T> extends IteratorOperation<T> {

    private elements: T[];
    private index: number;

    public constructor(elements: Iterable<T>) {
        super();
        this.elements = Array.from(elements);
        this.index = 0;
    }

    public receive(): void {
        if (this.index >= this.elements.length) {
            this.informDone();
        } else {
            this.send(this.elements[this.index++]);
        }
    }

}