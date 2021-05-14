import { LootTable, WeightedLootTable, FractionalLootTable, EqualLootTable } from "../impl/index";

function rollMultiple(lootTable: LootTable<string>): void {
    const rolls: { [result: string]: number } = {};
    for (let i = 0; i < 1e6; i++) {
        const roll = lootTable.roll();
        rolls[roll] = (rolls[roll] ?? 0) + 1;
    }
    console.log(rolls);
}

const weighted = new WeightedLootTable([
    { weight: 10, loot: "a" },
    { weight: 15, loot: "b" },
    { weight: 20, loot: "c" },
    { weight: 30, loot: "d" }
]);

const fractional = new FractionalLootTable([
    { probability: 0.05, loot: "f" },
    { probability: 0.25, loot: "g" },
    { probability: 0.10, loot: "h" },
    { probability: 0.20, loot: "i" },
]);

const random = new EqualLootTable(["j", "k", "l", "m"]);

rollMultiple(weighted);
rollMultiple(fractional);
rollMultiple(random);