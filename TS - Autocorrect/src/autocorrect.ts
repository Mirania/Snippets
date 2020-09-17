import * as fs from "fs";
const lev = require("js-levenshtein") as (a: string, b: string) => number;

export namespace Suggester {

    export type Dictionary = {
        [length: number]: string[],
        min: number,
        max: number
    };

}

type Word = { word: string, dist: number };

export class Suggester {

    private dict: Suggester.Dictionary = { min: Infinity, max: -1 };

    constructor(words: string[]);
    constructor(dictionary: Suggester.Dictionary);
    constructor(path: string, encoding?: string);
    constructor(arg: string[] | Suggester.Dictionary | string, encoding = "utf-8") { 
        let wordList: string[];

        if (typeof arg === "string") {
            wordList = fs.readFileSync(arg, encoding).split("\n");
        } else if (Array.isArray(arg)) {
            wordList = arg;
        } else {
            this.dict = arg;
            return;
        }

        wordList.forEach(word => {
            if (!this.dict[word.length]) {
                this.dict[word.length] = [];
                this.dict.min = Math.min(this.dict.min, word.length);
                this.dict.max = Math.max(this.dict.max, word.length);
            }

            this.dict[word.length].push(word);
        });
    }

    public suggest(word: string, amount = 3): string[] {
        if (amount<=0) throw Error("Amount of suggestions must be greater than 0.");

        const maxRange = Math.max(word.length - this.dict.min, this.dict.max - word.length);
        const targets: Word[] = [];

        outer: for (let searchRange of getSearchRange()) {
            const list = this.dict[word.length + searchRange];
            const absoluteRange = Math.abs(searchRange);
            
            // checks to break the infinitely expanding searchRange loop
            if (absoluteRange > maxRange) break;
            if (!list) continue;

            for (let i=0, length=list.length; i<length; i++) {
                const dist = lev(word, list[i]);

                // add if something worthwhile was found, but do not add self
                if (dist !== 0 && (targets.length < amount || peek(targets).dist > dist))
                    targets.push({word: list[i], dist});

                targets.sort(compareDistances);

                // if we're at amount+1, delete highest dist word (last index)
                if (targets.length > amount) targets.pop();

                // optimizations
                if (targets.length === amount &&
                    (peek(targets).dist === 1 || peek(targets).dist <= absoluteRange)) {
                    break outer;
                }
            }
        }

        return targets.map(target => target.word);
    }

    public distance(word1: string, word2: string): number {
        return lev(word1, word2);
    }

    public includes(word: string) {
        return this.dict[word.length]!==undefined && this.dict[word.length].includes(word);
    }

    public hasDictionary() {
        return this.dict.max !== -1;
    }

    public getDictionary(): Suggester.Dictionary {
        return this.dict;
    }

}

// slowly expands. 0, -1, 1, -2, 2, -3, 3, ...
function *getSearchRange(): IterableIterator<number> {
    let range = 0;
    yield range;

    while (true) {
        yield (range = range < 0 ? -range : -range - 1);
    }
}

function compareDistances(a: Word, b: Word): number {
    return a.dist - b.dist;
}

function peek<T>(array: T[]): T {
    return array[array.length - 1];
}