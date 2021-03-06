package de.goldendeveloper.backup.discord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.goldendeveloper.backup.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.*;
import java.util.Date;

public class Events extends ListenerAdapter {

    @Override
    public void onShutdown(@NotNull ShutdownEvent e) {
        if (Main.getDeployment()) {
            WebhookEmbedBuilder embed = new WebhookEmbedBuilder();
            embed.setAuthor(new WebhookEmbed.EmbedAuthor(Main.getDiscord().getBot().getSelfUser().getName(), Main.getDiscord().getBot().getSelfUser().getAvatarUrl(), "https://Golden-Developer.de"));
            embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "Offline"));
            embed.addField(new WebhookEmbed.EmbedField(false, "Gestoppt als", Main.getDiscord().getBot().getSelfUser().getName()));
            embed.addField(new WebhookEmbed.EmbedField(false, "Server", Integer.toString(Main.getDiscord().getBot().getGuilds().size())));
            embed.addField(new WebhookEmbed.EmbedField(false, "Status", "\uD83D\uDD34 Offline"));
            embed.addField(new WebhookEmbed.EmbedField(false, "Version", Main.getDiscord().getProjektVersion()));
            embed.setFooter(new WebhookEmbed.EmbedFooter("@Golden-Developer", Main.getDiscord().getBot().getSelfUser().getAvatarUrl()));
            embed.setTimestamp(new Date().toInstant());
            embed.setColor(0xFF0000);
            new WebhookClientBuilder(Main.getConfig().getDiscordWebhook()).build().send(embed.build()).thenRun(() -> System.exit(0));
        }
    }


    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        e.getJDA().getPresence().setActivity(Activity.playing("/help | " + e.getJDA().getGuilds().size() + " Servern"));
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        e.getJDA().getPresence().setActivity(Activity.playing("/help | " + e.getJDA().getGuilds().size() + " Servern"));
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        User _Coho04_ = e.getJDA().getUserById("513306244371447828");
        User nick = e.getJDA().getUserById("428811057700536331");
        String cmd = e.getName();
        if (cmd.equalsIgnoreCase(Discord.cmdShutdown)) {
            if (e.getUser() == nick || e.getUser() == _Coho04_) {
                e.getInteraction().reply("Der " + e.getJDA().getSelfUser().getName() + " wird nun heruntergefahren").queue();
                e.getJDA().shutdown();
            } else {
                e.getInteraction().reply("Dazu hast du keine Rechte, du musst f??r diesen Befehl der Bot Inhaber sein!").queue();
            }
        } else if (cmd.equalsIgnoreCase(Discord.cmdHelp)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("**Help Commands**");
            embed.setColor(Color.MAGENTA);
            for (Command cm : Main.getDiscord().getBot().retrieveCommands().complete()) {
                embed.addField("/" + cm.getName(), cm.getDescription(), true);
            }
            embed.setFooter("@Golden-Developer", e.getJDA().getSelfUser().getAvatarUrl());
            e.getInteraction().replyEmbeds(embed.build()).addActionRow(
                    Button.link("https://wiki.Golden-Developer.de/", "Online ??bersicht"),
                    Button.link("https://support.Golden-Developer.de", "Support Anfragen")
            ).queue();
        } else if (cmd.equalsIgnoreCase(Discord.cmdRestart)) {
            if (e.getUser() == nick || e.getUser() == _Coho04_) {
                try {
                    e.getInteraction().reply("Der Discord Bot wird nun neugestartet!").queue();
                    Process p = Runtime.getRuntime().exec("screen -AmdS " + Main.getDiscord().getProjektName() + " java -Xms1096M -Xmx1096M -jar " + Main.getDiscord().getProjektName() + "-" + Main.getDiscord().getProjektVersion() + ".jar");
                    p.waitFor();
                    e.getJDA().shutdown();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                e.getInteraction().reply("Dazu hast du keine Rechte, du musst f??r diesen Befehl der Bot Inhaber sein!").queue();
            }
        } else if (cmd.equalsIgnoreCase(Discord.cmdBackup)) {
            File file = ExportImport.Export(e.getGuild());
            e.reply("Bitte das Backup gut aufbewahren!").addFile(file).queue(m -> {
                File f = new File("ServerBackup-" + e.getGuild().getId() + ".gd");
                if (!f.delete()) {
                    System.out.println("ERROR: Failed to delete the file: " + "ServerBackup-" + e.getGuild().getId() + ".gd");
                }
            });
        } else if (cmd.equalsIgnoreCase(Discord.cmdImport)) {
            if (e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                ExportImport.Import(e.getGuild(), e.getTextChannel());
            }
        }
    }
}