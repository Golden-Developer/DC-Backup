package de.goldendeveloper.backup;

import de.goldendeveloper.backup.discord.Discord;

public class Main {

    private static Discord discord;
    private static Config config;

    private static Boolean restart = false;
    private static Boolean deployment = true;

    public static void main(String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("restart")) {
            restart = true;
        }
        if (System.getProperty("os.name").split(" ")[0].equalsIgnoreCase("windows")) {
            deployment = false;
        }
        config = new Config();
        discord = new Discord(config.getDiscordToken());
    }

    public static Config getConfig() {
        return config;
    }

    public static Discord getDiscord() {
        return discord;
    }

    public static Boolean getRestart() {
        return restart;
    }

    public static Boolean getDeployment() {
        return deployment;
    }
}
