import { TerminalOperation, BlockingIntermediateOperation, IntermediateOperation, IteratorOperation } from "./operation";
import * as IterOp from "./operations/iteration";

export class StreamWorker<T> {

    private pipeline: IteratorOperation<T>;
    private closed: boolean;

    private constructor(source: IteratorOperation<T>) {
        this.pipeline = source;
        this.closed = false;
    }

    public static withIterator<T>(iterator: IteratorOperation<T>): StreamWorker<T> {
        return new StreamWorker(iterator);
    }

    public static withElements<T>(elements: Iterable<T>): StreamWorker<T> {
        return new StreamWorker(StreamWorker.getDefaultIterator(elements));
    }

    private static getDefaultIterator<T>(elements: Iterable<T>): IteratorOperation<T> {
        return new IterOp.SimpleIteratorOperation(elements);
    }

    /**
     * continue progression of the stream after running a blocking intermediate operation
     */
    public unblock(elements: Iterable<T>, pipelineHead: BlockingIntermediateOperation<T, any>): void {
        this.pipeline = StreamWorker.getDefaultIterator(elements);
        this.pipeline.next = pipelineHead.next;
        pipelineHead.next = this.pipeline;
    }

    public addToPipeline<U>(operation: IntermediateOperation<T, U>) {
        this.pipeline.append(operation);
    }

    public runPipeline<R>(terminal: TerminalOperation<T, R>): R {
        if (this.closed) {
            throw new Error("Cannot reopen a stream");
        }

        this.pipeline.append(terminal);
        while (!terminal.isPipelineDone()) {
            this.pipeline.supply();
        }
        this.closed = true;
        return terminal.getResult();
    }
    
}