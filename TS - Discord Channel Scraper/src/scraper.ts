import { Client, GatewayIntentBits, Message, Partials, TextChannel } from 'discord.js';

if (process.argv.length < 5) {
    console.error("error: 3 args required: <bot token> <guild id> <channel id>");
    process.exit(1);
}

const client = new Client({
    intents: [
        GatewayIntentBits.Guilds,
        GatewayIntentBits.GuildMessages,
        GatewayIntentBits.MessageContent
    ], partials: [
        Partials.Message,
        Partials.Channel,
        Partials.GuildMember,
        Partials.ThreadMember
    ]
});

client.login(process.argv[2]); // for example: asjkhfhd384jf89938j498._tm3984

// based on https://stackoverflow.com/a/71620968
// note that this starts at the newest message and "scrolls up" to the oldest message.
async function processAllMessages(action: (message: Message<true>) => Promise<void>) {
    const guild = await client.guilds.fetch(process.argv[3]); // for example: 012930821409823908490
    const channel = await guild.channels.fetch(process.argv[4]) as TextChannel; // for example: 012930821409823908490

    // Create message pointer
    let message = await channel.messages
        .fetch({ limit: 1 })
        .then(async messagePage => {
            const pointer = (messagePage.size === 1 ? messagePage.at(0) : null);
            if (pointer) await action(pointer);
            return pointer;
        });

    while (message) {
        await channel.messages
            .fetch({ limit: 100, before: message.id })
            .then(async messagePage => {
                await Promise.all(messagePage.map(msg => action(msg)));

                // Update our message pointer to be the last message on the page of messages
                message = 0 < messagePage.size ? messagePage.at(messagePage.size - 1) : null;
            });
    }

    process.exit(0);
}

processAllMessages(msg => console.log(msg.content));
