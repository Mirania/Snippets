import { Stream, StreamBuilder, Collectors } from "../impl/index";

Stream.of(3, 0, 1, 6, 4, 0, 8, 9, 7)
    .filter(n => n !== 0)
    .sorted()
    .limit(3)
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
    .flatMap(n => n)
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

console.log("-".repeat(20));

const distinct = Stream.of(1, 2, 3, 4)
    .concat(3, 4, 5)
    .distinct()
    .collect(Collectors.toList());
console.log(distinct);

console.log("-".repeat(20));

const generated = Stream.generate(Math.random)
    .limit(3)
    .collect(Collectors.toList());
console.log(generated);

console.log("-".repeat(20));

const mult = StreamBuilder.range(1, 4)
    .reduce((a, b) => a * b, 5)
console.log(mult);

console.log("-".repeat(20));

const concat = Stream.concat(Stream.of("a", "b"), Stream.of("c", "d", "e").filter(n => n !== "e"))
    .map(n => ({char: n}))
    .collect(Collectors.toList());
console.log(concat);

console.log("-".repeat(20));

const avg = StreamBuilder.range(0, 100).average();
console.log(avg);