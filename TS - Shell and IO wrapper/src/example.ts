// example usage
import { IOUtils, ShellWrapper } from "./wrappers";

const main = async () => {
    const badExitCode = await ShellWrapper.run("non-existent-command");
    console.log("Exited with code:", badExitCode);
    const goodExitCode = await ShellWrapper.run("dir");
    console.log("Exited with code:", goodExitCode);

    const input = await IOUtils.prompt("Type something -> ");
    console.log(`Got input: '${input}'`);

    console.log("Paused... press any key to finish.");
    await IOUtils.pause();
    process.exit(0);
}

main();
