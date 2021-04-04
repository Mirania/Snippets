import { Stream } from "./stream";
import { Collectors } from "./utils/collectors";

Stream.of(1, 2, 3, 6, 4, 2, 7, 4, 5, 2)
    .filter(n => n !== 2)
    .sorted()
    .map(n => ({ val: n }))
    .forEach(n => console.log(n))

console.log("-".repeat(20));

const count = Stream.of(1, 10, 100, 1000)
    .filter(n => n > 5)
    .count();
console.log(count);

console.log("-".repeat(20));

const list = Stream.of("a", "b", "c", "d")
    .map(n => n.repeat(5))
    .collect(Collectors.toList())
console.log(list);

console.log("-".repeat(20));

const map = Stream.of("a", "b", "c", "d", "e")
    .filter(n => n !== "e")
    .collect(Collectors.toMap(
        e => e,
        e => e.charCodeAt(0)
    ));
console.log(map);

console.log("-".repeat(20));

const flat = Stream.of([2, 5], [4, 6], [3, 7])
    .flatMap()
    .collect(Collectors.toList())
console.log(flat);

console.log("-".repeat(20));

const all = Stream.of(1, 2, 3, 4)
    .allMatch(n => n > 0)
console.log(all);

console.log("-".repeat(20));

const any = Stream.of(1, 2, 3, 4)
    .anyMatch(n => n > 3)
console.log(any);

console.log("-".repeat(20));

const found = Stream.of({ x: 1 }, { x: 2 }, { x: 3 }, { x: 4 })
    .findAny(n => n.x === 2)
console.log(found);
