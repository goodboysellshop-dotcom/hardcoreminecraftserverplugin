package com.goyesbro.hardcoreban;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.BanList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class HardcoreBan extends JavaPlugin implements Listener {

    // Сообщение при бане
    private final String banMessage = ChatColor.RED + "Вы погибли!\n" +
            ChatColor.WHITE + "Для восстановления игры на сервере напишите в Telegram нашему менеджеру: " +
            ChatColor.AQUA + "@goyesbro";

    @Override
    public void onEnable() {
        // Регистрация событий смерти
        getServer().getPluginManager().registerEvents(this, this);
        // Регистрация команды разбана
        getCommand("unbanplayer").setExecutor(new UnbanCommand());
        getLogger().info("Плагин HardcoreBan успешно запущен!");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String playerName = player.getName();

        // Добавляем в бан-лист (навсегда)
        Bukkit.getBanList(BanList.Type.NAME).addBan(playerName, banMessage, null, "Server");

        // Выкидываем игрока с сервера с причиной
        player.kickPlayer(banMessage);
        
        getLogger().info("Игрок " + playerName + " погиб и был забанен.");
    }

    // Логика команды /unbanplayer
    public class UnbanCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!sender.hasPermission("hardcoreban.admin")) {
                sender.sendMessage(ChatColor.RED + "У вас недостаточно прав!");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage(ChatColor.YELLOW + "Используйте: /unbanplayer <ник>");
                return true;
            }

            String target = args[0];
            if (Bukkit.getBanList(BanList.Type.NAME).isBanned(target)) {
                Bukkit.getBanList(BanList.Type.NAME).pardon(target);
                sender.sendMessage(ChatColor.GREEN + "Игрок " + target + " разбанен.");
            } else {
                sender.sendMessage(ChatColor.RED + "Игрок " + target + " не найден в списке забаненных.");
            }
            return true;
        }
    }
}
