export abstract class Operation<T, U> {

    protected isDone: boolean;
    protected next: Operation<U, any>;

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
     * Insert an operation in the pipeline at the position right after
     * the operation that called this function.
     */
    public insert(op: Operation<U, any>): void {
        if (!this.next) {
            this.next = op;
        } else {
            op.next = this.next;
            this.next = op;
        }
    }

    /**
     * Iterate the operation chain.
     */
    public tick(element?: T): void {
        if (this.isDone) {
            this.next.tick();
        } else {
            this.receive(element);
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