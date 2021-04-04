import { IteratorOperation, SimpleIteratorOperation } from "./operations/iteration";
import { MapOperation, FilterOperation, SortOperation, FlatMapOperation } from "./operations/intermediate";
import { TerminalOperation, ForEachOperation, CountOperation, AllMatchOperation, AnyMatchOperation, FindAnyOperation } from "./operations/terminal";
import { Collector } from "./utils/collectors";

/**
 * Like the Java streams. These are sequential (no parallel streams here).
 */
export class Stream<T> {

    private pipeline: IteratorOperation<T>;

    private constructor(elements: Iterable<T>) {
        this.pipeline = new SimpleIteratorOperation(elements);
    }

    public static of<T>(...elements: T[]): Stream<T> {
        return new Stream(elements);
    }

    private runPipeline<R>(terminal: TerminalOperation<T, R>): R {
        this.pipeline.append(terminal);
        while (!terminal.isPipelineDone()) {
            this.pipeline.tick();
        }
        return terminal.getResult();
    }

    private cast<U>(): Stream<U> {
        return this as unknown as Stream<U>;
    }

    public map<U>(mapper: (element: T) => U): Stream<U> {
        this.pipeline.append(new MapOperation(mapper));
        return this.cast();
    }

    public filter(condition: (element: T) => boolean): Stream<T> {
       this.pipeline.append(new FilterOperation(condition));
       return this;
    }

    public sorted(comparator?: (a: T, b: T) => number): Stream<T> {
        this.pipeline.append(new SortOperation(comparator));
        return this;
    }

    public flatMap<U>(): Stream<U> {
        this.pipeline.append(new FlatMapOperation());
        return this.cast();
    }

    public forEach(action: (element: T) => void): void {
        this.runPipeline(new ForEachOperation(action));
    }

    public count(): number {
        return this.runPipeline(new CountOperation());
    }

    public allMatch(condition: (element: T) => boolean): boolean {
        return this.runPipeline(new AllMatchOperation(condition));
    }

    public anyMatch(condition: (element: T) => boolean): boolean {
        return this.runPipeline(new AnyMatchOperation(condition));
    }

    public findAny(condition: (element: T) => boolean): T {
        return this.runPipeline(new FindAnyOperation(condition));
    }

    public collect<R>(collector: Collector<T, R>): R {
        return this.runPipeline(collector);
    }
    
}