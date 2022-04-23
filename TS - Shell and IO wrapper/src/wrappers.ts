import { spawn } from "child_process";
import * as readline from "readline";

export class ShellWrapper {

    /**
     * Runs a shell command. Prints any outputs in real time. Sends any inputs (e.g. typing) to the shell in real time.
     * @param command The command to run (e.g. `ls` in Linux).
     * @param args The command arguments (e.g. `-l` in Linux, which would result in `ls -l`).
     * @returns Exit code of the shell command.
     */
    public static run(command: string, ...args: string[]): Promise<number> {
        return new Promise(resolve => {
            const shell = spawn(command, args, { shell: true });
            const pipe = process.stdin.pipe(shell.stdin); // user input will be sent to the shell in real time
            shell.stdout.on('data', (data) => console.log(data.toString())); // print outputs in real time
            shell.stderr.on('data', (data) => console.error(data.toString())); // print errors in real time
            shell.on('close', (code) => { pipe.end(); resolve(code); });
        });
    }

}

export class IOUtils {

    /**
     * Requests user input.
     * @param question Message to display when asking for user input.
     * @returns The inputted text, as a string.
     */
    public static prompt(question: string): Promise<string> {
        return new Promise(resolve => {
            const reader = readline.createInterface({ input: process.stdin, output: process.stdout });
            reader.question(question, (answer) => { reader.close(); resolve(answer); });
        });
    }

    /**
     * Pauses execution until a key is pressed.
     * @param message Message to display while waiting for user input.
     */
    public static pause(): Promise<void> {
        return new Promise(resolve => {
            process.stdin.resume(); process.stdin.setRawMode(true);
            process.stdin.once('data', () => { process.stdin.setRawMode(false); process.stdin.end(); resolve(); });
        });
    }

}