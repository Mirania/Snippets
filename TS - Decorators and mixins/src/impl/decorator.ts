// example definition of a decorator
// decorators CAN mutate a class but the ts compiler won't figure that out

type Class = new (...args: any[]) => {};
 
/** freezes all fields of instances of this class */
export function Frozen() {
    return function decorator<T extends Class>(Class: T): T {
        return class extends Class {
            constructor(...args: any[]) {
                super(...args);
                Object.freeze(this);
            }
        }
    }
}