import { Operation } from "../operation";

export abstract class TerminalOperation<T, R> extends Operation<T, R> {

    protected pipelineIsDone: boolean;
    protected result: R;

    public isPipelineDone(): boolean {
        return this.pipelineIsDone;
    }

    public getResult(): R {
        return this.result;
    }

}

/**
 * example terminal operation that consumes elements
 */
export class ForEachOperation<T> extends TerminalOperation<T, void> {

    private action: (element: T) => void;

    public constructor(action: (element: T) => void) {
        super();
        this.action = action;
    }

    public receive(element: T): void {
        this.action(element);
    }

    public acknowledgeDone(): void {
        this.pipelineIsDone = true;
    }

}

/**
 * example terminal operation that returns a result
 */
export class CountOperation<T> extends TerminalOperation<T, number> {

    public constructor() {
        super();
        this.result = 0;
    }

    public receive(): void {
        this.result++;
    }

    public acknowledgeDone(): void {
        this.pipelineIsDone = true;
    }

}

/**
 * example terminal operation that short-circuits and returns a result
 */
export class AllMatchOperation<T> extends TerminalOperation<T, boolean> {

    private condition: (element: T) => boolean;
    
    public constructor(condition: (element: T) => boolean) {
        super();
        this.condition = condition;
        this.result = true;
    }

    public receive(element: T): void {
        if (!this.condition(element)) {
            this.result = false;
            this.acknowledgeDone();
        }
    }

    public acknowledgeDone(): void {
        this.pipelineIsDone = true;
    }

}

/**
 * example terminal operation that short-circuits and returns a result
 */
export class AnyMatchOperation<T> extends TerminalOperation<T, boolean> {

    private condition: (element: T) => boolean;

    public constructor(condition: (element: T) => boolean) {
        super();
        this.condition = condition;
        this.result = false;
    }

    public receive(element: T): void {
        if (this.condition(element)) {
            this.result = true;
            this.acknowledgeDone();
        }
    }

    public acknowledgeDone(): void {
        this.pipelineIsDone = true;
    }

}

/**
 * example terminal operation that short-circuits and returns a result
 */
export class FindAnyOperation<T> extends TerminalOperation<T, T> {

    private condition: (element: T) => boolean;

    public constructor(condition: (element: T) => boolean) {
        super();
        this.condition = condition;
        this.result = null;
    }

    public receive(element: T): void {
        if (this.condition(element)) {
            this.result = element;
            this.acknowledgeDone();
        }
    }

    public acknowledgeDone(): void {
        this.pipelineIsDone = true;
    }

}