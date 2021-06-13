import { Point, RecordCircle as Circle } from "../impl/index";

console.log("Decorator:")
const deco = new Point(10, 20);
console.log(deco);
try { deco.x = 123; } catch (e) { console.log("Error:", e.message); }

console.log("Mixin:")
const rec = new Circle(1, 2, 3);
const rec2 = new Circle(1, 2, 3);
const rec3 = new Circle(1, 2, 10);
console.log(rec);
console.log(rec.equals(rec2), rec.equals(rec3), rec.equals([]));