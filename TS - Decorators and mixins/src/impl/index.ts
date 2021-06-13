import { Frozen } from "./decorator";
import { Record } from "./mixin";

// using a decorator

@Frozen()
export class Point {
    constructor(public x: number, public y: number) { }
    someFn() { }
}

// using a mixin

class Circle {
    constructor(public x: number, public y: number, public radius: number) { }
    someFn() { }
}

export const RecordCircle = Record(Circle);