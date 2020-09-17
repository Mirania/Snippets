// example usage
import { Suggester } from "./autocorrect";

// load english dictionary
const english = new Suggester("resources/en.txt");

// user input
const sentence = "i reallly love bneans";

// analyse each word
for (const word of sentence.split(" ")) {
    if (english.includes(word)) { // check if word exists in the dictionary
        console.log(`${word} -> exists`);
    } else {
        const suggestions = english.suggest(word, 3); // suggest 3 words for each misspelling
        console.log(`${word} -> doesn't exist -> suggestions: ${suggestions}`);
    }
}