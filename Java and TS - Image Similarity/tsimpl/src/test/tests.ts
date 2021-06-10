import { compare } from "../../built/impl/imgsim";
import * as fs from "fs";

const a = fs.readFileSync("imgs/1.png");
const b = fs.readFileSync("imgs/2.png");
const c = fs.readFileSync("imgs/3.png");
const d = fs.readFileSync("imgs/4.jpg");
const e = fs.readFileSync("imgs/5.png");

compare(a, b).then(res => console.log("a <=> b:", res));
compare(a, c).then(res => console.log("a <=> c:", res));
compare(a, d).then(res => console.log("a <=> d:", res));
compare(a, e).then(res => console.log("a <=> e:", res));