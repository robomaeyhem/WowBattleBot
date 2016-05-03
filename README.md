# WowBattleBot
An IRC Bot that can play Pokemon

# Music
To listen to the music feature, go here https://discord.gg/0uMaFWa51QM5My1E and join the Voice Channel.

# Commands
Command parameters with `<>` are required, parameters with `[]` are optional. Commands seperated by a `|` do the same thing.

| Command  | Purpose |
| ------------- | ------------- |
| `!battle`  | Puts you in a battle with a Random Pokemon and gives you a Random Pokemon to fight.  |
| `!randbat <@username> [num]`  | Challenges `username` to a battle. Change `num` to a number between 1 and 6 for a Multi-Pokemon battle.  |
| `!safari` | Puts you in a Safari battle with a Random pokemon. Gives you a rock, bait, and Pokeballs to catch the random Pokemon. |
| `!changeclass <class> | !switchclass <class>`| Changes your Trainer Class. Cannot be "Gym Leader", "Champion", "Elite Four" or any other protected class.
| `!list` | Gets a list of your current Pokemon in battles with more than 1 Pokemon per team. |
| `!switch<number>` | Switches your Pokemon to the `number` specified. `number` is gotten from the `!list` command. |
| `!check<number>` | Checks the Pokemon `number` specified. `number` is gotten from the `!list` command. |
| `!move<number>` | Uses the Move from your Pokemon against your opponent. |
| `!run` | Forefeits the current battle. If in a Wild Pokemon battle, if the Wild Pokemon outspeeds your Pokemon, the Wild Pokemon will attack before you run.|

# Authors and Contributors

Original Program created by the_chef1337. frumpy4 has contributed significanty.

Feel free to add/modify/fix the code above.

# Bots

If you would like, you can code a bot that can respond to matches through the `!randbat` command. If you have a favorite language, even if it is not Java, go ahead and code a bot and get in contact with the_chef1337 on Twitch.
