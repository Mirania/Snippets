import { CompleteTerminalOperation, ShortcutTerminalOperation } from "../operation";

/**
 * example terminal operation that consumes elements
 */
export class ForEachOperation<T> extends CompleteTerminalOperation<T, void> {

    private action: (element: T) => void;

    public constructor(action: (element: T) => void) {
        super();
        this.action = action;
    }

    public receive(element: T): void {
        this.action(element);
    }

}

/**
 * example terminal operation that returns a result
 */
export class CountOperation<T> extends CompleteTerminalOperation<T, number> {

    public constructor() {
        super();
        this.result = 0;
    }

    public receive(): void {
        this.result++;
    }

}

/**
 * example terminal operation that returns a result
 */
export class MinOperation<T> extends CompleteTerminalOperation<T, T> {

    private comparator: (a: T, b: T) => number;

    public constructor(comparator: (a: T, b: T) => number) {
        super();
        this.comparator = comparator;
        this.result = null;
    }

    public receive(element: T): void {
        if (this.result === null) {
            this.result = element;
        } else {
            this.result = this.comparator(this.result, element) > 0 ? element : this.result;
        }
    }

}

/**
 * example terminal operation that returns a result
 */
export class MaxOperation<T> extends CompleteTerminalOperation<T, T> {

    private comparator: (a: T, b: T) => number;

    public constructor(comparator: (a: T, b: T) => number) {
        super();
        this.comparator = comparator;
        this.result = null;
    }

    public receive(element: T): void {
        if (this.result === null) {
            this.result = element;
        } else {
            this.result = this.comparator(this.result, element) < 0 ? element : this.result;
        }
    }

}

/**
 * example terminal operation that returns a result
 */
export class ReduceOperation<T> extends CompleteTerminalOperation<T, T> {

    private reducer: (a: T, b: T) => T;

    public constructor(reducer: (a: T, b: T) => T, initialValue?: T) {
        super();
        this.reducer = reducer;
        this.result = initialValue ?? null;
    }

    public receive(element: T): void {
        if (this.result === null) {
            this.result = element;
        } else {
            this.result = this.reducer(this.result, element);
        }
    }

}

/**
 * example terminal operation that returns a result
 */
export class AverageOperation extends CompleteTerminalOperation<number, number> {

    private count: number;

    public constructor() {
        super();
        this.result = null;
        this.count = 0;
    }

    public receive(element: number): void {
        if (this.result === null) {
            this.result = element;
        } else {
            this.result += element;
        }
        this.count++;
    }

    public acknowledgeDone(): void {
        this.result /= this.count;
        super.acknowledgeDone();
    }

}

/**
 * example terminal operation that short-circuits and returns a result
 */
export class AllMatchOperation<T> extends ShortcutTerminalOperation<T, boolean> {

    private condition: (element: T) => boolean;
    
    public constructor(condition: (element: T) => boolean) {
        super();
        this.condition = condition;
        this.result = true;
    }

    public check(element: T): boolean {
        if (!this.condition(element)) {
            this.result = false;
            return true;
        } else return false;
    }

}

/**
 * example terminal operation that short-circuits and returns a result
 */
export class NoneMatchOperation<T> extends ShortcutTerminalOperation<T, boolean> {

    private condition: (element: T) => boolean;

    public constructor(condition: (element: T) => boolean) {
        super();
        this.condition = condition;
        this.result = true;
    }

    public check(element: T): boolean {
        if (this.condition(element)) {
            this.result = false;
            return true;
        } else return false;
    }

}

/**
 * example terminal operation that short-circuits and returns a result
 */
export class AnyMatchOperation<T> extends ShortcutTerminalOperation<T, boolean> {

    private condition: (element: T) => boolean;

    public constructor(condition: (element: T) => boolean) {
        super();
        this.condition = condition;
        this.result = false;
    }

    public check(element: T): boolean {
        if (this.condition(element)) {
            this.result = true;
            return true;
        } else return false;
    }

}

/**
 * example terminal operation that short-circuits and returns a result
 */
export class FindAnyOperation<T> extends ShortcutTerminalOperation<T, T> {

    private condition: (element: T) => boolean;

    public constructor(condition: (element: T) => boolean) {
        super();
        this.condition = condition;
        this.result = null;
    }

    public check(element: T): boolean {
        if (this.condition(element)) {
            this.result = element;
            return true;
        } else return false;
    }

}