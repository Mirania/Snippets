import { PythonicArray } from "./pythonicArray";

// examples
console.log("-1", new PythonicArray([0, 1, 2, 3, 4, 5, 6, 7])[-1]);
console.log("0:2", new PythonicArray([0, 1, 2, 3, 4, 5, 6, 7])["0:2"]);
console.log("0:2:4", new PythonicArray([0, 1, 2, 3, 4, 5, 6, 7])["0:2:4"]);
console.log("0:2:-1", new PythonicArray([0, 1, 2, 3, 4, 5, 6, 7])["0:2:-1"]);
console.log("-1:2:-1", new PythonicArray([0, 1, 2, 3, 4, 5, 6, 7])["-1:2:-1"]);
console.log(":2", new PythonicArray([0, 1, 2, 3, 4, 5, 6, 7])[":2"]);
console.log(":2:", new PythonicArray([0, 1, 2, 3, 4, 5, 6, 7])[":2:"]);
console.log("2:", new PythonicArray([0, 1, 2, 3, 4, 5, 6, 7])["2:"]);

// examples of other functionality
console.log("Range", PythonicArray.fromRange(0, 5));
console.log("Count", new PythonicArray([0, 0, 1, 1, 0, 0, 2, 2]).count(0));