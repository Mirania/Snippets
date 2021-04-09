import * as IOp from "./operations/intermediate";
import * as IterOp from "./operations/iteration";
import * as TOp from "./operations/terminal";
import { Collector, Collectors } from "./operations/collectors";
import { StreamWorker } from "./worker";

/**
 * Like the Java streams. These are sequential (no parallel streams here).
 *
 * @param T type of the elements
 */
export class Stream<T> {

    protected worker: StreamWorker<T>;

    protected constructor(worker: StreamWorker<T>) {
        this.worker = worker;
    }

    private cast<U>(): Stream<U> {
        return this as unknown as Stream<U>;
    }

    /* builders */

    public static of<T>(...elements: T[]): Stream<T> {
        return new Stream(StreamWorker.withElements(elements));
    }

    public static generate<T>(generator: () => T): Stream<T> {
        return new Stream(StreamWorker.withIterator(new IterOp.InfiniteGeneratorOperation(generator)));
    }

    public static concat<T>(...streams: Stream<T>[]): Stream<T> {
        const elements = [].concat(...streams.map(s => s.collect(Collectors.toList()))) as T[];
        return new Stream(StreamWorker.withElements(elements));
    }

    public static empty<T>(): Stream<T> {
        return new Stream(StreamWorker.withElements([]));
    }

    /* operations */

    public map<U>(mapper: (element: T) => U): Stream<U> {
        this.worker.addToPipeline(new IOp.MapOperation(mapper));
        return this.cast();
    }

    public filter(condition: (element: T) => boolean): Stream<T> {
        this.worker.addToPipeline(new IOp.FilterOperation(condition));
        return this;
    }

    public sorted(comparator?: (a: T, b: T) => number): Stream<T> {
        this.worker.addToPipeline(new IOp.SortOperation(this.worker, comparator));
        return this;
    }

    public flatMap<U>(unpacker: (element: T) => Iterable<U>): Stream<U> {
        this.worker.addToPipeline(new IOp.FlatMapOperation(this.worker, unpacker));
        return this.cast();
    }

    public concat(...elements: T[]): Stream<T> {
        this.worker.addToPipeline(new IOp.ConcatOperation(this.worker, elements));
        return this;
    }

    public distinct(): Stream<T> {
        this.worker.addToPipeline(new IOp.DistinctOperation(this.worker));
        return this;
    }

    public limit(limit: number): Stream<T> {
        if (limit < 0) {
            throw new Error("Limit must not be negative");
        }
        this.worker.addToPipeline(new IOp.LimitOperation(limit));
        return this;
    }

    public skip(skip: number): Stream<T> {
        if (skip < 0) {
            throw new Error("Skip amount must not be negative");
        }
        this.worker.addToPipeline(new IOp.SkipOperation(skip));
        return this;
    }

    public forEach(action: (element: T) => void): void {
        this.worker.runPipeline(new TOp.ForEachOperation(action));
    }

    public count(): number {
        return this.worker.runPipeline(new TOp.CountOperation());
    }

    public allMatch(condition: (element: T) => boolean): boolean {
        return this.worker.runPipeline(new TOp.AllMatchOperation(condition));
    }

    public anyMatch(condition: (element: T) => boolean): boolean {
        return this.worker.runPipeline(new TOp.AnyMatchOperation(condition));
    }

    public noneMatch(condition: (element: T) => boolean): boolean {
        return this.worker.runPipeline(new TOp.NoneMatchOperation(condition));
    }

    public findAny(condition: (element: T) => boolean): T {
        return this.worker.runPipeline(new TOp.FindAnyOperation(condition));
    }

    public min(comparator: (a: T, b: T) => number): T {
        return this.worker.runPipeline(new TOp.MinOperation(comparator));
    }

    public max(comparator: (a: T, b: T) => number): T {
        return this.worker.runPipeline(new TOp.MaxOperation(comparator));
    }

    public reduce(reducer: (a: T, b: T) => T, initialValue?: T): T {
        return this.worker.runPipeline(new TOp.ReduceOperation(reducer, initialValue));
    }

    public collect<R>(collector: Collector<T, R>): R {
        return this.worker.runPipeline(collector);
    }

}

/**
 * Like IntStream, LongStream and DoubleStream all in one.
 */
export class NumberStream extends Stream<number> {

    /* builder */

    public static range(minInclusive: number, maxInclusive: number, step?:number): NumberStream {
        if (minInclusive > maxInclusive) {
            throw new Error("Min must not be greater than max");
        }
        const pipelineHead = new IterOp.RangeIteratorOperation(minInclusive, maxInclusive, step);
        return new NumberStream(StreamWorker.withIterator(pipelineHead));
    }

    /* operations */

    public average(): number {
        return this.worker.runPipeline(new TOp.AverageOperation());
    }

    public sum(): number {
        return this.reduce((a, b) => a + b);
    }

}

/**
 * Easier to find the factory methods this way.
 */
export const StreamBuilder = {
    of: Stream.of,
    generate: Stream.generate,
    range: NumberStream.range,
    concat: Stream.concat,
    empty: Stream.empty
}
